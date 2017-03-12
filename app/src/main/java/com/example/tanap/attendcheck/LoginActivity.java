package com.example.tanap.attendcheck;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tanap.attendcheck.interfaces.AsyncResponseBoolean;
import com.example.tanap.attendcheck.tasks.LoginTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class LoginActivity extends AppCompatActivity implements AsyncResponseBoolean {
    @BindView(R.id.app_logo) TextView logo;
    @BindView(R.id.input_username) EditText inputUsername;
    @BindView(R.id.input_password) EditText inputPassword;
    @BindView(R.id.btn_login) Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    @OnClick(R.id.btn_login)
    public void login() {
        new LoginTask(LoginActivity.this, this).execute();
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
    /**
     * What to do after login process is finished.
     */
    public void processFinish(Boolean result) {
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
