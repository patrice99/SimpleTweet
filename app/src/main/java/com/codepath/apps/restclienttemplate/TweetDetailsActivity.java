package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Movie;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {

    Tweet tweet;
    TwitterClient client;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //using ViewBinding to get ride of boilerplate code
        final ActivityTweetDetailsBinding binding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initialize client
        client = TwitterApp.getRestClient(this);


        //get tweet from intent
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        binding.tvScreenName.setText("@" + tweet.user.screenName);
        binding.tvBody.setText(tweet.body);
        binding.tvTimeStamp.setText(tweet.getTimeStamp(tweet.timeStamp) + " " +  tweet.getDateStamp(tweet.timeStamp));
        binding.tvName.setText(tweet.user.name);
        Glide.with(this).load(tweet.user.profileImageURl).circleCrop().into(binding.ivProfileImage);
        if (tweet.photoUrl != null){
            binding.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.photoUrl).into(binding.ivMedia);
        } else {
            binding.ivMedia.setVisibility(View.GONE);
        }

        if (tweet.retweeted == true){
            Glide.with(this).load(this.getDrawable(R.drawable.ic_vector_retweet)).into(binding.btnRetweet);
        } else {
            Glide.with(this).load(this.getDrawable(R.drawable.ic_vector_retweet_stroke)).into(binding.btnRetweet);
        }
        binding.btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tweet.retweeted == false) {
                    //handle retweet
                    // Make an API call to twitter to publish unretweet
                    client.publishRetweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TweetsAdapter.class.getSimpleName(), "onSuccess to retweet tweet");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TweetsAdapter.class.getSimpleName(), "onFailiure to retweet tweet", throwable);


                        }

                    });

                    tweet.retweeted = true;
                    Glide.with(TweetDetailsActivity.this).load(TweetDetailsActivity.this.getDrawable(R.drawable.ic_vector_retweet)).into(binding.btnRetweet);
                } else {
                    //handle unretweet
                    // Make an API call to twitter to publish unretweet
                    client.publishUnretweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TweetsAdapter.class.getSimpleName(), "onSuccess to unretweet tweet");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TweetsAdapter.class.getSimpleName(), "onFailiure to unretweet tweet", throwable);


                        }

                    });

                    tweet.retweeted = false;
                    Glide.with(TweetDetailsActivity.this).load(TweetDetailsActivity.this.getDrawable(R.drawable.ic_vector_retweet_stroke)).into(binding.btnRetweet);

                }

            }
        });

        if (tweet.liked == true){
            Glide.with(this).load(this.getDrawable(R.drawable.ic_vector_heart)).into(binding.btnLike);
        } else {
            Glide.with(this).load(this.getDrawable(R.drawable.ic_vector_heart_stroke)).into(binding.btnLike);
        }
        binding.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tweet.liked == false) {
                    client.createFavorite(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TweetsAdapter.class.getSimpleName(), "onSuccess to favorite tweet");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TweetsAdapter.class.getSimpleName(), "onFailiure to favorite tweet", throwable);


                        }

                    });

                    tweet.liked = true;
                    Glide.with(TweetDetailsActivity.this).load(TweetDetailsActivity.this.getDrawable(R.drawable.ic_vector_heart)).into(binding.btnLike);

                } else {
                    client.destroyFavorite(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TweetsAdapter.class.getSimpleName(), "onSuccess to Unfavorite tweet");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TweetsAdapter.class.getSimpleName(), "onFailiure to Unfavorite tweet", throwable);


                        }

                    });

                    tweet.liked = false;
                    Glide.with(TweetDetailsActivity.this).load(TweetDetailsActivity.this.getDrawable(R.drawable.ic_vector_heart_stroke)).into(binding.btnLike);

                }
            }
        });

        binding.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle a reply
                //fire intent to compose activity
                Intent intent = new Intent(TweetDetailsActivity.this, ComposeActivity.class);
                //set compose the screenName of the user of that tweet
                intent.putExtra("screenName", tweet.user.screenName);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });



    }


}