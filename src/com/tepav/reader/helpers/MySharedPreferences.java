package com.tepav.reader.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import com.tepav.reader.model.FacebookUser;
import com.tepav.reader.model.GCM;
import com.tepav.reader.model.TepavUser;
import com.tepav.reader.model.TwitterUser;

/**
 * Author   : kanilturgut
 * Date     : 05/05/14
 * Time     : 12:22
 */
public class MySharedPreferences {

    Context context = null;
    static MySharedPreferences mySharedPreferences = null;
    SharedPreferences sp = null;

    // Preference name
    public static String PREFERENCE_NAME = "tepav_preferences";

    // Preference keys for Twitter
    final String PREF_KEY_TWITTER_OAUTH_TOKEN = "oauth_token";
    final String PREF_KEY_TWITTER_OAUTH_SECRET = "oauth_token_secret";
    final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";
    final String PREF_KEY_TWITTER_USER_ID = "twitter_user_id";
    final String PREF_KEY_TWITTER_EMAIL = "twitter_email";

    // Preference keys for Facebook
    final String PREF_KEY_FACEBOOK_USERNAME = "facebook_user_name";
    final String PREF_KEY_FACEBOOK_EMAIL = "facebook_user_";
    final String PREF_KEY_FACEBOOK_TOKEN = "facebook_user";

    // Preference keys for TepavUser
    final String PREF_KEY_TEPAV_NAME = "tepav_user_name";
    final String PREF_KEY_TEPAV_SURNAME = "tepav_user_surname";
    final String PREF_KEY_TEPAV_EMAIL = "tepav_user_email";
    final String PREF_KEY_TEPAV_PASSWORD = "tepav_user_password";

    // Constants
    public static final int USER_TYPE_TEPAV = 0;
    public static final int USER_TYPE_TWITTER = 1;
    public static final int USER_TYPE_FACEBOOK = 2;


    //Shared Preferences for GCM
    final String PREF_GCM_REG_ID = "regId";
    final String PREF_GCM_APP_VERSION = "appVersion";

    /**
     * Create an object
     *
     * @param context context of given activity or fragment
     */
    public MySharedPreferences(Context context) {

        this.context = context;
        sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Creates a singleton object
     *
     * @param context context of given activity or fragment
     * @return MySharedPreferences object
     */
    public static MySharedPreferences getInstance(Context context) {

        if (mySharedPreferences == null)
            mySharedPreferences = new MySharedPreferences(context);

        return mySharedPreferences;
    }

    /**
     * Size of shared preferences
     *
     * @return
     */
    public int getSize() {
        if (sp != null)
            return sp.getAll().size();
        else
            return 0;
    }

    /**
     * Add given facebook information into shared preferences
     *
     * @param username facebook username
     * @param email facebook email
     * @param token facebook token
     */
    public void setFacebookPref(String username, String email, String token) {

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREF_KEY_FACEBOOK_USERNAME, username);
        editor.putString(PREF_KEY_FACEBOOK_EMAIL, email);
        editor.putString(PREF_KEY_FACEBOOK_TOKEN, token);
        editor.commit();
    }

    /**
     * Return FacebookUser user from shared preferences
     *
     * @return FacebookUser
     */
    public FacebookUser getFacebookPref() {

        FacebookUser facebookUser = new FacebookUser();
        facebookUser.setUsername(sp.getString(PREF_KEY_FACEBOOK_USERNAME, null));
        facebookUser.setEmail(sp.getString(PREF_KEY_FACEBOOK_EMAIL, null));
        facebookUser.setToken(sp.getString(PREF_KEY_FACEBOOK_TOKEN, null));

        return facebookUser;
    }

    /**
     * Delete FacebookUser information from shared preferences
     */
    public void deleteFacebookFromPref() {

        SharedPreferences.Editor editor = sp.edit();
        editor.remove(PREF_KEY_FACEBOOK_USERNAME);
        editor.remove(PREF_KEY_FACEBOOK_EMAIL);
        editor.remove(PREF_KEY_FACEBOOK_TOKEN);
        editor.commit();
    }

