package com.example.vod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.net.Inet4Address;

public class VideoPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String title = extras.getString("EXTRA_TITLE");
        String genre = extras.getString("EXTRA_GENRE");
        String url = extras.getString("EXTRA_URL");
        TextView titleText = findViewById(R.id.text_title);
        TextView genreText = findViewById(R.id.text_genre);
        titleText.setText(title);
        genreText.setText(genre);
    }
}
