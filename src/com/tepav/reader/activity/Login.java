package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.tepav.reader.R;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.helpers.MySharedPreferences;
import com.tepav.reader.helpers.TwitterOperations;
import com.tepav.reader.model.FacebookUser;
import com.tepav.reader.model.TepavUser;
import com.tepav.reader.model.TwitterUser;
import com.tepav.reader.service.TepavService;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Author : kanilturgut
 * Date : 01.05.2014
 * Time : 19:51
 */
public class Login extends Activity implements View.OnClickListener {

    final String TAG = "Login";
    Context context;

    TextView tvRegister;
    EditText etEmail, etPassword;
    Button bDoLogin, twitterLoginButton;
    LinearLayout llHeaderBack;
    LoginButton facebookLoginButton;

    AQuery aQuery;
    TwitterOperations twitterOperations;
    MySharedPreferences mySharedPreferences;

    Map<String, String> params = null;
    AjaxCallback<JSONObject> ajaxCallback = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.context = this;

        mySharedPreferences = MySharedPreferences.getInstance(context);
        twitterOperations = TwitterOperations.getInstance(context);

        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(this);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        bDoLogin = (Button) findViewById(R.id.bDoLogin);
        bDoLogin.setOnClickListener(this);

        llHeaderBack = (LinearLayout) findViewById(R.id.llHeaderBack);
        llHeaderBack.setOnClickListener(this);

        twitterLoginButton = (Button) findViewById(R.id.bTwitterLogin);
        twitterLoginButton.setOnClickListener(this);

        facebookLoginButton = (LoginButton) findViewById(R.id.authButton);
        facebookLoginButton.setOnErrorListener(new LoginButton.OnErrorListener() {
            @Override
            public void onError(FacebookException error) {
                Logs.i(TAG, "Error " + error.getMessage());
                Toast.makeText(context, "ERROR : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // facebook izinlerini set ediyoruz.
        facebookLoginButton.setReadPermissions(Arrays.asList("basic_info", "email"));
        facebookLoginButton.setSessionStatusCallback(facebookCallback);

        //after register
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        if (email != null && password != null) {
            if (!email.isEmpty() && !password.isEmpty()) {
                etEmail.setText(email);
                etPassword.setText(password);
            }
        }

        aQuery = new AQuery(context);

        if (mySharedPreferences.getSize() > 0) {

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
                        else
                            Logs.e(TAG, "ERROR on Login : " + status.getError());
                    }
                };
                params = new HashMap<String, String>();
                params.put("email", tepavUser.getEmail());
                params.put("password", tepavUser.getPassword());

                etEmail.setText(tepavUser.getEmail());
                etPassword.setText(tepavUser.getPassword());

            } else if (userType == MySharedPreferences.USER_TYPE_TWITTER) {

                loginURL = HttpURL.createURL(HttpURL.twitterLogin);
                TwitterUser twitterUser = mySharedPreferences.getTwitterPref();
                ajaxCallback = new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {

                        if (status.getCode() == HttpStatus.SC_OK)
                            loginSuccessful();
                        else
                            Logs.e(TAG, "ERROR on Login : " + status.getError());
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
                        else
                            Logs.e(TAG, "ERROR on Login : " + status.getError());
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
        }
    }

    void loginSuccessful() {
        Splash.isUserLoggedIn = true;
        finish();
    }

    private final Session.StatusCallback facebookCallback = new Session.StatusCallback() {

        @Override
        public void call(final Session session, SessionState state, Exception exception) {

            if (session.isOpened()) {

                Logs.i(TAG, "Access Token " + session.getAccessToken());
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            Map<String, Object> userMap = user.asMap();
                            String userID = user.getId();
                            String name = user.getName();
                            String username = user.getUsername();
                            String email = null;

                            if (userMap.containsKey("email")) {
                                email = userMap.get("email").toString();
                            } else {
                                Logs.d(TAG, "Facebook email was null");
                                email = username + "@facebook.com";
                                Logs.d(TAG, "Facebook email -> " + email);
                            }

                            Logs.i(TAG, userID + "," + name + "," + username + "," + email);

                            mySharedPreferences.setFacebookPref(name, email, session.getAccessToken());
                            loginSuccessful();

                        }
                    }

                });
            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!twitterOperations.isTwitterLoggedInAlready()) {
            Logs.d(TAG, "onResume, isTwitterLoggedInAlready returned false");
            Uri uri = getIntent().getData();
            twitterOperations.autoLogin(uri);
        } else {
            Logs.d(TAG, "onResume, isTwitterLoggedInAlready returned true");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (!twitterOperations.isTwitterLoggedInAlready()) {
            Logs.d(TAG, "onResume, isTwitterLoggedInAlready returned false");
            Uri uri = getIntent().getData();
            twitterOperations.autoLogin(uri);
        } else {
            Logs.d(TAG, "onResume, isTwitterLoggedInAlready returned true");
        }
    }

    @Override
    public void onClick(View view) {

        if (view == tvRegister) {
            startActivity(new Intent(context, Register.class));
            finish();
        } else if (view == bDoLogin) {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            doLogin(email, password);

        } else if (view == llHeaderBack) {
            onBackPressed();
        } else if (view == twitterLoginButton) {
            twitterOperations.loginToTwitter();
        }
    }

    void doLogin(final String email, final String password) {

        if (!email.isEmpty() && !password.isEmpty()) {

            AjaxCallback<JSONObject> ajaxCallback = new AjaxCallback<JSONObject>() {

                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {

                    if (status.getCode() == HttpStatus.SC_OK) {
                        Logs.i(TAG, "Login successful");

                        try {
                            String name = object.getString("name");
                            String surname = object.getString("surname");

                            mySharedPreferences.setTepavUserPref(name, surname, email, password);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loginSuccessful();
                    } else {
                        Toast.makeText(context, "Login Failed", Toast.LENGTH_LONG).show();
                    }
                }
            };

            Map<String, String> map = new HashMap<String, String>();
            map.put("email", email);
            map.put("password", password);

            ajaxCallback.params(map);
            aQuery.ajax(HttpURL.createURL(HttpURL.tepavLogin), JSONObject.class, ajaxCallback);

        } else {
            Toast.makeText(context, "Boş alanları doldurunuz", Toast.LENGTH_LONG).show();
        }
    }
}