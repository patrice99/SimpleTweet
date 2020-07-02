package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Movie;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        TextView tvScreenName;
        TextView tvBody;
        TextView tvTimeStamp;
        TextView tvName;
        ImageView ivProfileImage;
        ImageView ivMedia;

        tvScreenName = findViewById(R.id.tvScreenName);
        tvBody = findViewById(R.id.tvBody);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);
        tvName = findViewById(R.id.tvName);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivMedia = findViewById(R.id.ivMedia);

        //get tweet from intent
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvScreenName.setText("@" + tweet.user.screenName);
        tvBody.setText(tweet.body);
        tvTimeStamp.setText(tweet.getTimeStamp(tweet.timeStamp) + " " +  tweet.getDateStamp(tweet.timeStamp));
        tvName.setText(tweet.user.name);
        Glide.with(this).load(tweet.user.profileImageURl).into(ivProfileImage);
        if (tweet.photoUrl != null){
            ivMedia.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.photoUrl).into(ivMedia);
        } else {
            ivMedia.setVisibility(View.GONE);
        }



    }

}