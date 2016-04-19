package com.shawon.yousuf.facebookintegration.model;

/**
 * Created by user on 4/19/2016.
 */
public class Friend {

    private String name;
    private String profilePicUrl;


    public Friend(String name, String profilePicUrl) {
        this.name = name;
        this.profilePicUrl = profilePicUrl;
    }

    public String getName() {
        return name;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }
}
