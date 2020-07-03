package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //using ViewBinding to get ride of boilerplate code
        ActivityTimelineBinding binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        client = TwitterApp.getRestClient(this);


        swipeContainer = binding.swipeContainer;
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Fetching new data");
                populateHomeTimeline();

            }
        });

        //Find the recycler View
        rvTweets = binding.rvTweets;
        //Initialize the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets, onClickListener);
        //Recycler view setup: layout manager and adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);


        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                loadMoreData();
            }
        };
        //Add the scroll listener to the recycler view
        rvTweets.addOnScrollListener(scrollListener);

        pb = binding.pbLoading;

        populateHomeTimeline();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_logo_whiteonblue);
        getSupportActionBar().setDisplayUseLogoEnabled(true);




        
    }

    private void loadMoreData() {
        // Send an API request to retrieve appropriate paginated data
        pb.setVisibility(ProgressBar.VISIBLE);
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess for load more data");
                //Deserialize and construct new model objects from the API response
                JSONArray jsonArray = json.jsonArray;
                pb.setVisibility(ProgressBar.INVISIBLE);
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);
                    //Append the new data objects to the existing set of items inside the array of items
                    //Notify the adapter of the new items made with `notifyItemRangeInserted()
                    adapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure for load more data", throwable);

            }
        }, tweets.get(tweets.size() - 1).id);
    }

    private void populateHomeTimeline() {
        pb.setVisibility(ProgressBar.VISIBLE);
        client.getTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                pb.setVisibility(ProgressBar.INVISIBLE);
                Log.i(TAG, "onSuccess" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));

                    //Signal refreshing has finished
                    swipeContainer.setRefreshing(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure" + response, throwable);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu;m this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.compose){
            //Navigate to compose activity
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            //get data from the intent (tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));

            //update the recycler view with the tweet
            //Modify the data source of tweets
            tweets.add(0, tweet);
            //Update the adapter
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    TweetsAdapter.onClickListener onClickListener = new TweetsAdapter.onClickListener() {
        @Override
        public void onReplyAction(int position) {
            //handle a reply
            Tweet tweet = tweets.get(position);
            //fire intent to compose activity
            Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
            //set compose the screenName of the user of that tweet
            intent.putExtra("screenName", tweet.user.screenName);
            startActivityForResult(intent, REQUEST_CODE);
        }

        @Override
        public void onRetweetAction(int position) {
            //handle retweet
            // Make an API call to twitter to publish retweet
            pb.setVisibility(ProgressBar.VISIBLE);
            client.publishRetweet(tweets.get(position).id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    Log.i(TweetsAdapter.class.getSimpleName(), "onSuccess to retweet tweet");
                    //set retweet color to green
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TweetsAdapter.class.getSimpleName(), "onFailiure to retweet tweet", throwable);


                }

            });

            tweets.get(position).retweeted = true;
            adapter.notifyItemChanged(position);

        }

        @Override
        public void onUnretweetAction(int position) {
            //handle unretweet
            // Make an API call to twitter to publish unretweet
            pb.setVisibility(ProgressBar.VISIBLE);
            client.publishUnretweet(tweets.get(position).id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    Log.i(TweetsAdapter.class.getSimpleName(), "onSuccess to unretweet tweet");
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TweetsAdapter.class.getSimpleName(), "onFailiure to unretweet tweet", throwable);


                }

            });

            tweets.get(position).retweeted = false;
            adapter.notifyItemChanged(position);


        }

        @Override
        public void onLikeAction(int position) {
            pb.setVisibility(ProgressBar.VISIBLE);
            client.createFavorite(tweets.get(position).id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    Log.i(TweetsAdapter.class.getSimpleName(), "onSuccess to favorite tweet");
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TweetsAdapter.class.getSimpleName(), "onFailiure to favorite tweet", throwable);


                }

            });

            tweets.get(position).liked = true;
            adapter.notifyItemChanged(position);



        }

        @Override
        public void onUnlike(int position) {
            pb.setVisibility(ProgressBar.VISIBLE);
            client.destroyFavorite(tweets.get(position).id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    Log.i(TweetsAdapter.class.getSimpleName(), "onSuccess to Unfavorite tweet");
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TweetsAdapter.class.getSimpleName(), "onFailiure to Unfavorite tweet", throwable);


                }

            });

            tweets.get(position).liked = false;
            adapter.notifyItemChanged(position);

        }

        @Override
        public void onShareAction(int position) {

        }


    };









}