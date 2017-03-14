package com.example.tanap.attendcheck;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class RegisterNewDeviceActivity extends AppCompatActivity {
    @BindView(R.id.app_logo) TextView logo;
    @BindView(R.id.input_username) EditText inputUsername;
    @BindView(R.id.input_password) EditText inputPassword;
    @BindView(R.id.btn_login) Button loginBtn;

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
    public void login() {
        Toast.makeText(this, "show popup etc etc", Toast.LENGTH_SHORT).show();
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
}
