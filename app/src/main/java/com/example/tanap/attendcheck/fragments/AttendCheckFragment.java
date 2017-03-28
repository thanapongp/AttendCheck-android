package com.example.tanap.attendcheck.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tanap.attendcheck.R;
import com.example.tanap.attendcheck.db.Attendances;
import com.example.tanap.attendcheck.db.Schedules;
import com.example.tanap.attendcheck.tasks.AttendanceCheckTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AttendCheckFragment extends Fragment implements View.OnClickListener,
        AttendanceCheckTask.AttendanceCheckTaskResponse {
    @BindView(R.id.text_subjectName) TextView subjectText;
    @BindView(R.id.text_roomName) TextView roomText;
    @BindView(R.id.text_attendStatus) TextView statusText;

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

        courseName = schedule.get(0).get("code") + " " + schedule.get(0).get("name");
        courseRoom = schedule.get(0).get("room");

        schedulesTable.closeDB();

        subjectText.setText("วิชา: " + courseName);
        roomText.setText("ห้อง: " + courseRoom);

        Attendances attendancesTable = new Attendances(getContext());
        if (attendancesTable.checkIfAlreadyAttendance(scheduleID)) {
            checkBtn.setClickable(false);
            statusText.setText("คุณเช็คชื่อแล้ว");
            checkBtn.setAlpha(.25f);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.btn_checkBtn) {
            return;
        }

        checkBtn.setClickable(false);
        Log.d("Click", "Clicky click");

        new AttendanceCheckTask(this, getContext(), scheduleID, courseRoom).execute();
    }

    @Override
    public void onAttendanceCheckComplete(boolean successState) {
        Log.d("Method Triggered", "onAttendanceCheckComplete");
        statusText.setText("คุณเช็คชื่อแล้ว");
        checkBtn.setAlpha(.25f);
    }
}
