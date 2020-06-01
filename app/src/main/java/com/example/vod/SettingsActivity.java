package com.example.vod;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    Intent a = new Intent(SettingsActivity.this,HomeActivity.class);
                    startActivity(a);
                    overridePendingTransition(0,0);
                    break;
                case R.id.action_settings:
                    break;
                case R.id.action_activity3:
                    Intent b = new Intent(SettingsActivity.this,Activity3.class);
                    startActivity(b);
                    overridePendingTransition(0,0);
                    break;
            }
            return true;
        });
        MenuItem item = navigation.getMenu().findItem(R.id.action_settings);
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