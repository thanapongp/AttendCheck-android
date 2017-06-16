package com.example.tanap.attendcheck.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.example.tanap.attendcheck.R;
import com.example.tanap.attendcheck.db.User;
import com.example.tanap.attendcheck.interfaces.AsyncResponseBoolean;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UpdateDataTask extends AsyncHttpResponseHandler {

    private final InsertDataToDBTask insertDataToDBTask = new InsertDataToDBTask();

    private String url;
    private String endPoint;

    private AsyncResponseBoolean responseClass;
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    private Context context;

    public UpdateDataTask(Context context, AsyncResponseBoolean responseClass) {
        this.responseClass = responseClass;
        this.context = context;

        this.client = new AsyncHttpClient();
        this.url = context.getResources().getString(R.string.app_server_address);
        this.endPoint = getURLendPoint();

        this.client.setMaxRetriesAndTimeout(1, AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT);
    }

    private String getURLendPoint() {
        String deviceID = new User(context).getUserInfo().get(0).get("uid");

        String getInfoEndPoint = "/api/user?device=";

        return getInfoEndPoint + deviceID;
    }

    public void execute() {
        Log.d("Making request", "To " + url + endPoint);
        client.get(url + endPoint, this);
    }

    @Override
    public void onStart() {
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage(context.getResources().getString(R.string.login_msg_updating));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        super.onStart();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        JSONObject jsonResponseBody = null;

        try {
            Log.d("Request success", "Try to convert to JSONObject");
            jsonResponseBody = new JSONObject(new String(responseBody));
            Log.d("Request success", "Body: " + jsonResponseBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        InsertDataToDBTask task = new InsertDataToDBTask();

        if (task.updateDataInDB(jsonResponseBody, context)) {
            responseClass.processFinish(true);
        } else {
            responseClass.processFinish(false);
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        dialogBuilder.setTitle("เกิดผิดพลาดระหว่างการอัพเดทข้อมูล")
                .setMessage("ไม่สามารถติดต่อกับ Server ได้")
                .setNegativeButton("ปิด", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

        dialogBuilder.create().show();
    }
}
