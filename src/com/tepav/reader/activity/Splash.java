package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.tepav.reader.R;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.helpers.MySharedPreferences;
import com.tepav.reader.model.FacebookUser;
import com.tepav.reader.model.TepavUser;
import com.tepav.reader.model.TwitterUser;
import com.tepav.reader.service.TepavService;
import com.tepav.reader.util.ConnectionDetector;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Splash extends Activity {

    final String TAG = "Splash";
    Context context = null;
    Handler startHandler = null;
    Runnable startRunnable = null;

    MySharedPreferences mySharedPreferences = null;
    AQuery aQuery = null;
    Map<String, String> params = null;
    AjaxCallback<JSONObject> ajaxCallback = null;
    ConnectionDetector connectionDetector;


    public static boolean isUserLoggedIn = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        context = this;

        connectionDetector = new ConnectionDetector(context);
        DBHandler.getInstance(context);
        startService(new Intent(context, TepavService.class));

        //Implementation of handler and its runnable
        startHandler = new Handler();
        startRunnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        };

        mySharedPreferences = MySharedPreferences.getInstance(context);
        aQuery = new AQuery(context);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mySharedPreferences.getSize() > 0 && connectionDetector.isConnectingToInternet()) {

            int userType = mySharedPreferences.getUserType();
            String loginURL = "";

            if (userType == MySharedPreferences.USER_TYPE_TEPAV) {

                loginURL = HttpURL.createURL(HttpURL.tepavLogin);
                TepavUser tepavUser = mySharedPreferences.getTepavUser();
                ajaxCallback = new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {

                        if (status.getCode() == HttpStatus.SC_OK)
                            loginSuccessful();
                        else {
                            Logs.e(TAG, "ERROR on Login : " + status.getError());
                            loginUnsuccessful();
                        }
                    }
                };
                params = new HashMap<String, String>();
                params.put("email", tepavUser.getEmail());
                params.put("password", tepavUser.getPassword());

            } else if (userType == MySharedPreferences.USER_TYPE_TWITTER) {

                loginURL = HttpURL.createURL(HttpURL.twitterLogin);
                TwitterUser twitterUser = mySharedPreferences.getTwitterPref();
                ajaxCallback = new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {

                        if (status.getCode() == HttpStatus.SC_OK)
                            loginSuccessful();
                        else {
                            Logs.e(TAG, "ERROR on Login : " + status.getError());
                            loginUnsuccessful();
                        }
                    }
                };
                params = new HashMap<String, String>();
                params.put("oauth_token", twitterUser.getOauthToken());
                params.put("oauth_token_secret", twitterUser.getOauthSecret());
                params.put("user_id", twitterUser.getUserID());

            } else if (userType == MySharedPreferences.USER_TYPE_FACEBOOK) {

                loginURL = HttpURL.createURL(HttpURL.facebookLogin);
                FacebookUser facebookUser = mySharedPreferences.getFacebookPref();
                ajaxCallback = new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {

                        if (status.getCode() == HttpStatus.SC_OK)
                            loginSuccessful();
                        else {
                            Logs.e(TAG, "ERROR on Login : " + status.getError());
                            loginUnsuccessful();
                        }
                    }
                };
                params = new HashMap<String, String>();
                params.put("access_token", facebookUser.getToken());
            }

            Logs.i(TAG, "Login started with ...");
            for (String key : params.keySet()) {
                Logs.i(TAG, key + " : " + params.get(key));
            }

            ajaxCallback.params(params);
            aQuery.ajax(loginURL, JSONObject.class, ajaxCallback);
        } else {
            //call runnable
            startHandler.postDelayed(startRunnable, Constant.SPLASH_TRANSITION_TIME);
        }
    }

    void loginSuccessful() {
        Logs.d(TAG, "login successful");
        isUserLoggedIn = true;
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }

    void loginUnsuccessful() {
        Logs.d(TAG, "login not successful");
        isUserLoggedIn = false;
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (startHandler != null)
            startHandler.removeCallbacks(startRunnable);
    }
}
