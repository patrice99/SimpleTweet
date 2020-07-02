package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    public static String TAG = "Tweet";
    public String body;
    public String createdAt;
    public User user;
    public String timeStamp;
    public String photoUrl;
    public long id;

    //empty constructor for parceler
    public Tweet(){}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.timeStamp = jsonObject.getString("created_at");
        tweet.id = jsonObject.getLong("id");

        try {
            tweet.photoUrl = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url_https");
        } catch (JSONException e){
            Log.i(TAG, "No media attached");
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length();i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return tweets;

    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //shortened form of time stamp
        String[] num = relativeDate.split(" ");
        relativeDate = num[0] + num[1].charAt(0);





        return relativeDate;
    }

    public String getTimeStamp(String timeStamp){
        String toReturn = "";
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        try {
            long dateMillis = sf.parse(timeStamp).getTime();
            SimpleDateFormat sf2 = new SimpleDateFormat("h:mm a");
            toReturn =  sf2.format(dateMillis);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return toReturn;


    }

    public String getDateStamp(String timeStamp) {
        String toReturn = "";
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        try {
            long dateMillis = sf.parse(timeStamp).getTime();
            SimpleDateFormat sf2 = new SimpleDateFormat("EEE, MMM d, ''yy");
            toReturn = sf2.format(dateMillis);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return toReturn;
    }


}
