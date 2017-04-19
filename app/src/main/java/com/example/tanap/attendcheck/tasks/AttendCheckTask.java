package com.example.tanap.attendcheck.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
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

public class AttendCheckTask {

    private AttendCheckTaskResponse responseClass;
    private Context context;
    private Integer scheduleID;

    private ArrayList<HashMap<String, String>> userInfo;

    private MqttAndroidClient client;
    private String clientID;

    private int type;

    public AttendCheckTask(AttendCheckTaskResponse attendCheckFragment,
                           Context context, Integer scheduleID, int type) {

        this.responseClass = attendCheckFragment;
        this.context = context;
        this.scheduleID = scheduleID;
        this.userInfo = new User(context).getUserInfo();
        this.type = type;
    }

    public void execute() {
        connectTorPi();
    }

    private void connectTorPi() {
        final String rPiUri = "tcp://192.168.10.1:1883";
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
                    responseClass.onAttendCheckComplete(false, type);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sendAttendCheckRequest() {
        String requestTopicPrefix = getRequestTopicPrefix();

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
                public void messageArrived(String topic, final MqttMessage message) throws Exception {
                    Log.d("Response Message", String.valueOf(message.toString()));

                    if (! message.toString().contains("error")) {
                        Attendances attendancesTable = new Attendances(context);
                        attendancesTable.attend(scheduleID);
                    }

                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (! message.toString().contains("error")) {
                                responseClass.onAttendCheckComplete(true, type);
                            } else {
                                responseClass.onAttendCheckComplete(false, type);
                            }
                        }
                    };

                    mainHandler.post(runnable);

                    client.disconnect();
                }
            });

        } catch (JSONException | UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private String getRequestTopicPrefix() {
        switch (this.type) {
            case WifiSearchTask.SEARCH_ATTEND:
                return "attendance/request/";

            case WifiSearchTask.SEARCH_CHECKOUT:
                return "attendance/checkout/";
        }
        return null;
    }

    public interface AttendCheckTaskResponse {
        void onAttendCheckComplete(boolean successState, int type);
    }
}
