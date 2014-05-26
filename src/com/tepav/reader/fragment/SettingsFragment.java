package com.tepav.reader.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.tepav.reader.R;
import com.tepav.reader.backend.Requests;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.model.User;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Author   : kanilturgut
 * Date     : 26/05/14
 * Time     : 17:09
 */
public class SettingsFragment extends Fragment {

    final String TAG = "SettingsFragment";
    Context context = null;

    Button bSaveSettings;
    Switch sNews, sBlog, sPublication;
    TextView tvEmail, tvFullname;

    User user;
    boolean notificationNews, notificationBlog, notificationPublication;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;

        user = User.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        bSaveSettings = (Button) view.findViewById(R.id.bSaveNotificationSettings);
        bSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationNews = sNews.isChecked();
                notificationBlog = sBlog.isChecked();
                notificationPublication = sPublication.isChecked();

                new AsyncTask<Void, Void, HttpResponse>() {

                    @Override
                    protected HttpResponse doInBackground(Void... voids) {

                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("news", notificationNews);
                            jsonObject.put("blog", notificationBlog);
                            jsonObject.put("publication", notificationPublication);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            return Requests.post(HttpURL.notificationSettings, jsonObject.toString());
                        } catch (IOException e) {
                            Logs.e(TAG, "ERROR occured on doInBackgroudn", e);
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(HttpResponse httpResponse) {
                        super.onPostExecute(httpResponse);

                        if (Requests.checkStatusCode(httpResponse, HttpStatus.SC_OK)) {
                            user.setNotificationNews(notificationNews);
                            user.setNotificationBlog(notificationBlog);
                            user.setNotificationPublication(notificationPublication);

                            Logs.d(TAG, "notification settings saved");
                            Toast.makeText(context, "Ayarlar kaydedildi", Toast.LENGTH_SHORT).show();
                        } else {
                            Logs.e(TAG, "notification settings couldn't saved");
                            Toast.makeText(context, "Ayarlar kaydedilemedi", Toast.LENGTH_SHORT).show();
                        }

                    }
                }.execute();

            }
        });

        sNews = (Switch) view.findViewById(R.id.switchNews);
        sBlog = (Switch) view.findViewById(R.id.switchBlog);
        sPublication = (Switch) view.findViewById(R.id.switchPublication);

        tvEmail = (TextView) view.findViewById(R.id.tvNotificationSettingEmail);
        tvFullname = (TextView) view.findViewById(R.id.tvNotificationSettingFullname);

        if (user != null) {
            sNews.setChecked(user.notificationNews);
            sBlog.setChecked(user.notificationBlog);
            sPublication.setChecked(user.notificationPublication);
            tvFullname.setText(user.fullname);
            tvEmail.setText(user.email);

        } else {
            sNews.setChecked(true);
            sBlog.setChecked(true);
            sPublication.setChecked(true);
        }

        return view;
    }
}