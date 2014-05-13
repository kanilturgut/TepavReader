package com.tepav.reader.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.tepav.reader.R;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Aquery;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.helpers.popup.QuickActionForList;
import com.tepav.reader.helpers.popup.QuickActionForPost;
import com.tepav.reader.model.DBData;
import com.tepav.reader.operation.LikeOperation;
import com.tepav.reader.util.Util;
import com.tepav.reader.model.Publication;
import com.tepav.reader.util.AlertDialogManager;
import org.json.JSONException;

import java.io.File;

/**
 * Author : kanilturgut
 * Date : 27.04.2014
 * Time : 18:56
 */
public class PublicationDetails extends Activity implements View.OnClickListener {

    final String TAG = "PublicationDetails";
    Context context;
    DBHandler dbHandler;
    QuickActionForPost quickAction;
    QuickActionForList quickActionForList;
    int fromWhere, listType;

    Publication publication;

    WebView webView;
    TextView titleOfPublication, timeOfPublication;
    LinearLayout llHeaderBack, llFooterLike, llFooterAlreadyLiked, llFooterShare, llFooterAddToList, filesLayout;
    RelativeLayout rlFooter;
    View viewTransparent;
    Button buttonOpenPDF;

    AQuery aQuery = null;
    File downloadedPDF = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_details);
        this.context = this;

        try {
            publication =Publication.fromDBData((DBData) getIntent().getSerializableExtra("class"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fromWhere = getIntent().getIntExtra("fromWhere", -1);
        listType = getIntent().getIntExtra("listType", -1);
        dbHandler = DBHandler.getInstance(context);
        aQuery = Aquery.getInstance(context);

        viewTransparent = findViewById(R.id.viewTransparent);

        if (fromWhere == Constant.DETAILS_FROM_LIST) {
            quickActionForList = new QuickActionForList(context, dbHandler, publication, listType);
            quickActionForList.setOnDismissListener(new QuickActionForList.OnDismissListener() {
                @Override
                public void onDismiss() {
                    viewTransparent.setVisibility(View.INVISIBLE);
                }
            });
        }
        else if (fromWhere == Constant.DETAILS_FROM_POST) {
            quickAction = new QuickActionForPost(context, dbHandler, publication);
            quickAction.setOnDismissListener(new QuickActionForPost.OnDismissListener() {
                @Override
                public void onDismiss() {
                    viewTransparent.setVisibility(View.INVISIBLE);
                }
            });
        }

        llFooterLike = (LinearLayout) findViewById(R.id.llFooterLike);
        llFooterAlreadyLiked = (LinearLayout) findViewById(R.id.llFooterAlreadyLiked);
        llFooterShare = (LinearLayout) findViewById(R.id.llFooterShare);
        llFooterAddToList = (LinearLayout) findViewById(R.id.llFooterAddToList);
        llHeaderBack = (LinearLayout) findViewById(R.id.llHeaderBack);
        filesLayout = (LinearLayout) findViewById(R.id.filesLayout);
        rlFooter = (RelativeLayout) findViewById(R.id.rlFooter);

        llFooterLike.setOnClickListener(this);
        llFooterAlreadyLiked.setOnClickListener(this);
        llFooterShare.setOnClickListener(this);
        llFooterAddToList.setOnClickListener(this);
        llHeaderBack.setOnClickListener(this);

        buttonOpenPDF = (Button) findViewById(R.id.buttonOpenPDF);
        buttonOpenPDF.setOnClickListener(this);

        if (isPublicationHasFile()) {
            checkIfPDFAlreadyDownloaded();
        } else {
            buttonOpenPDF.setText(getString(R.string.no_doc));
        }

        webView = (WebView) findViewById(R.id.wvPublicationDetailContentOfPublication);
        webView.loadData(publication.getYcontent(), "text/html; charset=UTF-8", null);

        titleOfPublication = (TextView) findViewById(R.id.tvPublicationDetailTitleOfPublication);
        titleOfPublication.setText(publication.getYtitle());

        timeOfPublication = (TextView) findViewById(R.id.tvPublicationDetailTimeInformationOfPublication);
        timeOfPublication.setText(publication.getYdate() + " - " + publication.getYtype());

        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_LIKE, publication.getId(), llFooterLike, llFooterAlreadyLiked);
    }

    @Override
    public void onClick(View view) {

        if (Splash.isUserLoggedIn) {

            if (view == llFooterLike) {

                try {
                    dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_LIKE);
                    LikeOperation.doLike(Publication.toDBData(publication));
                } catch (JSONException e) {
                    Logs.e(TAG, "ERROR on like", e);
                }

                Util.changeVisibility(llFooterLike);
                Util.changeVisibility(llFooterAlreadyLiked);

            } else if (view == llFooterAlreadyLiked) {
                Util.changeVisibility(llFooterLike);
                Util.changeVisibility(llFooterAlreadyLiked);

            } else if (view == llFooterShare) {
                String url = Constant.SHARE_PUBLICATION + publication.getYayin_id();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, publication.getYtitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, publication.getYtitle() + " " + url);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
            } else if (view == llFooterAddToList) {

                viewTransparent.setVisibility(View.VISIBLE);

                if (quickActionForList != null) {
                    quickActionForList.show(rlFooter);
                    quickActionForList.setAnimStyle(QuickActionForPost.ANIM_GROW_FROM_CENTER);
                } else if (quickAction != null) {
                    quickAction.show(rlFooter);
                    quickAction.setAnimStyle(QuickActionForPost.ANIM_GROW_FROM_CENTER);
                }
            } else if (view == llHeaderBack) {
                onBackPressed();
            } else if (view == buttonOpenPDF) {
                openPDFAction(downloadedPDF);
            }
        } else {

            if (view == llHeaderBack) {
                onBackPressed();
            } else if (view == buttonOpenPDF) {
                openPDFAction(downloadedPDF);
            } else {
                AlertDialogManager alertDialogManager = new AlertDialogManager();
                alertDialogManager.showLoginDialog(context, getString(R.string.warning), getString(R.string.must_log_in), false);
            }
        }

    }

    void openPDFAction(File file) {

        if (file != null) {
            try {
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } catch (ActivityNotFoundException ignored) {

            }
        }
    }

    boolean isPublicationHasFile() {
        return (publication.getFiles().size() > 0);
    }

    void checkIfPDFAlreadyDownloaded() {

        File ext = Environment.getExternalStorageDirectory();
        File newFile = new File(ext, Constant.PDF_TARGET + publication.getFiles().get(0).getName());
        Logs.i(TAG, "target : " + newFile);

        if (!newFile.exists()) {
            Logs.i(TAG, newFile.getName() + " doesn't exist, it will download from server");
            downloadPDFWithAQuery();
        } else {

            Logs.i(TAG, newFile.getName() + " exist, it will load from file target");
            downloadedPDF = newFile;
            buttonOpenPDF.setText(getResources().getString(R.string.open_doc));

            openPDFAction(downloadedPDF);
        }
    }

    void downloadPDFWithAQuery() {

        File ext = Environment.getExternalStorageDirectory();
        File target = new File(ext, Constant.PDF_TARGET + publication.getFiles().get(0).getName());

        aQuery.download(publication.getFiles().get(0).getUrl(), target, new AjaxCallback<File>() {

            @Override
            public void callback(String url, File file, AjaxStatus status) {

                if (file != null) {
                    downloadedPDF = file;
                    buttonOpenPDF.setText(getString(R.string.open_doc));
                    openPDFAction(downloadedPDF);
                } else {
                    buttonOpenPDF.setText(getString(R.string.not_found_doc));
                }
            }
        });
    }
}