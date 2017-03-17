package com.example.tanap.attendcheck.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.tanap.attendcheck.fragments.AttendCheckFragment;
import com.example.tanap.attendcheck.fragments.PeriodsFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    public int tabsCount;

    public PagerAdapter(FragmentManager fm, int tabsCount) {
        super(fm);
        this.tabsCount = tabsCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AttendCheckFragment();
            case 1:
                return new PeriodsFragment();
            default:
                throw new RuntimeException("Unknow Tab postion");
        }
    }

    @Override
    public int getCount() {
        return this.tabsCount;
    }
}
