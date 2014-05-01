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
import com.tepav.reader.R;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.HttpURL;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author : kanilturgut
 * Date : 01.05.2014
 * Time : 19:51
 */
public class Login extends Activity implements View.OnClickListener {

    Context context;

    TextView tvRegister;
    EditText etEmail, etPassword;
    Button bDoLogin;
    LinearLayout llHeaderBack;

    AQuery aQuery;

    SharedPreferences sharedPreferences;

    final String USER_EMAIL = "user_email";
    final String USER_PASSWORD = "user_password";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.context = this;

        sharedPreferences = getSharedPreferences(Constant.SP_LOGIN, MODE_PRIVATE);

        if (sharedPreferences.getAll().size() > 0) {

            String email = sharedPreferences.getString(USER_EMAIL, "null");
            String pass = sharedPreferences.getString(USER_PASSWORD, "null");

            if (!"null".equals(email) && !"null".equals(pass))
                doLogin(email, pass);
        }

        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(this);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        bDoLogin = (Button) findViewById(R.id.bDoLogin);
        bDoLogin.setOnClickListener(this);

        llHeaderBack = (LinearLayout) findViewById(R.id.llHeaderBack);
        llHeaderBack.setOnClickListener(this);

        aQuery = new AQuery(context);
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
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
                jsonObject.put(USER_EMAIL, email);
                jsonObject.put(USER_PASSWORD, password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            aQuery.post(HttpURL.createURL(HttpURL.tepavLogin), jsonObject, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {

                    if (object != null) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_email", email);
                        editor.putString("user_password", password);
                        editor.commit();
                    }

                    finish();
                }
            });

        } else {
            Toast.makeText(context, "Boş alanları doldurunuz", Toast.LENGTH_LONG).show();
        }
    }
}