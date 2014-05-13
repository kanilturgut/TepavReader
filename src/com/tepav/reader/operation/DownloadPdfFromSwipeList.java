package com.tepav.reader.operation;

import android.content.Context;
import android.os.Environment;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.tepav.reader.helpers.Aquery;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.Logs;

import java.io.File;

/**
 * Author   : kanilturgut
 * Date     : 11/05/14
 * Time     : 10:07
 */
public class DownloadPdfFromSwipeList {

    final static String TAG = "DownloadPdfFromSwipeList";

    public static void doDownloadPdf(Context context, String filename, String url) {

        File externalStorage = Environment.getExternalStorageDirectory();
        File newFile = new File(externalStorage, Constant.PDF_TARGET + filename);
        Logs.i(TAG, "target : " + newFile);

        if (!newFile.exists()) {
            Logs.i(TAG, filename + " exist on external storage, it will not download again");
        } else {
            Logs.i(TAG, filename + " doesn't exist on external storage, it will download");

            AQuery aQuery = Aquery.getInstance(context);
            aQuery.download(url, newFile, new AjaxCallback<File>() {

                @Override
                public void callback(String url, File object, AjaxStatus status) {

                    if (object != null) {
                        Logs.i(TAG, "file downloaded to external storage");
                    } else {
                        Logs.i(TAG, "file doesn't download to external storage");
                    }
                }
            });
        }
    }

    public static void doDeletePdf(String filename) {

        File externalStorage = Environment.getExternalStorageDirectory();
        File newFile = new File(externalStorage, Constant.PDF_TARGET + filename);
        Logs.i(TAG, "target : " + newFile);

        if (newFile.delete()) {
            Logs.i(TAG, filename + " deleted from external storage");
        } else {
            Logs.i(TAG, filename + " couldn't deleted from external storage");

        }
    }
}
