package com.example.tanap.attendcheck;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.tanap.attendcheck.fragments.AttendCheckFragment;
import com.example.tanap.attendcheck.interfaces.AsyncResponseBoolean;
import com.example.tanap.attendcheck.tasks.UpdateDataTask;
import com.example.tanap.attendcheck.utils.PagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AsyncResponseBoolean {
    @BindView(R.id.toolBar_logo) TextView toolbar_logo;
    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.toolBar) Toolbar toolbar;
    private PagerAdapter pagerAdapter;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        updateData();

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        toolbar_logo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-regular.ttf"));

        tabLayout.addTab(tabLayout.newTab().setText("เช็คชื่อ"));
        tabLayout.addTab(tabLayout.newTab().setText("ตารางเรียน"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabLayout.setElevation(3);
        }

        pagerAdapter = new PagerAdapter(
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

    private void updateData() {
        Log.d("Update", "Try updating data");
        Log.d("Update", String.valueOf(Build.VERSION.SDK_INT));
        Log.d("Update", String.valueOf(android.os.Build.VERSION_CODES.M));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasLocationPermission = checkSelfPermission(Manifest.permission_group.LOCATION);
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                }, REQUEST_CODE_ASK_PERMISSIONS);
            }
        } else {
            if (isNetworkAvailable()) {
                new UpdateDataTask(MainActivity.this, this).execute();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isNetworkAvailable()) {
                        new UpdateDataTask(MainActivity.this, this).execute();
                    }
                } else {
                    //
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = connectManager.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void openSettingActivity(MenuItem item) {
        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
    }

    @Override
    public void processFinish(Boolean result) {
        viewPager.setAdapter(pagerAdapter);
    }
}
