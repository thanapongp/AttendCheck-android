package com.example.tanap.attendcheck.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.example.tanap.attendcheck.R;
import com.example.tanap.attendcheck.db.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class FectchRecordsTask extends AsyncHttpResponseHandler {

    private final Context context;
    private final AsyncHttpClient client;
    private final String url;
    private final String endPoint;
    private ProgressDialog dialog;

    public FectchRecordsTask(Context context) {
        this.context = context;

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
                R.string.login_msg_requesting_record)
        );
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        super.onStart();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

    }
}
