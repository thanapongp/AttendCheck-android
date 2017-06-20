package com.example.tanap.attendcheck.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.example.tanap.attendcheck.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class GetNewDeviceData extends AsyncHttpResponseHandler {

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

    private final InsertDataToDBTask insertDataToDBTask = new InsertDataToDBTask();

    public GetNewDeviceData(Context context, GetNewDeviceData.AsyncResponseWithStatusCode responseClass) {
        this.context = context;
        this.responseClass = responseClass;

        this.client =  new AsyncHttpClient();
        this.url = context.getResources().getString(R.string.app_server_address);
        this.endPoint = context.getResources().getString(
                R.string.app_server_changedevice_endpoint
        );
    }

    public void execute(String token) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("token", token);

        Log.d("Making request", "To " + url + endPoint);

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
        dialog.setMessage(context.getResources().getString(R.string.login_msg_requesting_newdata));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        super.onStart();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        JSONObject jsonResponseBody;

        try {
            Log.d("Request success", "Try to convert to JSONObject");
            jsonResponseBody = new JSONObject(new String(responseBody));
            Log.d("Request success", "Body: " + jsonResponseBody.toString());

            new InsertDataToDBTask().insertUserDataToDBForTheFirstTime(
                    jsonResponseBody,
                    context,
                    jsonResponseBody.getString("username"),
                    "",
                    jsonResponseBody.getString("email")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.dismiss();

        responseClass.processGetDataFinish(SUCCESS);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        dialog.dismiss();

        responseClass.processGetDataFinish(statusCode);
    }

    public interface AsyncResponseWithStatusCode {
        void processGetDataFinish(Integer status);
    }
}
