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
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.tepav.reader.R;
import com.tepav.reader.helpers.HttpURL;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
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
    String faceAccessToken = "000";

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
        public void call(Session session, SessionState state,
                         Exception exception) {

            if (session.isOpened()) {
                faceAccessToken = session.getAccessToken();
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

                        }
                    }

                });
            }
        }

        Request.GraphUserCallback userCallback = new Request.GraphUserCallback() {

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

                }
            }
        };
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

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", name);
                    jsonObject.put("surname", surname);
                    jsonObject.put("email", email);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                aQuery.post(HttpURL.createURL(HttpURL.tepavRegister), jsonObject, Object.class, new AjaxCallback<Object>());

                changeToLogin();
            } else {
                Toast.makeText(context, "facebook token : " + faceAccessToken, Toast.LENGTH_LONG).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode,
                resultCode, data);
    }
}