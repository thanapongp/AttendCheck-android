package com.example.tanap.attendcheck.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.example.tanap.attendcheck.R;
import com.example.tanap.attendcheck.interfaces.AsyncResponseBoolean;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterTask extends AsyncHttpResponseHandler {

    private static final int HTTP_CONFLICT = 409;
    private static final int HTTP_NOTFOUND = 404;

    private AsyncResponseBoolean responseClass;
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    private Context context;
    private String url;
    private String endPoint;

    public RegisterTask(Context context, AsyncResponseBoolean responseClass) {
        this.responseClass = responseClass;
        this.context = context;

        this.client =  new AsyncHttpClient();
        this.url = context.getResources().getString(R.string.app_server_address);
        this.endPoint = context.getResources().getString(R.string.app_server_register_endpoint);
    }

    public void execute(String username, String password) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("username", username);
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
        dialog.setMessage(context.getResources().getString(R.string.login_msg_registering));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        super.onStart();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        JSONObject jsonResponseBody = null;

        try {
            jsonResponseBody = new JSONObject(new String(responseBody));
            Log.d("Request success", "Body: " + jsonResponseBody.get("uid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.d("Request fail", "Code: " + statusCode);
        Log.d("Request fail", "Code: " + error.getMessage());

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        if (statusCode == HTTP_CONFLICT) {
            showConflictDialog(dialogBuilder);
        }

        if (statusCode == HTTP_NOTFOUND) {
            showNotFoundDialog(dialogBuilder);
        }
    }

    private void showConflictDialog(AlertDialog.Builder dialogBuilder) {
        dialogBuilder.setTitle("ไม่สามารถลงทะเบียนได้")
                     .setMessage("รหัสนักศึกษานี้ได้ทำการลงทะเบียนแล้ว\n" +
                                 "หากต้องการลงทะเบียนอุปกรณ์ใหม่กรุณากดปุ่มลงทะเบียน")
                    .setPositiveButton("ลงทะเบียน", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                     .setNegativeButton("ปิด", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {}
                     });

        dialogBuilder.create().show();
    }

    private void showNotFoundDialog(AlertDialog.Builder dialogBuilder) {
        dialogBuilder.setTitle("ไม่สามารถลงทะเบียนได้")
                .setMessage("ไม่พบรหัสนักศึกษานี้")
                .setNegativeButton("ปิด", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

        dialogBuilder.create().show();
    }

    @Override
    public void onFinish() {
        dialog.dismiss();
    }
}
