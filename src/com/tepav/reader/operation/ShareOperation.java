package com.tepav.reader.operation;

import android.content.Context;
import android.os.AsyncTask;
import com.tepav.reader.R;
import com.tepav.reader.backend.Requests;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.model.DBData;
import com.tepav.reader.util.AlertDialogManager;
import com.tepav.reader.util.ConnectionDetector;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Author   : kanilturgut
 * Date     : 14/05/14
 * Time     : 11:22
 */
public class ShareOperation {

    final static String TAG = "ShareOperation";

    public static void doShare(Context context, final DBData dbData) {

        if (ConnectionDetector.getInstance(context).isConnectingToInternet()) {

            new AsyncTask<Void, Void, HttpResponse>() {

                @Override
                protected HttpResponse doInBackground(Void... voids) {

                    String url = "";
                    JSONObject map = new JSONObject();

                    try {
                        if (dbData.getType() == DBData.TYPE_NEWS) {
                            url = HttpURL.likeNews;
                            map.put("newsId", dbData.getId());

                        } else if (dbData.getType() == DBData.TYPE_BLOG) {
                            url = HttpURL.likeBLog;
                            map.put("blogId", dbData.getId());
                        } else if (dbData.getType() == DBData.TYPE_PUBLICATION) {
                            url = HttpURL.likePublication;
                            map.put("publicationId", dbData.getId());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        return Requests.post(url, map.toString());
                    } catch (IOException e) {
                        Logs.e(TAG, "SHARE_OPERATION FAILED", e);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(HttpResponse httpResponse) {

                    if (httpResponse != null) {

                        try {
                            String resp = Requests.readResponse(httpResponse);
                            Logs.i(TAG, "response is " + resp);
                        } catch (IOException e) {
                            Logs.e(TAG, "ERROR on reading httpResponse", e);
                        }

                        if (Requests.checkStatusCode(httpResponse, HttpStatus.SC_OK)) {
                            Logs.i(TAG, "SUCCESS");
                        } else {
                            Logs.e(TAG, "FAILED");
                        }
                    }
                }
            }.execute();

        }
    }
}
