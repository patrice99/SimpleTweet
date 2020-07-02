package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
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
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    //Pass in context and list of tweets'
    Context context;
    List<Tweet> tweets;

    //constructor
    public TweetsAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //For each row, inflate the layout for a tweet
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);

        return new ViewHolder(view);
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

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvTime;
        ImageView ivMedia;
        TextView tvName;
        ImageButton btnReply;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvName = itemView.findViewById(R.id.tvName);
            btnReply = itemView.findViewById(R.id.btnReply);

            //set on click listener
            itemView.setOnClickListener(this);
            btnReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Tweet tweet = tweets.get(position);
                    //fire intent to compose activity
                    Intent intent = new Intent(context, ComposeActivity.class);
                    intent.putExtra("screenName", tweet.user.screenName);
                    context.startActivity(intent);
                }
            });



        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@" + tweet.user.screenName);
            tvTime.setText(tweet.getRelativeTimeAgo(tweet.timeStamp));
            tvName.setText(tweet.user.name);
            int radius = 40;
            int margin = 20;
            Glide.with(context).load(tweet.user.profileImageURl).transform(new RoundedCornersTransformation(radius, margin)).into(ivProfileImage);
            if (tweet.photoUrl != null){
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.photoUrl).transform(new RoundedCornersTransformation(radius, margin)).into(ivMedia);
            } else {
                ivMedia.setVisibility(View.GONE);
            }
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
