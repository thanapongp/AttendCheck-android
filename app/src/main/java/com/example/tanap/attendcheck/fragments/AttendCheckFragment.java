package com.example.tanap.attendcheck.fragments;

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
import com.example.tanap.attendcheck.db.Schedules;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AttendCheckFragment extends Fragment {
    @BindView(R.id.text_subjectName) TextView subjectText;
    @BindView(R.id.text_roomName) TextView roomText;

    private OnAttendCheckFragmentInteractionListener mListener;

    public FloatingActionButton checkBtn;
    private String courseName;
    private String courseRoom;


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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAttendCheckFragmentInteractionListener) {
            mListener = (OnAttendCheckFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        checkBtn = (FloatingActionButton) getView().findViewById(R.id.btn_checkBtn);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Pressed!", Toast.LENGTH_SHORT).show();
            }
        });

        Schedules schedulesTable = new Schedules(getContext());
        ArrayList<HashMap<String, String>> schedule = schedulesTable.getNextOrCurrentSchedule();

        Log.d("Data", schedule.get(0).get("name"));
        Log.d("Data", schedule.get(0).get("code"));

        courseName = schedule.get(0).get("code") + " " + schedule.get(0).get("name");
        courseRoom = schedule.get(0).get("room");

        subjectText.setText("วิชา: " + courseName);
        roomText.setText("ห้อง: " + courseRoom);
    }

    public interface OnAttendCheckFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
