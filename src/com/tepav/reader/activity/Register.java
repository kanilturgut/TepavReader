package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import org.apache.http.HttpStatus;
import org.json.JSONException;
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
    Button doRegister;
    LinearLayout llHeaderBack;

    AQuery aQuery;
    LoginButton facebookLogin;

    final String PREF_NAME = "login_preferences";
    final String PREF_USER_NAME = "name";
    final String PREF_USER_EMAIL = "email";
    final String PREF_USER_FACETOKEN = "facebook_token";

    SharedPreferences sharedPreferences = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.context = this;

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
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        /*
        if (sharedPreferences.getAll().size() > 0) {

            String token = sharedPreferences.getString(PREF_USER_FACETOKEN, "null");
            JSONObject jsonObject = new JSONObject();
            if (!token.equals("null")) {
                try {
                    jsonObject.put("access_token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            aQuery.post(HttpURL.createURL(HttpURL.facebookLogin), jsonObject, Object.class, new AjaxCallback<Object>() {

                @Override
                public void callback(String url, Object object, AjaxStatus status) {

                    finish();
                }
            });

        }
        */

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

        Button twitterButton = (Button) findViewById(R.id.twitterButton);
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Twitter login clicked");
            }
        });

    }

    private final Session.StatusCallback facebookCallback = new Session.StatusCallback() {

        @Override
        public void call(final Session session, SessionState state,
                         Exception exception) {

            if (session.isOpened()) {

                Toast.makeText(context, "Access Token " + session.getAccessToken(), Toast.LENGTH_LONG).show();
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

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(PREF_USER_NAME, name);
                            editor.putString(PREF_USER_EMAIL, email);
                            editor.putString(PREF_USER_FACETOKEN, session.getAccessToken());
                            editor.commit();

                            finish();

                        }
                    }

                });
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();

        //finish();
    }

    @Override
    public void onClick(View view) {

        if (view == doRegister) {

            String name = etName.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!name.isEmpty() && !surname.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", name);
                    jsonObject.put("surname", surname);
                    jsonObject.put("email", email);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {

                        if (status.getCode() == HttpStatus.SC_OK) {
                            changeToLogin(jsonObject);
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
        } else if (view == llHeaderBack) {
            onBackPressed();
        }
    }

    void changeToLogin() {

        startActivity(new Intent(context, Login.class));
        finish();
    }

    void changeToLogin(JSONObject jsonObject) {

        try {
            String email = jsonObject.getString("email");
            String password = jsonObject.getString("password");

            Intent intent = new Intent(context, Login.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);

            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode,
                resultCode, data);
    }
}