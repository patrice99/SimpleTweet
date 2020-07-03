package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    //Pass in context and list of tweets'
    Context context;
    List<Tweet> tweets;
    TwitterClient client;
    onClickListener clickListener;

    public interface onClickListener {
        void onReplyAction(int position);
        void onRetweetAction(int position);
        void onUnretweetAction(int position);
        void onLikeAction(int position);
        void onUnlike(int position);
        void onShareAction(int position);
    }


    //constructor
    public TweetsAdapter(Context context, List<Tweet> tweets, onClickListener clickListener){
        this.context = context;
        this.tweets = tweets;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //For each row, inflate the layout for a tweet
        ItemTweetBinding binding = ItemTweetBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Bind values based on the position of the element
        //get the data at position
        Tweet tweet = tweets.get(position);

        //bind the tweet with the viewholder
        holder.bind(tweet);





    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }



    //Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemTweetBinding binding;



        public ViewHolder(@NonNull ItemTweetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


            //initializing the client
            client = new TwitterClient(context);

            //set on click listener
            binding.getRoot().setOnClickListener(this);



        }

        //to bind views
        public void bind(final Tweet tweet) {
            binding.tvBody.setText(tweet.body);
            binding.tvScreenName.setText("@" + tweet.user.screenName);
            binding.tvTime.setText(tweet.getRelativeTimeAgo(tweet.timeStamp));
            binding.tvName.setText(tweet.user.name);
            int radius = 40;
            int margin = 20;
            Glide.with(context).load(tweet.user.profileImageURl).transform(new RoundedCornersTransformation(radius, margin)).into(binding.ivProfileImage);
            if (tweet.photoUrl != null){
                binding.ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.photoUrl).transform(new RoundedCornersTransformation(radius, margin)).into(binding.ivMedia);
            } else {
                binding.ivMedia.setVisibility(View.GONE);
            }

            binding.btnReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onReplyAction(getAdapterPosition());
                }
            });


            if (tweet.retweeted == true){
                Glide.with(context).load(context.getDrawable(R.drawable.ic_vector_retweet)).into(binding.btnRetweet);
            } else {
                Glide.with(context).load(context.getDrawable(R.drawable.ic_vector_retweet_stroke)).into(binding.btnRetweet);
            }
            binding.btnRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tweet.retweeted == false){
                        clickListener.onRetweetAction(getAdapterPosition());
                    } else {
                        clickListener.onUnretweetAction(getAdapterPosition());
                    }
                }
            });

            if (tweet.liked == true){
                Glide.with(context).load(context.getDrawable(R.drawable.ic_vector_heart)).into(binding.btnLike);
            } else {
                Glide.with(context).load(context.getDrawable(R.drawable.ic_vector_heart_stroke)).into(binding.btnLike);
            }
            binding.btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tweet.liked == true){
                        clickListener.onUnlike((getAdapterPosition()));

                    } else{
                        clickListener.onLikeAction(getAdapterPosition());
                    }
                }
            });

            binding.btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onShareAction(getAdapterPosition());
                }
            });




        }

        @Override
        public void onClick(View view) {
            //gets position
            int position = getAdapterPosition();
            //get tweet at that position
            Tweet tweet = tweets.get(position);
            Log.i(TweetsAdapter.class.getSimpleName(), "Tweet at position" + position + "clicked");
            //fire an intent to TweetDetailsActivity class
            Intent intent = new Intent(context, TweetDetailsActivity.class);
            intent.putExtra("tweet", Parcels.wrap(tweet));
            context.startActivity(intent);

        }





    }


    //helper method to clear items from the underlying data set
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    //helper method to add a list of items to the underlying data set
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }








}
