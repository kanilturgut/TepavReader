package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.tepav.reader.helpers.MySharedPreferences;
import com.tepav.reader.helpers.TwitterOperations;
import com.tepav.reader.model.TepavUser;
import com.tepav.reader.util.AlertDialogManager;
import com.tepav.reader.util.ConnectionDetector;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Author : kanilturgut
 * Date : 01.05.2014
 * Time : 19:21
 */
public class Register extends Activity implements View.OnClickListener {

    final String TAG = "Register";
    Context context;

    EditText etName, etSurname, etEmail, etPassword;
    TextView tvLogin;
    Button doRegister, twitterButton;
    LinearLayout llHeaderBack;

    AQuery aQuery;
    LoginButton facebookLogin;
    TwitterOperations twitterOperations;

    ConnectionDetector connectionDetector;
    AlertDialogManager alert = new AlertDialogManager();

    MySharedPreferences mySharedPreferences;

    Map<String, String> params = null;
    AjaxCallback<JSONObject> ajaxCallback = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.context = this;

        twitterOperations = TwitterOperations.getInstance(context);
        mySharedPreferences = MySharedPreferences.getInstance(context);

        connectionDetector = new ConnectionDetector(context);
        if (!connectionDetector.isConnectingToInternet()) {
            alert.showAlertDialog(context, getString(R.string.ad_no_internet_error_title), getString(R.string.ad_no_internet_error_message), false);
            return;
        }

        etName = (EditText) findViewById(R.id.etName);
        etSurname = (EditText) findViewById(R.id.etSurname);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        doRegister = (Button) findViewById(R.id.bDoRegister);
        doRegister.setOnClickListener(this);

        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(this);

        llHeaderBack = (LinearLayout) findViewById(R.id.llHeaderBack);
        llHeaderBack.setOnClickListener(this);

        aQuery = new AQuery(context);


        if (mySharedPreferences.getSize() > 0) {

            int userType = mySharedPreferences.getUserType();

            if (userType == MySharedPreferences.USER_TYPE_TEPAV) {

                TepavUser tepavUser = mySharedPreferences.getTepavUser();

                ajaxCallback = new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {

                    }
                };
                params = new HashMap<String, String>();
                params.put("email", tepavUser.getEmail());
                params.put("password", tepavUser.getEmail());
                ajaxCallback.params(params);
                aQuery.ajax(HttpURL.createURL(HttpURL.tepavLogin), JSONObject.class, ajaxCallback);


            } else if (userType == MySharedPreferences.USER_TYPE_TWITTER) {

            } else if (userType == MySharedPreferences.USER_TYPE_FACEBOOK) {

            }

        }


        twitterButton = (Button) findViewById(R.id.twitterButton);
        twitterButton.setOnClickListener(this);

        facebookLogin = (LoginButton) findViewById(R.id.authButton);
        facebookLogin.setOnErrorListener(new LoginButton.OnErrorListener() {
            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "Error " + error.getMessage());
                Toast.makeText(context, "ERROR : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // facebook izinlerini set ediyoruz.
        facebookLogin.setReadPermissions(Arrays.asList("basic_info", "email"));
        facebookLogin.setSessionStatusCallback(facebookCallback);


    }

    private final Session.StatusCallback facebookCallback = new Session.StatusCallback() {

        @Override
        public void call(final Session session, SessionState state, Exception exception) {

            if (session.isOpened()) {

                Log.i(TAG, "Access Token " + session.getAccessToken());
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
                                Log.d(TAG, "Facebook email was null");
                                email = username + "@facebook.com";
                                Log.d(TAG, "Facebook email -> " + email);
                            }

                            Log.i(TAG, userID + "," + name + "," + username + ","
                                    + email);

                            Toast.makeText(context, "USER INFO : " + userID + "," + name + "," + username + ","
                                    + email, Toast.LENGTH_LONG).show();

                            mySharedPreferences.setFacebookPref(name, email, session.getAccessToken());
                            finish();

                        }
                    }

                });
            }
        }
    };

    @Override
    public void onClick(View view) {

        if (view == doRegister) {

            final String name = etName.getText().toString().trim();
            final String surname = etSurname.getText().toString().trim();
            final String email = etEmail.getText().toString().trim();
            final String password = etPassword.getText().toString().trim();

            if (!name.isEmpty() && !surname.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

                AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {

                        if (status.getCode() == HttpStatus.SC_OK) {
                            changeToLogin(name, surname, email, password);
                        }

                    }
                };

                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("surname", surname);
                params.put("email", email);
                params.put("password", password);

                cb.params(params);

                aQuery.ajax(HttpURL.createURL(HttpURL.tepavRegister), JSONObject.class, cb);

            } else {
                Toast.makeText(context, getString(R.string.fill_all_blank), Toast.LENGTH_LONG).show();
            }
        } else if (view == tvLogin) {
            changeToLogin();
        } else if (view == twitterButton) {
            twitterOperations.loginToTwitter();
        } else if (view == llHeaderBack) {
            onBackPressed();
        }
    }

    void changeToLogin() {

        startActivity(new Intent(context, Login.class));
        finish();
    }

    void changeToLogin(String... strings) {

        mySharedPreferences.setTepavUserPref(strings[0], strings[1], strings[2], strings[3]);

        Intent intent = new Intent(context, Login.class);
        intent.putExtra("email", strings[2]);
        intent.putExtra("password", strings[3]);
        startActivity(intent);

        finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
}