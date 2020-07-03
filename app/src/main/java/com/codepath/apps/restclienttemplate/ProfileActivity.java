package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity {
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityProfileBinding binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        binding.tvScreenName.setText("@" + tweet.user.screenName);
        binding.tvName.setText(tweet.user.name);
        Glide.with(this).load(tweet.user.profileImageURl).circleCrop().into(binding.ivProfileImage);
        binding.tvBio.setText(tweet.user.bio);
        binding.tvFollowers.setText("Followers: " + tweet.user.followers);
        binding.tvFollowing.setText("Following: " + tweet.user.following);





    }
}