package com.example.tanap.attendcheck;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tanap.attendcheck.db.DB;
import com.example.tanap.attendcheck.tasks.RegisterTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * AttendCheck | The application for AttendCheck checking system.
 * See: https://github.com/thanapongp/attend-check-server
 * for server-side application detail and documentation.
 *
 * This class is the main entry point for the application. it will check if
 * the user already logged in to the application or not, if yes, they will
 * be redirected to MainActivity.
 *
 * @author Thanapong Prathumchat
 * @version 1.0
 */
public class RegisterActivity extends AppCompatActivity implements RegisterTask.AsyncResponseWithStatusCode {

    /**
     * Butterknife provides an easy way to bind the view to a variable without a hassle.
     * All we have to do is just tell it what view should be bind to what variable.
     *
     * See: http://jakewharton.github.io/butterknife/ for more detail.
     */
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

        // Check if the user is already logged in, if yes, then we will redirect them to
        // the MainActivity and close this activity.
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

    /**
     * Check if the user is already logged in.
     *
     * @return boolean
     */
    private boolean userHasAlreadyLogin() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(
                "login_pref", Context.MODE_PRIVATE
        );

        return pref.getBoolean("already_login", false);
    }

    /**
     * Close this activity and then start MainActivity
     */
    private void startMainActivity() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Boot the view.
     */
    private void boot() {

        // Change logo font
        Typeface ralewayFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-regular.ttf");
        logo.setTypeface(ralewayFont);

        // Set event listener
        registerNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterNewDeviceActivity.class));
            }
        });

        // Create DB for the first time and then close it.
        SQLiteDatabase db = new DB(getApplicationContext()).getWritableDatabase();
        db.close();

        // Disable the register button by forcing the input validation.
        validateInputs();
    }

    /**
     * Validate input every time the inputs change.
     */
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

    /**
     * Register the new user.
     */
    @OnClick(R.id.btn_register)
    public void registerNewUser() {
        String password = passwordEditText.getText().toString();
        String passwordConfirmation = passwordConfirmEditText.getText().toString();

        if (! validatePassword(password, passwordConfirmation)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        // If everything is good
        new RegisterTask(RegisterActivity.this, this)
        .execute(
                usernameEditText.getText().toString(),
                passwordEditText.getText().toString(),
                emailEditText.getText().toString()
        );
    }

    public boolean validatePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    @Override
    public void processFinish(Integer status) {
        switch (status) {
            case RegisterTask.SUCCESS:
                loginAndRedirectUser();
                break;
            case RegisterTask.HTTP_NOTFOUND:
                showNotFoundDialog();
                break;
            case RegisterTask.HTTP_CONFLICT:
                showConflictDialog();
                break;
        }
    }

    private void loginAndRedirectUser() {
        getApplicationContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("already_login", true)
                .apply();

        startMainActivity();
    }

    private void showConflictDialog() {
        AlertDialog.Builder dialogBuilder = getDialogBuilder();

        dialogBuilder.setTitle("ไม่สามารถลงทะเบียนได้")
                .setMessage("รหัสนักศึกษานี้ได้ทำการลงทะเบียนแล้ว\n" +
                        "หากต้องการลงทะเบียนอุปกรณ์ใหม่กรุณากดปุ่มลงทะเบียน")
                .setPositiveButton("ลงทะเบียน", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(RegisterActivity.this, RegisterNewDeviceActivity.class));
                    }
                })
                .setNegativeButton("ปิด", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
                });

        dialogBuilder.create().show();
    }

    private void showNotFoundDialog() {
        AlertDialog.Builder dialogBuilder = getDialogBuilder();

        dialogBuilder.setTitle("ไม่สามารถลงทะเบียนได้")
                .setMessage("ไม่พบรหัสนักศึกษานี้")
                .setNegativeButton("ปิด", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
                });

        dialogBuilder.create().show();
    }

    private AlertDialog.Builder getDialogBuilder() {
        return new AlertDialog.Builder(RegisterActivity.this);
    }
}
