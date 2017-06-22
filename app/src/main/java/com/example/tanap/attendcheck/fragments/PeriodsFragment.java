package com.example.tanap.attendcheck.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tanap.attendcheck.AttendanceRecord;
import com.example.tanap.attendcheck.R;
import com.example.tanap.attendcheck.db.Periods;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class PeriodsFragment extends Fragment implements AdapterView.OnItemClickListener {
    @BindView (R.id.days_spinner) Spinner daysSpinner;
    @BindView (R.id.periods_list_view) ListView listview;

    public Integer currentSelectedDay = null;
    public BaseAdapter adapter;
    public ArrayList<HashMap<String, String>> periods = new ArrayList<>();

    String[] from = new String[] { 
            "code", 
            "name", 
            "room", 
            "start_time" 
    };
    
    int[] to = new int[] { 
            R.id.item_subjectcode, 
            R.id.item_subjectname, 
            R.id.item_subjectroom, 
            R.id.item_time 
    };


    public PeriodsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_periods, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        boot();
    }

    private void boot() {
        fetchDaysWithPeriods();

        adapter = new SimpleAdapter(getActivity().getApplicationContext(), periods, R.layout.layout_periodsitem, from, to);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);

        Log.d("Data Lenght", String.valueOf(periods.size()));

        daysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSelectedDay = getDaySpinnerValue(daysSpinner.getSelectedItem().toString());
                showPeriodsOnSelectedDay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fetchDaysWithPeriods() {
        Periods periodsTable = new Periods(getActivity().getApplicationContext());
        ArrayList<String> days = periodsTable.getAvailableDays();

        for (String day : days) {
            Log.d("Available day", "Day:" + day);
        }

        String[] daysArr = days.toArray(new String[days.size()]);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(), R.layout.spinner_layout, daysArr
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        daysSpinner.setAdapter(spinnerAdapter);
        daysSpinner.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

        periodsTable.closeDB();
    }

    private void showPeriodsOnSelectedDay() {
        Log.d("Spinner", "Spinner Selected");
        Log.d("Spinner", "value: " + currentSelectedDay);
        Periods periodsTable = new Periods(getActivity().getApplicationContext());
        periods = periodsTable.getPeriodsOnSelectedDay(currentSelectedDay);
        periodsTable.closeDB();

        Log.d("Data Lenght", String.valueOf(periods.size()));
        BaseAdapter newAdapter = new SimpleAdapter(getActivity().getApplicationContext(), periods, R.layout.layout_periodsitem, from, to);
        listview.setAdapter(newAdapter);
        listview.invalidateViews();
    }

    public Integer getDaySpinnerValue(String dayString) {
        switch (dayString){
            case "วันจันทร์":
                return 1;
            case "วันอังคาร":
                return 2;
            case "วันพุธ":
                return 3;
            case "วันพฤหัส":
                return 4;
            case "วันศุกร์":
                return 5;
            case "วันเสาร์":
                return 6;
            case "วันอาทิตย์":
                return 7;
            default:
                return null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String courseCode = ((TextView) view.findViewById(R.id.item_subjectcode)).getText().toString();
        String courseName = ((TextView) view.findViewById(R.id.item_subjectname)).getText().toString();

        Intent intent = new Intent(getContext(), AttendanceRecord.class);

        intent.putExtra("courseCode", courseCode);
        intent.putExtra("courseName", courseName);

        startActivity(intent);

//        Toast.makeText(getActivity(), textView.getText().toString(), Toast.LENGTH_SHORT).show();
    }
}
