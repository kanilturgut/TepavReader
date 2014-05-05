package com.tepav.reader.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.tepav.reader.R;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.MySharedPreferences;
import com.tepav.reader.util.AlertDialogManager;
import com.tepav.reader.util.ConnectionDetector;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

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

    ProgressDialog progressDialog = null;
    AQuery aQuery;

    ConnectionDetector connectionDetector;
    AlertDialogManager alert = new AlertDialogManager();

    MySharedPreferences mySharedPreferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.context = this;

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
    }


    @Override
    public void onClick(View view) {

        if (view == doRegister) {

            progressDialog = ProgressDialog.show(context, getString(R.string.loading), getString(R.string.register_started));

            final String name = etName.getText().toString().trim();
            final String surname = etSurname.getText().toString().trim();
            final String email = etEmail.getText().toString().trim();
            final String password = etPassword.getText().toString().trim();

            if (!name.isEmpty() && !surname.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

                AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {

                        if (status.getCode() == HttpStatus.SC_OK) {

                            if (progressDialog != null)
                                progressDialog.dismiss();

                            changeToLogin(name, surname, email, password);
                        } else {
                            Toast.makeText(context, "Register Failed", Toast.LENGTH_LONG).show();
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

    void changeToLogin(String... strings) {

        mySharedPreferences.setTepavUserPref(strings[0], strings[1], strings[2], strings[3]);

        Intent intent = new Intent(context, Login.class);
        intent.putExtra("email", strings[2]);
        intent.putExtra("password", strings[3]);
        startActivity(intent);

        finish();

    }
}