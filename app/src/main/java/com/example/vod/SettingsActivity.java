package com.example.vod;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    String[] items = new String[]{"Sign Out"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (items[position] == "Sign Out") {
                AWSMobileClient.getInstance().signOut();
                Intent authIntent = new Intent(SettingsActivity.this, AuthenticationActivity.class);
                finish();
                startActivity(authIntent);
            }
        });

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

        Intent intent = getIntent();
        String uname = intent.getStringExtra("username");
        TextView welcome = findViewById(R.id.welcome);
        String welcomeString = "Welcome"+uname;
        welcome.setText(welcomeString);
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