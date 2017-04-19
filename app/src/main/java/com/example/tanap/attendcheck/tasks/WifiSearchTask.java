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

    public static final int SEARCH_ATTEND = 1;
    public static final int SEARCH_CHECKOUT = 2;

    private String courseRoom;
    private String accesspoint_SSID;

    private Context context;
    private ProgressDialog dialog;

    private int searchType;

    private WifiSearchTaskResponse responseClass;

    public WifiSearchTask(WifiSearchTaskResponse attendCheckFragment,
                          Context context, String courseRoom, int type) {
        this.courseRoom = courseRoom;
        this.context = context;
        this.responseClass = attendCheckFragment;
        this.searchType = type;

        accesspoint_SSID = constructAccessPointName(this.courseRoom);
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
        Log.d("Looking for AP name", accesspoint_ssid);

        conf.SSID = "\"" + accesspoint_ssid + "\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        boolean state = false;

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        // if it's already connected to room's accesspoint (for some reason),
        // then, we don't have to scan and reconnect again.
        if (wifiManager.getConnectionInfo().getSSID().contains(accesspoint_ssid)) {
            return true;
        }

        List<WifiConfiguration> wifiList = wifiManager.getConfiguredNetworks();
        List<ScanResult> wifiScanResult = wifiManager.getScanResults();

        for (ScanResult wifi : wifiScanResult) {
            Log.d("WiFi scan result", wifi.SSID);
            if (wifi.SSID != null && wifi.SSID.equals(accesspoint_ssid)) {

                for (WifiConfiguration configWiFi: wifiList) {
                    Log.d("Config WiFi", configWiFi.SSID);
                    if (configWiFi.SSID.trim().equals(conf.SSID)) {
                        wifiManager.disconnect();
                        state = wifiManager.enableNetwork(configWiFi.networkId, true);

                    } else {
                        wifiManager.disableNetwork(configWiFi.networkId);
                    }
                }

                wifiManager.reconnect();

                // delay to let wifi reconnect
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return state;
            }
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.dismiss();
        responseClass.onWifiSearchComplete(aBoolean, this.searchType);
    }

    public interface WifiSearchTaskResponse {
        void onWifiSearchComplete(boolean successState, int type);
    }
}
