package com.example.tanap.attendcheck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void performAttendanceCheck(View view) {
        Toast.makeText(this, "PRESSED!", Toast.LENGTH_SHORT).show();
    }
}
