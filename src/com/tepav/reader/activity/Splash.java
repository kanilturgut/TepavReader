package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import com.androidquery.AQuery;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tepav.reader.R;
import com.tepav.reader.backend.Requests;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.*;
import com.tepav.reader.model.*;
import com.tepav.reader.operation.OfflineList;
import com.tepav.reader.util.ConnectionDetector;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Splash extends Activity {

    final String TAG = "Splash";
    Context context = null;
    Handler startHandler = null;
    Runnable startRunnable = null;

    MySharedPreferences mySharedPreferences = null;
    AQuery aQuery = null;
    ConnectionDetector connectionDetector;

    public static boolean isUserLoggedIn = false;


    static GoogleCloudMessaging gcm;
    static final String GOOGLE_PROJECT_ID = "222792462283";
    private String regId;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        context = this;

        connectionDetector = ConnectionDetector.getInstance(context);
        DBHandler.getInstance(context);

        OfflineList offlineList = OfflineList.getInstance(context);
        offlineList.startReadingFromDatabase();

        //Implementation of handler and its runnable
        startHandler = new Handler();
        startRunnable = new Runnable() {
            @Override
            public void run() {
                Logs.i(TAG, "No internet access, runnable started");
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        };

        mySharedPreferences = MySharedPreferences.getInstance(context);
        aQuery = Aquery.getInstance(context);

        if (connectionDetector.isConnectingToInternet())
            registerGCM();
    }

    public String registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(context);
        regId = getRegistrationId();

        if (TextUtils.isEmpty(regId)) {

            registerInBackground();

            Logs.e(TAG, "registerGCM - successfully registered with GCM server - regId: " + regId);
        } else {
            Logs.e(TAG, "RegId already available. RegId: " + regId);
        }
        return regId;
    }

    private String getRegistrationId() {

        String registrationId = MySharedPreferences.getInstance(context).getGCMInformation().getRegId();

        if (registrationId != null) {
            if (registrationId.isEmpty()) {
                Logs.i(TAG, "Registration not found.");
                return "";
            }
            int registeredVersion = Integer.parseInt(MySharedPreferences.getInstance(context).getGCMInformation().getAppVersion());
            int currentVersion = getAppVersion();
            if (registeredVersion != currentVersion) {
                Logs.i(TAG, "App version changed.");
                return "";
            }
        }

        return registrationId;
    }

    private int getAppVersion() {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Logs.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(GOOGLE_PROJECT_ID);
                    Logs.d(TAG, "registerInBackground - regId: " + regId);
                    msg = "Device registered, registration ID=" + regId;

                    storeRegistrationId(regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Logs.d(TAG, "Error: " + msg);
                }
                Logs.d(TAG, "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Logs.d(TAG, "Registered with GCM Server." + msg);
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(String regId) {

        int appVersion = getAppVersion();
        Logs.i(TAG, "Saving regId on app version " + appVersion);

        GCM gcm = new GCM();
        gcm.setRegId(regId);
        gcm.setAppVersion(String.valueOf(appVersion));

        mySharedPreferences.saveGCMInformation(gcm);
    }


    @Override
    protected void onStart() {
        super.onStart();

        EasyTracker.getInstance(context).activityStart(this);

        if (mySharedPreferences.getSize() > 0 && connectionDetector.isConnectingToInternet()) {

            int userType = mySharedPreferences.getUserType();

            if (userType == MySharedPreferences.USER_TYPE_TEPAV) {

                final TepavUser tepavUser = mySharedPreferences.getTepavUser();

                new AsyncTask<Void, Void, HttpResponse>() {

                    @Override
                    protected HttpResponse doInBackground(Void... voids) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("email", tepavUser.getEmail());
                            jsonObject.put("password", tepavUser.getPassword());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        try {
                            return Requests.post(HttpURL.tepavLogin, jsonObject.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Logs.e(TAG, "LOGIN FAILED", e);
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(HttpResponse httpResponse) {

                        try {
                            String resp = Requests.readResponse(httpResponse);
                            Logs.i(TAG, "response is " + resp);

                            try {
                                JSONObject object = new JSONObject(resp);
                                String fullname = object.getString("fullname");
                                String email = object.getString("email");

                                boolean newsNoti = object.getBoolean("sendNewsNotification");
                                boolean blogNoti = object.getBoolean("sendBlogNotification");
                                boolean publicationNoti = object.getBoolean("sendPublicationNotification");

                                User.setUser(fullname, email, newsNoti, blogNoti, publicationNoti);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (Requests.checkStatusCode(httpResponse, HttpStatus.SC_OK)) {
                            loginSuccessful();
                        } else
                            loginUnsuccessful();
                    }
                }.execute();

            } else if (userType == MySharedPreferences.USER_TYPE_TWITTER) {

                final TwitterUser twitterUser = mySharedPreferences.getTwitterPref();

                new AsyncTask<Void, Void, HttpResponse>() {

                    @Override
                    protected HttpResponse doInBackground(Void... voids) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("oauth_token", twitterUser.getOauthToken());
                            jsonObject.put("oauth_token_secret", twitterUser.getOauthSecret());
                            jsonObject.put("user_id", twitterUser.getUserID());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        try {
                            return Requests.post(HttpURL.twitterLogin, jsonObject.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Logs.e(TAG, "LOGIN FAILED", e);
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(HttpResponse httpResponse) {

                        try {
                            String resp = Requests.readResponse(httpResponse);
                            Logs.i(TAG, "response is " + resp);

                            try {
                                JSONObject object = new JSONObject(resp);
                                String fullname = object.getString("fullname");
                                String email = mySharedPreferences.getTwitterPref().getEmail();

                                boolean newsNoti = object.getBoolean("sendNewsNotification");
                                boolean blogNoti = object.getBoolean("sendBlogNotification");
                                boolean publicationNoti = object.getBoolean("sendPublicationNotification");

                                User.setUser(fullname, email, newsNoti, blogNoti, publicationNoti);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (Requests.checkStatusCode(httpResponse, HttpStatus.SC_OK)) {
                            loginSuccessful();
                        } else
                            loginUnsuccessful();
                    }
                }.execute();

            } else if (userType == MySharedPreferences.USER_TYPE_FACEBOOK) {

                final FacebookUser facebookUser = mySharedPreferences.getFacebookPref();

                new AsyncTask<Void, Void, HttpResponse>() {

                    @Override
                    protected HttpResponse doInBackground(Void... voids) {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("access_token", facebookUser.getToken());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        try {
                            return Requests.post(HttpURL.facebookLogin, jsonObject.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Logs.e(TAG, "LOGIN FAILED", e);
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(HttpResponse httpResponse) {

                        try {
                            String resp = Requests.readResponse(httpResponse);
                            Logs.i(TAG, "response is " + resp);

                            try {
                                JSONObject object = new JSONObject(resp);
                                String fullname = object.getString("fullname");
                                String email = object.getString("email");

                                boolean newsNoti = object.getBoolean("sendNewsNotification");
                                boolean blogNoti = object.getBoolean("sendBlogNotification");
                                boolean publicationNoti = object.getBoolean("sendPublicationNotification");

                                User.setUser(fullname, email, newsNoti, blogNoti, publicationNoti);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (Requests.checkStatusCode(httpResponse, HttpStatus.SC_OK)) {
                            loginSuccessful();
                        } else
                            loginUnsuccessful();
                    }
                }.execute();

            } else {
                //call runnable
                startHandler.postDelayed(startRunnable, Constant.SPLASH_TRANSITION_TIME);
            }
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

        EasyTracker.getInstance(context).activityStop(this);

        if (startHandler != null)
            startHandler.removeCallbacks(startRunnable);
    }
}
