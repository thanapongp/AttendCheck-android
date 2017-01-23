package com.example.tanap.attendcheck;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView logo = (TextView) findViewById(R.id.app_logo);
        Typeface ralewayFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-regular.ttf");
        logo.setTypeface(ralewayFont);
    }
}
