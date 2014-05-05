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
import com.tepav.reader.R;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.MySharedPreferences;
import com.tepav.reader.model.FacebookUser;
import com.tepav.reader.model.TepavUser;
import com.tepav.reader.model.TwitterUser;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

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
    Button bDoLogin;
    LinearLayout llHeaderBack;

    AQuery aQuery;
    MySharedPreferences mySharedPreferences;

    Map<String, String> params = null;
    AjaxCallback<JSONObject> ajaxCallback = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.context = this;

        mySharedPreferences = MySharedPreferences.getInstance(context);

        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(this);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        bDoLogin = (Button) findViewById(R.id.bDoLogin);
        bDoLogin.setOnClickListener(this);

        llHeaderBack = (LinearLayout) findViewById(R.id.llHeaderBack);
        llHeaderBack.setOnClickListener(this);

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
            String loginURL= "";

            if (userType == MySharedPreferences.USER_TYPE_TEPAV) {

                loginURL = HttpURL.createURL(HttpURL.tepavLogin);
                TepavUser tepavUser = mySharedPreferences.getTepavUser();
                ajaxCallback = new AjaxCallback<JSONObject>() {

                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {

                        if (status.getCode() == HttpStatus.SC_OK)
                            finish();
                        else
                            Log.e(TAG, "ERROR on Login : Login unsuccessful");
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
                            finish();
                        else
                            Log.e(TAG, "ERROR on Login : Login unsuccessful");
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
                            finish();
                        else
                            Log.e(TAG, "ERROR on Login : Login unsuccessful");
                    }
                };
                params = new HashMap<String, String>();
                params.put("access_token", facebookUser.getToken());
            }

            Log.i(TAG, "Login started with ...");
            for (String key: params.keySet()) {
                Log.i(TAG, key + " : " + params.get(key));
            }

            ajaxCallback.params(params);
            aQuery.ajax(loginURL, JSONObject.class, ajaxCallback);
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
        }
    }

    void doLogin(final String email, final String password) {

        if (!email.isEmpty() && !password.isEmpty()) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", email);
                jsonObject.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            AjaxCallback<JSONObject> ajaxCallback = new AjaxCallback<JSONObject>() {

                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {

                    if (status.getCode() == HttpStatus.SC_OK) {
                        Log.i(TAG, "Login successful");
                        finish();
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