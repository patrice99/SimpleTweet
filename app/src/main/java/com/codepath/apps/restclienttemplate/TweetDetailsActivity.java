package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        TextView tvScreenName;
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvTimeStamp;
        ImageView ivMedia;

        tvScreenName = findViewById(R.id.tvScreenName);
        tvBody = findViewById(R.id.tvBody);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivMedia = findViewById(R.id.ivMedia);

        

    }

}