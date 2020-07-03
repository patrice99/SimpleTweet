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
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {

    Tweet tweet;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        //initialize client
        client = TwitterApp.getRestClient(this);

        TextView tvScreenName;
        TextView tvBody;
        TextView tvTimeStamp;
        TextView tvName;
        ImageView ivProfileImage;
        ImageView ivMedia;
        final ImageButton btnRetweet;
        final ImageButton btnLike;
        ImageButton btnReply;
        ImageButton btnShare;

        tvScreenName = findViewById(R.id.tvScreenName);
        tvBody = findViewById(R.id.tvBody);
        tvTimeStamp = findViewById(R.id.tvTimeStamp);
        tvName = findViewById(R.id.tvName);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivMedia = findViewById(R.id.ivMedia);
        btnRetweet = findViewById(R.id.btnRetweet);
        btnLike = findViewById(R.id.btnLike);
        btnReply = findViewById(R.id.btnReply);
        btnShare = findViewById(R.id.btnShare);

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

        if (tweet.retweeted == true){
            Glide.with(this).load(this.getDrawable(R.drawable.ic_vector_retweet)).into(btnRetweet);
        } else {
            Glide.with(this).load(this.getDrawable(R.drawable.ic_vector_retweet_stroke)).into(btnRetweet);
        }
        btnRetweet.setOnClickListener(new View.OnClickListener() {
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
                    Glide.with(TweetDetailsActivity.this).load(TweetDetailsActivity.this.getDrawable(R.drawable.ic_vector_retweet)).into(btnRetweet);
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
                    Glide.with(TweetDetailsActivity.this).load(TweetDetailsActivity.this.getDrawable(R.drawable.ic_vector_retweet_stroke)).into(btnRetweet);

                }

            }
        });

        if (tweet.liked == true){
            Glide.with(this).load(this.getDrawable(R.drawable.ic_vector_heart)).into(btnLike);
        } else {
            Glide.with(this).load(this.getDrawable(R.drawable.ic_vector_heart_stroke)).into(btnLike);
        }
        btnLike.setOnClickListener(new View.OnClickListener() {
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
                    Glide.with(TweetDetailsActivity.this).load(TweetDetailsActivity.this.getDrawable(R.drawable.ic_vector_heart)).into(btnLike);

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
                    Glide.with(TweetDetailsActivity.this).load(TweetDetailsActivity.this.getDrawable(R.drawable.ic_vector_heart_stroke)).into(btnLike);

                }
            }
        });

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle a reply
                //fire intent to compose activity
                Intent intent = new Intent(TweetDetailsActivity.this, ComposeActivity.class);
                //set compose the screenName of the user of that tweet
                intent.putExtra("screenName", tweet.user.screenName);
                startActivity(intent);
            }
        });



    }

}