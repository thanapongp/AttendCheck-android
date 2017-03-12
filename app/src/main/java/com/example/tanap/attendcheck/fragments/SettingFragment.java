package com.example.tanap.attendcheck.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.tanap.attendcheck.R;

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
