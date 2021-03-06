package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class User {
    public String name;
    public String screenName; //handle
    public String profileImageURl;
    public String bio;
    public String followers;
    public String following;

    //empty constructor for parceler
    public User() {}

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageURl = jsonObject.getString("profile_image_url_https");
        user.bio = jsonObject.getString("description");
        user.followers = jsonObject.getString("followers_count");
        user.following = jsonObject.getString("friends_count");
        return user;
    }


}
