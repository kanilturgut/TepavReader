package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.facebook.*;
import com.facebook.AccessToken;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.tepav.reader.R;
import com.tepav.reader.backend.Requests;
import com.tepav.reader.helpers.*;
import com.tepav.reader.model.FacebookUser;
import com.tepav.reader.model.TepavUser;
import com.tepav.reader.model.TwitterUser;
import com.tepav.reader.model.User;
import com.tepav.reader.operation.TwitterOperations;
import com.tepav.reader.util.ConnectionDetector;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.*;

import java.io.IOException;
import java.util.Arrays;
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

    TwitterOperations twitterOperations;
    MySharedPreferences mySharedPreferences;

    ConnectionDetector connectionDetector;

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

        connectionDetector = ConnectionDetector.getInstance(context);

        if (mySharedPreferences.getSize() > 0 && connectionDetector.isConnectingToInternet()) {
            if (mySharedPreferences.getUserType() == MySharedPreferences.USER_TYPE_TEPAV) {
                doTepavLogin();
            } else if (mySharedPreferences.getUserType() == MySharedPreferences.USER_TYPE_TWITTER) {
                doTwitterLogin();
            } else if (mySharedPreferences.getUserType() == MySharedPreferences.USER_TYPE_FACEBOOK) {
                doFacebookLogin();
            }
        }
    }

    void loginSuccessful() {

        new AsyncTask<Void, Void, HttpResponse>() {

            @Override
            protected HttpResponse doInBackground(Void... voids) {

                if (mySharedPreferences.getGCMInformation().getRegId().isEmpty())
                    cancel(true);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("android", mySharedPreferences.getGCMInformation().getRegId());

                    return Requests.post(HttpURL.registerGCM, jsonObject.toString());

                } catch (JSONException e) {
                    Logs.e(TAG, "GCM REGISTRATION FAILED", e);
                } catch (IOException e) {
                    Logs.e(TAG, "GCM REGISTRATION FAILED", e);
                }


                return null;
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {
                super.onPostExecute(httpResponse);

                if (Requests.checkStatusCode(httpResponse, HttpStatus.SC_OK))
                    Logs.d(TAG, "GCM REGISTRATION SUCCESS");
            }
        }.execute();


        Splash.isUserLoggedIn = true;
        finish();
    }

    void loginUnsuccessful() {
        Splash.isUserLoggedIn = false;
        Toast.makeText(context, "Giriş İşlemi Başarısız", Toast.LENGTH_LONG).show();

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
                            doFacebookLogin();

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
            autoLogin(uri, TwitterOperations.twitter, TwitterOperations.requestToken);
        } else {
            Logs.d(TAG, "onResume, isTwitterLoggedInAlready returned true");
            doTwitterLogin();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (!twitterOperations.isTwitterLoggedInAlready()) {
            Logs.d(TAG, "onResume, isTwitterLoggedInAlready returned false");
            Uri uri = getIntent().getData();
            autoLogin(uri, TwitterOperations.twitter, TwitterOperations.requestToken);
        } else {
            Logs.d(TAG, "onResume, isTwitterLoggedInAlready returned true");
            if (!mySharedPreferences.getTwitterPref().getEmail().equals(""))
                doTwitterLogin();
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

    void doFacebookLogin() {

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

                        User.setUser(fullname, email);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (Requests.checkStatusCode(httpResponse, HttpStatus.SC_OK)) {
                        loginSuccessful();
                    } else
                        loginUnsuccessful();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.execute();

    }

    void doTwitterLogin() {

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

                        User.setUser(fullname, email);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (Requests.checkStatusCode(httpResponse, HttpStatus.SC_OK)) {
                        loginSuccessful();
                    } else
                        loginUnsuccessful();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.execute();
    }

    void doTepavLogin() {

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

                        User.setUser(fullname, email);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (Requests.checkStatusCode(httpResponse, HttpStatus.SC_OK)) {
                        loginSuccessful();
                    } else
                        loginUnsuccessful();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    void doLogin(final String email, final String password) {

        if (!email.isEmpty() && !password.isEmpty()) {

            new AsyncTask<Void, Void, HttpResponse>() {

                @Override
                protected HttpResponse doInBackground(Void... voids) {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("email", email);
                        jsonObject.put("password", password);
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
                            String name = object.getString("name");
                            String surname = object.getString("surname");
                            String fullname = object.getString("fullname");
                            String email = object.getString("email");

                            mySharedPreferences.setTepavUserPref(name, surname, email, password);
                            User.setUser(fullname, email);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (Requests.checkStatusCode(httpResponse, HttpStatus.SC_OK)) {
                            loginSuccessful();
                        } else
                            loginUnsuccessful();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.execute();
        } else {
            Toast.makeText(context, "Boş alanları doldurunuz", Toast.LENGTH_LONG).show();
        }
    }

    void autoLogin(Uri uri, Twitter twitter, RequestToken requestToken) {

        if (uri != null && uri.toString().startsWith(TwitterOperations.TWITTER_CALLBACK_URL)) {
            String verifier = uri.getQueryParameter(TwitterOperations.URL_TWITTER_OAUTH_VERIFIER);
            GetOAuthAccessTokenTask getOAuthAccessTokenTask = new GetOAuthAccessTokenTask(twitter, requestToken);
            getOAuthAccessTokenTask.execute(verifier);
        }
    }

    class GetOAuthAccessTokenTask extends AsyncTask<String, Void, twitter4j.auth.AccessToken> {

        Twitter twitter;
        RequestToken requestToken;

        public GetOAuthAccessTokenTask(Twitter twitter, RequestToken requestToken) {
            this.twitter = twitter;
            this.requestToken = requestToken;
        }

        @Override
        protected twitter4j.auth.AccessToken doInBackground(String... strings) {
            try {
                return twitter.getOAuthAccessToken(requestToken, strings[0]);
            } catch (TwitterException e) {
                Logs.e(TAG, "ERROR on GetOAuthAccessTokenTask");
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(twitter4j.auth.AccessToken accessToken) {

            Intent intent = new Intent(context, GetEmailForTwitter.class);
            intent.putExtra("user_id", String.valueOf(accessToken.getUserId()));
            intent.putExtra("token", accessToken.getToken());
            intent.putExtra("tokenSecret", accessToken.getTokenSecret());
            startActivity(intent);
        }
    }
}