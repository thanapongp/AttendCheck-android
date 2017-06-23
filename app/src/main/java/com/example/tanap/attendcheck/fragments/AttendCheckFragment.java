package com.example.tanap.attendcheck.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    /**
     * Instance of circular checking button.
     */
    public FloatingActionButton checkBtn;

    /**
     * Course room number. Use to construct the WIFI-SSID.
     */
    private String courseRoom;

    /**
     * The current schedule ID.
     */
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

    /**
     * Create new instance of fragment, initialize necessary variables
     * and fetch the current schedule from SQLite DB.
     *
     * @param view  Instance of view.
     * @param savedInstanceState Saved instance state.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        checkBtn = (FloatingActionButton) getView().findViewById(R.id.btn_checkBtn);
        checkBtn.setOnClickListener(this);

        // Fetch the current schedule from SQLite DB.
        Schedules schedulesTable = new Schedules(getActivity().getApplicationContext());
        ArrayList<HashMap<String, String>> schedule = schedulesTable.getNextOrCurrentSchedule();

        // If no schedule is not found, then do nothing and change the text to inform the user.
        if (schedule.isEmpty()) {
            subjectText.setText("");
            statusText.setText("ไม่พบคาบเรียน");
            roomText.setText("");
            checkBtn.setAlpha(.25f);
            checkBtn.setClickable(false);
            schedulesTable.closeDB();

            return;
        }

        Log.d("Data", schedule.get(0).get("name"));
        Log.d("Data", schedule.get(0).get("code"));
        
        scheduleID = Integer.parseInt(schedule.get(0).get("id"));

        Log.d("Schedule ID", scheduleID.toString());

        String courseName = schedule.get(0).get("code") + " " + schedule.get(0).get("name");

        // Store the course room to use it build the SSID later in the process.
        courseRoom = schedule.get(0).get("room");

        schedulesTable.closeDB();

        subjectText.setText(String.format("วิชา: %s", courseName));
        roomText.setText(String.format("ห้อง: %s", courseRoom));

        // Now we will check if the user already attended the current schedule.
        // If yes, then just switch the button to 'checkout' state.
        Attendances attendancesTable = new Attendances(getContext());
        if (attendancesTable.checkIfAlreadyAttendance(scheduleID)) {
            statusText.setText("คุณเช็คชื่อแล้ว");
            //checkBtn.setAlpha(.25f);

            checkBtn.setOnClickListener(new CheckOutBtnClickListener());
            checkBtn.setImageResource(R.drawable.ic_exit_to_app_white_24dp);

            instructionText.setText("กดเพื่อเช็คชื่อออก");
        }
    }

    /**
     * Perform check when the check button is click.
     *
     * @param v Clicked view.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.btn_checkBtn) {
            return;
        }

        checkBtn.setClickable(false);
        checkBtn.setAlpha(.25f);
        Log.d("Click", "Clicky click");

        // Thanks to Android 6's permission thingy, this is what I have to do to make it work.
        // Requesting location permissions before actually start looking for WIFI SSID.
        int hasLocationPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission_group.LOCATION);
        if (hasLocationPermission == PackageManager.PERMISSION_GRANTED) {
            startWifiSearchTask();
        } else {
            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Debug", "onRequestPermissionsResult: Permission granted");
                    startWifiSearchTask();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * New up the WifiSearchTask and start it.
     */
    private void startWifiSearchTask() {
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

            alertBuilder.setMessage("ไม่พบอุปกรณ์ Raspberry Pi ประจำห้อง \n" +
                    "หากท่านใช้อุปกรณ์ที่เป็น Android 6 ขึ้นไป \n กรุณาเปิด GPS แล้วลองใหม่อีกครั้ง")
                        .setTitle("ไม่สามารถเช็คชื่อได้")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });

            AlertDialog dialog = alertBuilder.create();
            dialog.show();
            checkBtn.setClickable(true);
            checkBtn.setAlpha(1f);
        }
    }

    @Override
    public void onAttendCheckComplete(boolean successState, int type) {
        if (successState) {
            Log.d("Method Triggered", "onAttendanceCheckComplete");
            statusText.setText("คุณเช็คชื่อแล้ว");
            checkBtn.setAlpha(1f);

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
//            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
//
//            alertBuilder.setMessage("มีข้อผิดพลาดเกิดขึ้นระหว่างการเช็คชื่อ \n กรุณาลองใหม่อีกครั้ง")
//                    .setTitle("ไม่สามารถเช็คชื่อได้")
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {}
//                    });
//
//            AlertDialog dialog = alertBuilder.create();
//            dialog.show();

            checkBtn.setClickable(true);
            checkBtn.setAlpha(1f);
        }
    }

    @Override
    public void onScheduleNeedCode() {
        checkBtn.setClickable(true);
        checkBtn.setAlpha(1f);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        dialogBuilder.setTitle("กรุณากรอกรหัสเช็คชื่อ");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        dialogBuilder.setView(input);

        dialogBuilder.setPositiveButton("เช็คชื่อ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AttendCheckTask(
                        AttendCheckFragment.this, getContext(),
                        scheduleID, WifiSearchTask.SEARCH_ATTEND,
                        input.getText().toString()
                ).execute();
            }
        });

        dialogBuilder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialogBuilder.show();
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
