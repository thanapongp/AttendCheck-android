package com.example.tanap.attendcheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tanap.attendcheck.tasks.RequestNewDeviceCodeTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class RegisterNewDeviceActivity extends AppCompatActivity
        implements DialogInterface.OnClickListener,
                   RequestNewDeviceCodeTask.AsyncResponseWithStatusCode {
    @BindView(R.id.app_logo) TextView logo;
    @BindView(R.id.input_username) EditText inputUsername;
    @BindView(R.id.input_password) EditText inputPassword;
    @BindView(R.id.btn_login) Button loginBtn;

    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_device);
        ButterKnife.bind(this);

        this.boot();
    }

    private void boot() {
        // change logo font
        Typeface ralewayFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-regular.ttf");
        logo.setTypeface(ralewayFont);

        loginBtn.setAlpha(.75f);
        loginBtn.setEnabled(false);
    }

    @OnClick(R.id.goToRegisterLink)
    public void showRegisterPage() {
        finish();
    }

    @OnClick(R.id.btn_login)
    public void requestCode() {
        new RequestNewDeviceCodeTask(this, this).execute(
                inputUsername.getText().toString(),
                inputPassword.getText().toString()
        );
    }

    @OnTextChanged(
            value = { R.id.input_username, R.id.input_password },
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED
    )
    public void validateInputs() {
        if (TextUtils.isEmpty(inputUsername.getText()) ||
            TextUtils.isEmpty(inputPassword.getText())) {
            loginBtn.setAlpha(.75f);
            loginBtn.setEnabled(false);
        } else {
            loginBtn.setAlpha(1);
            loginBtn.setEnabled(true);
        }
    }

    @Override
    public void processRequestFinish(Integer status) {
        switch (status) {
            case RequestNewDeviceCodeTask.SUCCESS:
                showStatusDialog(
                        "กรุณาเช็คอีเมล์ของท่าน", "ระบบได้ส่งรหัสสำหรับการเปลี่ยนอุปกรณ์ไปให้ท่านแล้ว", new CallBack() {
                            public void call() {
                                displayDialog();
                            }
                        }
                );
                break;
            case RequestNewDeviceCodeTask.HTTP_UNAUTHORIZED:
                showStatusDialog("ไม่สามารถขอรหัสเปลี่ยนอุปกรณ์ได้", "เนื่องจาก Username หรือ Password ผิด");
                break;
            case RequestNewDeviceCodeTask.HTTP_NOTFOUND:
                showStatusDialog("ไม่สามารถขอรหัสเปลี่ยนอุปกรณ์ได้", "เนื่องจากไม่พบผู้ใช้นี้");
                break;
            case RequestNewDeviceCodeTask.HTTP_CONFLICT:
                showStatusDialog("ไม่สามารถขอรหัสเปลี่ยนอุปกรณ์ได้", "เนื่องจากผู้ใช้นี้ยังไม่เคยลงทะเบียนอุปกรณ์ในระบบ");
                break;
        }
    }

    private void showStatusDialog(String title, String message) {
        showStatusDialog(title, message, new CallBack() { public void call() {} });
    }

    private void showStatusDialog(String title, String message, final CallBack callback) {
        AlertDialog.Builder dialogBuilder = getDialogBuilder();

        dialogBuilder.setTitle(title)
                .setMessage(message);

        dialogBuilder.setPositiveButton("ปิด", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callback.call();
                dialog.cancel();
            }
        });

        dialogBuilder.show();
    }

    private AlertDialog.Builder getDialogBuilder() {
        return new AlertDialog.Builder(this);
    }

    @OnClick(R.id.displayDialog)
    public void displayDialog() {
        AlertDialog.Builder dialogBuilder = getDialogBuilder();

        dialogBuilder.setTitle(getResources().getString(R.string.login_displayDialog));

        this.input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        dialogBuilder.setView(input);

        dialogBuilder.setPositiveButton("เปลี่ยนอุปกรณ์", this);

        dialogBuilder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialogBuilder.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Toast.makeText(RegisterNewDeviceActivity.this, input.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    interface CallBack {
        void call();
    }
}
