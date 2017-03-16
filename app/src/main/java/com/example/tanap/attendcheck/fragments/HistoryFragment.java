package com.example.tanap.attendcheck.fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.tanap.attendcheck.R;
import com.example.tanap.attendcheck.db.Periods;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {


    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Periods periodsTable = new Periods(getContext());
        ArrayList<String> days = periodsTable.getAvailibleDays();

        String[] daysArr = days.toArray(new String[days.size()]);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(), R.layout.spinner_layout, daysArr
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) getActivity().findViewById(R.id.days_spinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.getBackground().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

        periodsTable.closeDB();
    }
}
