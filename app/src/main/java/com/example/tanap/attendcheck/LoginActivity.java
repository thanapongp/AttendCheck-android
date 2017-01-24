package com.example.tanap.attendcheck;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity implements AsyncResponseBoolean {
    @BindView(R.id.app_logo) TextView logo;
    @BindView(R.id.input_username) EditText inputUsername;
    @BindView(R.id.input_password) EditText inputPassword;
    @BindView(R.id.btn_login) Button loginBtn;
    @BindView(R.id.goToRegisterLink) TextView registerLink;

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

        // set event listener
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    @OnClick(R.id.btn_login)
    public void login() {
        new LoginTask(getApplicationContext(), this).execute();
    }

    @Override
    /**
     * What to do after login process is finished.
     */
    public void processFinish(Boolean result) {
        Toast.makeText(getApplicationContext(), result ? "Success" : "NO!", Toast.LENGTH_SHORT).show();
    }
}
