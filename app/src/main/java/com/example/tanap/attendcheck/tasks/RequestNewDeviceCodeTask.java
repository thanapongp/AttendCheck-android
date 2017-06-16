package com.example.tanap.attendcheck.tasks;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.tanap.attendcheck.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class RequestNewDeviceCodeTask extends AsyncHttpResponseHandler {

    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_NOTFOUND = 404;
    public static final int HTTP_CONFLICT = 409;
    public static final int SUCCESS = 1;

    private final Context context;
    private final AsyncResponseWithStatusCode responseClass;
    private final AsyncHttpClient client;
    private final String url;
    private final String endPoint;

    private ProgressDialog dialog;

    public RequestNewDeviceCodeTask(Context context, AsyncResponseWithStatusCode responseClass) {
        this.context = context;
        this.responseClass = responseClass;

        this.client =  new AsyncHttpClient();
        this.url = context.getResources().getString(R.string.app_server_address);
        this.endPoint = context.getResources().getString(R.string.app_server_requestdevice_endpoint);
    }

    public void execute(String studentID, String password) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("username", studentID);
        requestParams.add("password", password);

        client.setMaxRetriesAndTimeout(0,0);
        client.post(getEndPoint(), requestParams, this);
    }

    private String getEndPoint() {
        return this.url + this.endPoint;
    }

    @Override
    public void onStart() {
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage(context.getResources().getString(R.string.login_msg_requesting));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        super.onStart();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        dialog.dismiss();

        responseClass.processRequestFinish(SUCCESS);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        dialog.dismiss();

        responseClass.processRequestFinish(statusCode);
    }

    public interface AsyncResponseWithStatusCode {
        void processRequestFinish(Integer status);
    }
}
