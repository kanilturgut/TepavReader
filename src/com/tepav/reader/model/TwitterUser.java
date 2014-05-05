package com.tepav.reader.model;

/**
 * Author   : kanilturgut
 * Date     : 05/05/14
 * Time     : 12:32
 */
public class TwitterUser {

    String userID;
    String oauthToken;
    String oauthSecret;
    boolean isLoggedIn;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getOauthSecret() {
        return oauthSecret;
    }

    public void setOauthSecret(String oauthSecret) {
        this.oauthSecret = oauthSecret;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
}
