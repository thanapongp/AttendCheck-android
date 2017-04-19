package com.example.tanap.attendcheck.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tanap.attendcheck.R;
import com.example.tanap.attendcheck.db.Attendances;
import com.example.tanap.attendcheck.db.Schedules;
import com.example.tanap.attendcheck.tasks.AttendCheckTask;
import com.example.tanap.attendcheck.tasks.WifiSearchTask;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AttendCheckFragment extends Fragment
        implements View.OnClickListener,
                   WifiSearchTask.WifiSearchTaskResponse,
                   AttendCheckTask.AttendCheckTaskResponse {
    @BindView(R.id.text_subjectName) TextView subjectText;
    @BindView(R.id.text_roomName) TextView roomText;
    @BindView(R.id.text_attendStatus) TextView statusText;
    @BindView(R.id.instruction_text) TextView instructionText;

    public FloatingActionButton checkBtn;
    private String courseName;
    private String courseRoom;
    private Integer scheduleID;


    public AttendCheckFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attend_check, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        checkBtn = (FloatingActionButton) getView().findViewById(R.id.btn_checkBtn);
        checkBtn.setOnClickListener(this);

        Schedules schedulesTable = new Schedules(getActivity().getApplicationContext());
        ArrayList<HashMap<String, String>> schedule = schedulesTable.getNextOrCurrentSchedule();

        Log.d("Data", schedule.get(0).get("name"));
        Log.d("Data", schedule.get(0).get("code"));
        
        scheduleID = Integer.parseInt(schedule.get(0).get("id"));

        Log.d("Schedule ID", scheduleID.toString());

        courseName = schedule.get(0).get("code") + " " + schedule.get(0).get("name");
        courseRoom = schedule.get(0).get("room");

        schedulesTable.closeDB();

        subjectText.setText("วิชา: " + courseName);
        roomText.setText("ห้อง: " + courseRoom);

        Attendances attendancesTable = new Attendances(getContext());
        if (attendancesTable.checkIfAlreadyAttendance(scheduleID)) {
            statusText.setText("คุณเช็คชื่อแล้ว");
            //checkBtn.setAlpha(.25f);

            checkBtn.setOnClickListener(new CheckOutBtnClickListener());
            checkBtn.setImageResource(R.drawable.ic_exit_to_app_white_24dp);

            instructionText.setText("กดเพื่อเช็คชื่อออก");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.btn_checkBtn) {
            return;
        }

        checkBtn.setClickable(false);
        Log.d("Click", "Clicky click");

        new WifiSearchTask(
                this, getContext(), courseRoom, WifiSearchTask.SEARCH_ATTEND
        ).execute();
    }

    @Override
    public void onWifiSearchComplete(boolean successState, int type) {
        if (successState) {
            new AttendCheckTask(this, getContext(), scheduleID, type).execute();
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

            alertBuilder.setMessage("ไม่พบอุปกรณ์ Raspberry Pi ประจำห้อง")
                        .setTitle("ไม่สามารถเช็คชื่อได้")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });

            AlertDialog dialog = alertBuilder.create();
            dialog.show();
        }
    }

    @Override
    public void onAttendCheckComplete(boolean successState, int type) {
        if (successState) {
            Log.d("Method Triggered", "onAttendanceCheckComplete");
            statusText.setText("คุณเช็คชื่อแล้ว");
            //checkBtn.setAlpha(.25f);

            if (type == WifiSearchTask.SEARCH_ATTEND) {
                checkBtn.setOnClickListener(new CheckOutBtnClickListener());
                checkBtn.setImageResource(R.drawable.ic_exit_to_app_white_24dp);
                checkBtn.setClickable(true);
                instructionText.setText("กดเพื่อเช็คชื่อออก");
            } else if (type == WifiSearchTask.SEARCH_CHECKOUT) {
                checkBtn.setAlpha(.25f);
                checkBtn.setClickable(false);
            }

            WifiManager wifiManager =
                    (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            // prevent WiFi from reconnect itself
            wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
            wifiManager.disconnect();

        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

            alertBuilder.setMessage("มีข้อผิดพลาดเกิดขึ้นระหว่างการเช็คชื่อ \n กรุณาลองใหม่อีกครั้ง")
                    .setTitle("ไม่สามารถเช็คชื่อได้")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });

            AlertDialog dialog = alertBuilder.create();
            dialog.show();

            checkBtn.setClickable(true);
        }
    }

    private class CheckOutBtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            checkBtn.setClickable(false);

            new WifiSearchTask(
                    AttendCheckFragment.this, getContext(), courseRoom,
                    WifiSearchTask.SEARCH_CHECKOUT
            ).execute();
        }
    }
}
