package com.tepav.reader.operation;

import android.os.AsyncTask;
import com.tepav.reader.backend.Requests;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.Logs;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Author   : kanilturgut
 * Date     : 13/05/14
 * Time     : 09:42
 */
public class CommentOperation {

    final static String TAG = "CommentOperation";

    public static void addComment(String contentId, String comment) {

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("comment", comment);
            jsonObject.put("contentId", contentId);
        } catch (JSONException e) {
            Logs.e(TAG, "ERROR on creating jsonObject", e);
        }

        new AsyncTask<Void, Void, HttpResponse>() {

            @Override
            protected HttpResponse doInBackground(Void... voids) {
                try {
                    return Requests.post(HttpURL.addComment, jsonObject.toString());
                } catch (IOException e) {
                    Logs.e(TAG, "ERROR on addComment", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(HttpResponse httpResponse) {

                if (httpResponse != null) {
                    try {
                        Logs.i(TAG, "response is " + Requests.readResponse(httpResponse));
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
