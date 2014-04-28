package com.tepav.reader.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.tepav.reader.R;
import com.tepav.reader.helpers.Constant;

import java.io.File;

/**
 * Author : kanilturgut
 * Date : 28.04.2014
 * Time : 17:22
 */
public class PDFDownloadActivity extends Activity {

    String TAG = "PDFDownloadActivity";
    Context context;

    String filename, fileURL;

    AQuery aQuery = null;
    File downloadedPDF = null;

    Button buttonOpenDownloadedPDF = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_download);
        this.context = this;
        aQuery = new AQuery(context);

        buttonOpenDownloadedPDF = (Button) findViewById(R.id.buttonOpenDownloadedPDF);

        filename = getIntent().getStringExtra("file_name");
        fileURL = getIntent().getStringExtra("file_url");

        checkIfPDFAlreadyDownloaded();
    }

    void checkIfPDFAlreadyDownloaded() {

        File ext = Environment.getExternalStorageDirectory();
        File newFile = new File(ext, Constant.PDF_TARGET + filename);

        if (!newFile.exists()) {
            Log.i(TAG, newFile.getName() + " doesn't exist, it will download from server");
            downloadPDFWithAQuery();
        } else {
            Log.i(TAG, newFile.getName() + " exist, it will load from file target");
            downloadedPDF = newFile;
            buttonOpenDownloadedPDF.setText(getResources().getString(R.string.open_doc));

            openPDFAction(downloadedPDF);
        }
    }

    void downloadPDFWithAQuery() {

        File ext = Environment.getExternalStorageDirectory();
        File target = new File(ext, Constant.PDF_TARGET + filename);

        aQuery.download(fileURL, target, new AjaxCallback<File>() {

            @Override
            public void callback(String url, File file, AjaxStatus status) {

                if(file != null) {
                    downloadedPDF = file;
                    buttonOpenDownloadedPDF.setText(getString(R.string.open_doc));
                    openPDFAction(downloadedPDF);
                } else {
                    buttonOpenDownloadedPDF.setText(getString(R.string.not_found_doc));
                }
            }
        });
    }

    void openPDFAction(File file) {

        if (file != null) {
            try {
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                finish();
            } catch (ActivityNotFoundException ignored) {

            }
        }
    }
}