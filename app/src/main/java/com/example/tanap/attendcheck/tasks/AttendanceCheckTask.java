package com.example.tanap.attendcheck.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tanap.attendcheck.db.Attendances;
import com.example.tanap.attendcheck.db.User;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AttendanceCheckTask extends AsyncTask<Void, Void, Boolean> {

    private final String rPiUri = "tcp://192.168.10.1:1883";
    private final String requestTopicPrefix = "attendance/request/";

    private Integer scheduleID;
    private String courseRoom;
    private String accesspoint_SSID;
    private ArrayList<HashMap<String, String>> userInfo;

    private Context context;
    private ProgressDialog dialog;

    private boolean successStatus = false;
    private MqttAndroidClient client;

    AttendanceCheckTaskResponse responseClass;
    private String clientID;

    public AttendanceCheckTask(AttendanceCheckTaskResponse attendCheckFragment,
                               Context context, Integer scheduleID, String courseRoom) {
        this.scheduleID = scheduleID;
        this.courseRoom = courseRoom;
        this.context = context;

        this.responseClass = attendCheckFragment;
        
        userInfo = getUserInfo(context);
        accesspoint_SSID = constructAccessPointName(this.courseRoom);
    }

    private ArrayList<HashMap<String, String>> getUserInfo(Context context) {
        return new User(context).getUserInfo();
    }

    private String constructAccessPointName(String courseRoom) {
        return "AttendCheck_" + courseRoom;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("กำลังทำการเช็คชื่อ");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void ...params) {
        executeTask();

        return successStatus;
    }

    public void executeTask() {
        if (! connectToAccessPoint(accesspoint_SSID)) {
            Log.d("AttendaceCheckTask", "Cant find SSID");
            successStatus = false;
            return;
        }

        connectTorPi();
    }

    private boolean connectToAccessPoint(String accesspoint_ssid) {
        WifiConfiguration conf = new WifiConfiguration();
        Log.d("Looking for AP name", "AttendCheck_SC412");

        //conf.SSID = "\"" + accesspoint_ssid + "\"";
        conf.SSID = "\"" + "AttendCheck_SC412" + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> wifiList = wifiManager.getConfiguredNetworks();
        List<ScanResult> wifiScanResult = wifiManager.getScanResults();

        for (ScanResult wifi : wifiScanResult) {
            Log.d("WiFi scan result", wifi.SSID);
            if (wifi.SSID != null && wifi.SSID.equals("AttendCheck_SC412")) {

                for (WifiConfiguration configWiFi: wifiList) {
                    Log.d("Config WiFi", configWiFi.SSID);
                    if (configWiFi.SSID.trim().equals(conf.SSID)) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(configWiFi.networkId, true);
                        wifiManager.reconnect();

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void connectTorPi() {
        this.clientID = MqttClient.generateClientId();

        MemoryPersistence persistence = new MemoryPersistence();

        client = new MqttAndroidClient(context, rPiUri, clientID, persistence);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(false);
        mqttConnectOptions.setCleanSession(false);

        try {
            client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT Connect status", "OK!");
                    sendAttendCheckRequest();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("MQTT Connect status", "FAILED CODE: " + client.getResultCode());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sendAttendCheckRequest() {
        String username = userInfo.get(0).get("username");

        try {
            JSONObject requestPayload = new JSONObject();
            requestPayload.put("schedule_id", String.valueOf(scheduleID));
            requestPayload.put("username", username);

            MqttMessage payload = new MqttMessage(requestPayload.toString().getBytes("UTF-8"));
            String topic = requestTopicPrefix + this.clientID;

            client.publish(topic, payload);
            Log.d("Payload topic:", topic);

            client.subscribe("attendcheck/response/" + clientID, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d("Response Message", String.valueOf(message.toString()));

                    Attendances attendancesTable = new Attendances(context);
                    attendancesTable.attend(scheduleID, message.toString());

                    client.disconnect();
                }
            });

        } catch (JSONException | UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.dismiss();
        responseClass.onAttendanceCheckComplete(true);
    }

    public interface AttendanceCheckTaskResponse {
        public void onAttendanceCheckComplete(boolean successState);
    }
}
