package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.tepav.reader.R;
import com.tepav.reader.helpers.MySharedPreferences;

/**
 * Author   : kanilturgut
 * Date     : 25/05/14
 * Time     : 23:45
 */
public class GetEmailForTwitter extends Activity {

    final String TAG = "GetEmailForTwitter";
    Context context = null;

    EditText etEmail;
    Button bSaveUser;

    String user_id, token, tokenSecret;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_email_for_twitter);
        this.context = this;

        final MySharedPreferences mySharedPreferences = MySharedPreferences.getInstance(context);

        user_id = getIntent().getStringExtra("user_id");
        token = getIntent().getStringExtra("token");
        tokenSecret = getIntent().getStringExtra("tokenSecret");

        etEmail = (EditText) findViewById(R.id.etEmail);
        bSaveUser = (Button) findViewById(R.id.bDoSaveUser);
        bSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();

                if (!email.isEmpty()) {
                    mySharedPreferences.setTwitterPref(user_id, token, tokenSecret, true, email);
                    finish();
                } else {
                    Toast.makeText(context, getString(R.string.fill_all_blank), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}