    /**
     * Add given twitter information into shared preferences
     *
     * @param userID twitter user id
     * @param oauthToken twitter oauth token
     * @param oauthSecret twitter oauth token secret
     * @param loggedIn whether user logged in or not
     */
    public void setTwitterPref(String userID, String oauthToken, String oauthSecret, boolean loggedIn, String email) {

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREF_KEY_TWITTER_USER_ID, userID);
        editor.putString(PREF_KEY_TWITTER_OAUTH_TOKEN, oauthToken);
        editor.putString(PREF_KEY_TWITTER_OAUTH_SECRET, oauthSecret);
        editor.putBoolean(PREF_KEY_TWITTER_LOGIN, loggedIn);
        editor.putString(PREF_KEY_TWITTER_EMAIL, email);
        editor.commit();
    }

    /**
     * Return TwitterUser from shared preferences
     *
     * @return TwitterUser
     */
    public TwitterUser getTwitterPref() {

        TwitterUser twitterUser = new TwitterUser();
        twitterUser.setUserID(sp.getString(PREF_KEY_TWITTER_USER_ID, null));
        twitterUser.setOauthToken(sp.getString(PREF_KEY_TWITTER_OAUTH_TOKEN, null));
        twitterUser.setOauthSecret(sp.getString(PREF_KEY_TWITTER_OAUTH_SECRET, null));
        twitterUser.setLoggedIn(sp.getBoolean(PREF_KEY_TWITTER_LOGIN, false));
        twitterUser.setEmail(sp.getString(PREF_KEY_TWITTER_EMAIL, null));

        return twitterUser;
    }

    /**
     * Delete TwitterUser from shared preferences
     */
    public void deleteTwitterFromPref() {

        SharedPreferences.Editor editor = sp.edit();
        editor.remove(PREF_KEY_TWITTER_USER_ID);
        editor.remove(PREF_KEY_TWITTER_OAUTH_TOKEN);
        editor.remove(PREF_KEY_TWITTER_OAUTH_SECRET);
        editor.remove(PREF_KEY_TWITTER_LOGIN);
        editor.remove(PREF_KEY_TWITTER_EMAIL);
        editor.commit();
    }

    /**
     * Add given TepavUser information into shared preferences
     *
     * @param name tepav user name
     * @param surname tepav user surname
     * @param email tepav user email
     * @param password tepav user password
     */
    public void setTepavUserPref(String name, String surname, String email, String password) {

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREF_KEY_TEPAV_NAME, name);
        editor.putString(PREF_KEY_TEPAV_SURNAME, surname);
        editor.putString(PREF_KEY_TEPAV_EMAIL, email);
        editor.putString(PREF_KEY_TEPAV_PASSWORD, password);
        editor.commit();
    }

    /**
     * Return TepavUser from shared preferences
     *
     * @return TepavUser
     */
    public TepavUser getTepavUser() {
        
        TepavUser tepavUser = new TepavUser();
        tepavUser.setName(sp.getString(PREF_KEY_TEPAV_NAME, null));
        tepavUser.setSurname(sp.getString(PREF_KEY_TEPAV_SURNAME, null));
        tepavUser.setEmail(sp.getString(PREF_KEY_TEPAV_EMAIL, null));
        tepavUser.setPassword(sp.getString(PREF_KEY_TEPAV_PASSWORD, null));

        return tepavUser;
    }

    /**
     * Delete TepavUser from shared preferences
     */
    public void deleteTepavFromPref() {

        SharedPreferences.Editor editor = sp.edit();
        editor.remove(PREF_KEY_TEPAV_NAME);
        editor.remove(PREF_KEY_TEPAV_SURNAME);
        editor.remove(PREF_KEY_TEPAV_EMAIL);
        editor.remove(PREF_KEY_TEPAV_PASSWORD);
        editor.commit();
    }


    /**
     *
     * @return userType
     */
    public int getUserType() {

        if (getTepavUser().getName() != null)
            return MySharedPreferences.USER_TYPE_TEPAV;
        else if (getTwitterPref().getUserID() != null)
            return MySharedPreferences.USER_TYPE_TWITTER;
        else if ( getFacebookPref().getUsername() != null)
            return MySharedPreferences.USER_TYPE_FACEBOOK;
        else
            return -1;
    }


    public void saveGCMInformation(GCM gcm) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREF_GCM_REG_ID, gcm.getRegId());
        editor.putString(PREF_GCM_APP_VERSION, gcm.getAppVersion());
        editor.commit();
    }

    public GCM getGCMInformation() {
        GCM gcm = new GCM();
        gcm.setRegId(sp.getString(PREF_GCM_REG_ID, null));
        gcm.setAppVersion(sp.getString(PREF_GCM_APP_VERSION, null));

        return gcm;
    }


}
