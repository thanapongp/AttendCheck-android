package com.example.tanap.attendcheck.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.tanap.attendcheck.R;
import com.example.tanap.attendcheck.interfaces.AsyncResponseBoolean;

public class LoginTask extends AsyncTask<String, Void, Boolean> {
    private AsyncResponseBoolean delegate = null;
    private ProgressDialog dialog;
    private Context context;

    public LoginTask(Context context, AsyncResponseBoolean delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage(context.getResources().getString(R.string.login_msg_authenticating));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dialog.dismiss();
        delegate.processFinish(result);
    }
}
