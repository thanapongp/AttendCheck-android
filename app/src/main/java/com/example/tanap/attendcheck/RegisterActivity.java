package com.example.tanap.attendcheck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tanap.attendcheck.db.DB;
import com.example.tanap.attendcheck.interfaces.AsyncResponseBoolean;
import com.example.tanap.attendcheck.tasks.RegisterTask;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class RegisterActivity extends AppCompatActivity implements AsyncResponseBoolean {
    @BindView(R.id.app_logo) TextView logo;
    @BindView(R.id.btn_register) Button registerBtn;
    @BindView(R.id.registerNewDeviceLink) TextView registerNewDevice;

    @BindView(R.id.input_username) EditText usernameEditText;
    @BindView(R.id.input_password) EditText passwordEditText;
    @BindView(R.id.input_password_confirmation) EditText passwordConfirmEditText;
    @BindView(R.id.input_email) EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (userHasAlreadyLogin()) {
            startMainActivity();
        }

        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        //!!IMPORTANT!! Delete database every time the application start (for testing purpose)
//        SQLiteDatabase db = new DB(getApplicationContext()).getWritableDatabase();
//        db.close();
//        this.deleteDatabase(DB.DATABASE_NAME);

        this.boot();
    }

    private void startMainActivity() {
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private boolean userHasAlreadyLogin() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(
                "login_pref", Context.MODE_PRIVATE
        );

        return pref.getBoolean("already_login", false);
    }

    private void boot() {
        // change logo font
        Typeface ralewayFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-regular.ttf");
        logo.setTypeface(ralewayFont);

        // set event listener
        registerNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterNewDeviceActivity.class));
            }
        });

        // Create DB for the first time and then close it.
        SQLiteDatabase db = new DB(getApplicationContext()).getWritableDatabase();
        db.close();

        validateInputs();
    }

    @OnTextChanged(
            value = {
                    R.id.input_username,
                    R.id.input_password,
                    R.id.input_password_confirmation,
                    R.id.input_email
            },
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED
    )
    public void validateInputs() {
        if (TextUtils.isEmpty(usernameEditText.getText()) ||
            TextUtils.isEmpty(passwordEditText.getText()) ||
            TextUtils.isEmpty(passwordConfirmEditText.getText()) ||
            TextUtils.isEmpty(emailEditText.getText())) {
            registerBtn.setAlpha(.75f);
            registerBtn.setEnabled(false);
        } else {
            registerBtn.setAlpha(1);
            registerBtn.setEnabled(true);
        }
    }

    @OnClick(R.id.btn_register)
    public void registerNewUser() {
        if (! passwordEditText.getText().toString().equals(passwordConfirmEditText.getText().toString())) {
            Log.d("Debug EditText", "Password Edit Text:" + passwordEditText.getText());
            Log.d("Debug EditText", "Password Confirm Edit Text:" + passwordConfirmEditText.getText());
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        new RegisterTask(RegisterActivity.this, this)
        .execute(
                usernameEditText.getText().toString(),
                passwordEditText.getText().toString(),
                emailEditText.getText().toString()
        );
    }

    @Override
    public void processFinish(Boolean result) {
        if (! result) {
            return;
        }

        //Toast.makeText(this, "all done!", Toast.LENGTH_SHORT).show();

        getApplicationContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("already_login", true)
                .apply();

        startMainActivity();
    }
}
