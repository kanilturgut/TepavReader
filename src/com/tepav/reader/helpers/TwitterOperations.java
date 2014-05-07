package com.tepav.reader.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import com.tepav.reader.model.TwitterUser;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Author   : kanilturgut
 * Date     : 05/05/14
 * Time     : 12:15
 */
public class TwitterOperations {

    String TAG = "TwitterOperations";
    Context context;
    String TWITTER_CONSUMER_KEY = "DcCfDjXdMwgwwKJhw0HfWcWMt";
    String TWITTER_CONSUMER_SECRET = "lgSRrO1DwAQxqVJBTiO7CSSkD3Urkmx4InG9ASrqSpd4uRSSU5";
    //String TWITTER_CONSUMER_KEY = "6OVgh3VmI98dOvfgjcqu48vZT";
    //String TWITTER_CONSUMER_SECRET = "A0NcqkU4ecYLi0CVOYugVHwvVZDlTqofgEu5KyJ1bXssl8eqMT";
    String TWITTER_CALLBACK_URL = "oauth://tepav";

    // Twitter oauth urls
    final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";

    // Twitter
    Twitter twitter;
    RequestToken requestToken;

    static TwitterOperations twitterOperations = null;
    MySharedPreferences mySharedPreferences = null;

    public static TwitterOperations getInstance(Context context) {
        if (twitterOperations == null)
            twitterOperations = new TwitterOperations(context);

        return twitterOperations;
    }

    private TwitterOperations(Context context) {

        this.context = context;
        mySharedPreferences = MySharedPreferences.getInstance(context);
    }

    public boolean isTwitterLoggedInAlready() {
        TwitterUser twitterUser = mySharedPreferences.getTwitterPref();

        return  twitterUser.isLoggedIn();
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

        mySharedPreferences.deleteTwitterFromPref();
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
                Logs.e(TAG, "ERROR on GetOAuthRequestTokenTask");
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(RequestToken requestToken) {
            super.onPostExecute(requestToken);

            if (requestToken != null) {
                TwitterOperations.this.requestToken = requestToken;
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
            }
        }
    }

    class GetOAuthAccessTokenTask extends AsyncTask<String, Void, AccessToken> {

        @Override
        protected AccessToken doInBackground(String... strings) {
            try {
                return twitter.getOAuthAccessToken(requestToken, strings[0]);
            } catch (TwitterException e) {
                Logs.e(TAG, "ERROR on GetOAuthAccessTokenTask");
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(AccessToken accessToken) {

            mySharedPreferences.setTwitterPref(String.valueOf(accessToken.getUserId()), accessToken.getToken(),
                    accessToken.getTokenSecret(), true);

            Logs.i(TAG, "Twitter OAuth Token added to SP");
        }
    }
}
