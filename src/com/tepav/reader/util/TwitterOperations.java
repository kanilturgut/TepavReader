package com.tepav.reader.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Author   : kanilturgut
 * Date     : 03/05/14
 * Time     : 19:23
 */
public class TwitterOperations {

    String TAG = "TwitterOperations";
    Context context;
    String TWITTER_CONSUMER_KEY = "DcCfDjXdMwgwwKJhw0HfWcWMt";
    String TWITTER_CONSUMER_SECRET = "lgSRrO1DwAQxqVJBTiO7CSSkD3Urkmx4InG9ASrqSpd4uRSSU5";

    // Preference Constants
    public static String PREFERENCE_NAME = "twitter_oauth";
    final static String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    final static String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    final static String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    final static String PREF_KEY_USER_ID = "twitter_user_id";

    final String TWITTER_CALLBACK_URL = "oauth://tepav";

    // Twitter oauth urls
    final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";

    // Twitter
    Twitter twitter;
    RequestToken requestToken;

    // Shared Preferences
    static SharedPreferences mSharedPreferences = null;
    static TwitterOperations twitterOperations = null;

    public static TwitterOperations getInstance(Context context) {
        if (twitterOperations == null)
            twitterOperations = new TwitterOperations(context);

        return twitterOperations;
    }

    private TwitterOperations(Context context) {

        this.context = context;
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public boolean isTwitterLoggedInAlready() {
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    public void loginToTwitter() {

        if (!isTwitterLoggedInAlready()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory twitterFactory = new TwitterFactory(configuration);
            twitter = twitterFactory.getInstance();

            new GetOAuthRequestTokenTask().execute();
        }

    }

    public void logoutFromTwitter() {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(PREF_KEY_OAUTH_TOKEN);
        editor.remove(PREF_KEY_OAUTH_SECRET);
        editor.remove(PREF_KEY_USER_ID);
        editor.remove(PREF_KEY_TWITTER_LOGIN);
        editor.commit();

    }

    public void autoLogin(Uri uri) {

        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
            String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
            new GetOAuthAccessTokenTask().execute(verifier);
        }
    }


    class GetOAuthRequestTokenTask extends AsyncTask<Void, Void, RequestToken> {

        @Override
        protected RequestToken doInBackground(Void... voids) {
            try {
                return twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
            } catch (TwitterException e) {
                Log.e(TAG, "ERROR on GetOAuthRequestTokenTask");
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(RequestToken requestToken) {
            super.onPostExecute(requestToken);

            TwitterOperations.this.requestToken = requestToken;
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
        }
    }

    class GetOAuthAccessTokenTask extends AsyncTask<String, Void, AccessToken> {

        @Override
        protected AccessToken doInBackground(String... strings) {
            try {
                return twitter.getOAuthAccessToken(requestToken, strings[0]);
            } catch (TwitterException e) {
                Log.e(TAG, "ERROR on GetOAuthAccessTokenTask");
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(AccessToken accessToken) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            editor.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            editor.putString(PREF_KEY_USER_ID, String.valueOf(accessToken.getUserId()));
            editor.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            editor.commit();

            Log.i(TAG, "Twitter OAuth Token added to SP");
        }
    }
}
