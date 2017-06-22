package com.example.tanap.attendcheck.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.example.tanap.attendcheck.R;
import com.example.tanap.attendcheck.db.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class FetchRecordsTask extends AsyncHttpResponseHandler {

    public static final int SUCCESS = 1;

    private final Context context;
    private final AsyncHttpClient client;
    private final String url;
    private final String endPoint;
    private final AsyncResponseWithJSONObject responseClass;
    private ProgressDialog dialog;

    public FetchRecordsTask(Context context, AsyncResponseWithJSONObject responseClass) {
        this.context = context;
        this.responseClass = responseClass;

        this.client =  new AsyncHttpClient();
        this.url = context.getResources().getString(R.string.app_server_address);
        this.endPoint = context.getResources().getString(
                R.string.app_server_record_endpoint
        );
    }

    public void execute(String courseCode) {
        String deviceID = new User(context).getUserInfo().get(0).get("uid");

        RequestParams requestParams = new RequestParams();
        requestParams.add("device", deviceID);
        requestParams.add("course", courseCode);

        Log.d("Making request", "To " + url + endPoint);

        client.setMaxRetriesAndTimeout(0,0);
        client.get(getEndPoint(), requestParams, this);
    }

    private String getEndPoint() {
        return this.url + this.endPoint;
    }

    @Override
    public void onStart() {
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage(context.getResources().getString(
                R.string.login_msg_requesting_record));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        super.onStart();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        dialog.dismiss();

        responseClass.processFinish(convertByteToArrayList(responseBody), statusCode);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        dialog.dismiss();

        responseClass.processFinish(null, statusCode);
    }

    private ArrayList<HashMap<String, String>> convertByteToArrayList(byte[] responseBody) {
        try {
            JSONArray array = new JSONArray(new String(responseBody));

            ArrayList<HashMap<String, String>> returnData = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                HashMap<String, String> map = new HashMap<>();

                Iterator keys = object.keys();

                while(keys.hasNext()) {
                    String key = keys.next().toString();
                    map.put(key, object.get(key).toString());
                }

                returnData.add(map);
            }

            return returnData;
        } catch (JSONException e) {
            return null;
        }
    }

    public interface AsyncResponseWithJSONObject {
        void processFinish(ArrayList<HashMap<String, String>> response, int statusCode);
    }
}
