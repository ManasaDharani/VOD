package com.example.vod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Activity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    Intent a = new Intent(Activity3.this,HomeActivity.class);
                    startActivity(a);
                    overridePendingTransition(0,0);
                    break;
                case R.id.action_settings:
                    Intent b = new Intent(Activity3.this,SettingsActivity.class);
                    startActivity(b);
                    overridePendingTransition(0,0);
                    break;
                case R.id.action_activity3:

                    break;
            }
            return true;
        });
        MenuItem item = navigation.getMenu().findItem(R.id.action_activity3);
        item.setChecked(true);

    }
/*
    @Override
    int getContentViewId() {
        return R.layout.settings_activity;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.action_settings;
    }
*/
}
