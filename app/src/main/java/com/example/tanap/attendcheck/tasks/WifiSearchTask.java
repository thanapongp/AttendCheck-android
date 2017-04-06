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

public class WifiSearchTask extends AsyncTask<Void, Void, Boolean> {

    private final String rPiUri = "tcp://192.168.10.1:1883";
    private final String requestTopicPrefix = "attendance/request/";

    private String courseRoom;
    private String accesspoint_SSID;

    private Context context;
    private ProgressDialog dialog;

    private MqttAndroidClient client;

    WifiSearchTaskResponse responseClass;
    private String clientID;

    public WifiSearchTask(WifiSearchTaskResponse attendCheckFragment,
                          Context context, Integer scheduleID, String courseRoom) {
        this.courseRoom = courseRoom;
        this.context = context;
        this.responseClass = attendCheckFragment;
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
        dialog.setMessage("ค้นหาอุปกรณ์ Raspberry Pi");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void ...params) {
        return connectToAccessPoint(accesspoint_SSID);
    }

    private boolean connectToAccessPoint(String accesspoint_ssid) {
        WifiConfiguration conf = new WifiConfiguration();
        Log.d("Looking for AP name", "AttendCheck_SC412");

        conf.SSID = "\"" + accesspoint_ssid + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> wifiList = wifiManager.getConfiguredNetworks();
        List<ScanResult> wifiScanResult = wifiManager.getScanResults();

        for (ScanResult wifi : wifiScanResult) {
            Log.d("WiFi scan result", wifi.SSID);
            if (wifi.SSID != null && wifi.SSID.equals(accesspoint_ssid)) {

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

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.dismiss();
        responseClass.onWifiSearchComplete(aBoolean);
    }

    public interface WifiSearchTaskResponse {
        void onWifiSearchComplete(boolean successState);
    }
}
