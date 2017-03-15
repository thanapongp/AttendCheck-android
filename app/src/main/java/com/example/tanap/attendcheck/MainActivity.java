package com.example.tanap.attendcheck;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.tanap.attendcheck.fragments.AttendCheckFragment.OnAttendCheckFragmentInteractionListener;
import com.example.tanap.attendcheck.utils.PagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements OnAttendCheckFragmentInteractionListener
{
    @BindView(R.id.toolBar_logo) TextView toolbar_logo;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.toolBar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        toolbar_logo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-regular.ttf"));

        tabLayout.addTab(tabLayout.newTab().setText("เช็คชื่อ"));
        tabLayout.addTab(tabLayout.newTab().setText("ตารางเรียน"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setElevation(3);

        final PagerAdapter pagerAdapter = new PagerAdapter(
                getSupportFragmentManager(), tabLayout.getTabCount()
        );

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void openSettingActivity(MenuItem item) {
        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
    }
}