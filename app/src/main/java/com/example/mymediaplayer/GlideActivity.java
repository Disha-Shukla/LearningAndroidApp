package com.example.mymediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GlideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);

        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this)
                .load("https://www.tutorialspoint.com/images/tp-logo-diamond.png")
                .into(imageView);
    }
}