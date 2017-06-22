package com.example.tanap.attendcheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.tanap.attendcheck.tasks.FetchRecordsTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AttendanceRecord extends AppCompatActivity implements FetchRecordsTask.AsyncResponseWithJSONObject {

    @BindView(R.id.records_coursename) TextView courseNameView;
    @BindView(R.id.records_list_view) ListView listview;

    private String courseCode;
    private String courseName;

    String[] from = new String[] {
            "date",
            "type",
            "time",
    };

    int[] to = new int[] {
            R.id.item_subjectname,
            R.id.item_time,
            R.id.item_subjectroom
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_record);

        ButterKnife.bind(this);

        this.courseCode = getIntent().getStringExtra("courseCode");
        this.courseName = getIntent().getStringExtra("courseName");
        Toolbar toolbar = (Toolbar) findViewById(R.id.record_toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + getString(R.string.app_name) + "</font>"));


        new FetchRecordsTask(this, this).execute(courseCode);
    }

    @Override
    public void processFinish(ArrayList<HashMap<String, String>> response, int statusCode) {
        if (response == null) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

            dialogBuilder.setTitle("เกิดข้อผิดพลาด")
                    .setMessage("ไม่สามารถแสดงข้อมูลได้ขณะนี้ (Err: " + statusCode + ")");

            dialogBuilder.setPositiveButton("ปิด", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                    dialog.cancel();
                }
            });

            dialogBuilder.show();
            return;
        }

        BaseAdapter adapter = new SimpleAdapter(this, response, R.layout.layout_recordsitem, from, to);

        courseNameView.setText(courseName);

        listview.setAdapter(adapter);
        listview.invalidateViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
