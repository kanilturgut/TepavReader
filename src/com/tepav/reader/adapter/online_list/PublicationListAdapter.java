package com.tepav.reader.adapter.online_list;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.tepav.reader.R;
import com.tepav.reader.activity.PublicationDetails;
import com.tepav.reader.activity.Splash;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.model.News;
import com.tepav.reader.model.Publication;
import com.tepav.reader.service.OfflineList;
import com.tepav.reader.service.TepavService;
import com.tepav.reader.util.AlertDialogManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Author : kanilturgut
 * Date : 21.04.2014
 * Time : 21:43
 */
public class PublicationListAdapter extends ArrayAdapter<Publication> {

    Context context;
    List<Publication> publicationList = new LinkedList<Publication>();
    int pageNumber;
    AQuery aq;
    DBHandler dbHandler;
    String publicationType;
    OfflineList offlineList;
    ProgressDialog progressDialog = null;

    public PublicationListAdapter(Context ctx, String type, int number) {
        super(ctx, R.layout.custom_publication_row);

        this.context = ctx;
        this.publicationType = type;
        this.pageNumber = number;

        dbHandler = DBHandler.getInstance(context);
        aq = new AQuery(context);
        offlineList = OfflineList.getInstance(context);
        progressDialog = ProgressDialog.show(context, context.getString(R.string.please_wait),
                context.getString(R.string.loading), false, false);
        loadMore();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final PublicationHolder holder;
        final Publication publication = publicationList.get(position);

        if (position == (publicationList.size() - 1))
            loadMore();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_publication_row, parent, false);

            holder = new PublicationHolder();

            //front view
            holder.frontOfPublicationClick = (RelativeLayout) convertView.findViewById(R.id.frontOfPublicationClick);
            holder.titleOfPublication = (TextView) convertView.findViewById(R.id.tvTitleOfPublication);
            holder.dateOfPublication = (TextView) convertView.findViewById(R.id.tvDateOfPublication);

            //back view
            holder.ibShare = (ImageButton) convertView.findViewById(R.id.ibShare);
            holder.ibLike = (ImageButton) convertView.findViewById(R.id.ibLike);
            holder.ibFavorite = (ImageButton) convertView.findViewById(R.id.ibFavorite);
            holder.ibReadList = (ImageButton) convertView.findViewById(R.id.ibReadList);

            convertView.setTag(holder);

        } else {
            holder = (PublicationHolder) convertView.getTag();
        }

        holder.titleOfPublication.setText(publication.getYtitle());
        holder.dateOfPublication.setText(publication.getYdate() + ", " + publication.getYtype());

        // swipe list icons
        if (offlineList != null) {
            if (checkDB(publication, DBHandler.TABLE_FAVORITE))
                holder.ibFavorite.setImageResource(R.drawable.swipe_favorites_dolu);
            else
                holder.ibFavorite.setImageResource(R.drawable.swipe_favorites);

            if (checkDB(publication, DBHandler.TABLE_READ_LIST))
                holder.ibReadList.setImageResource(R.drawable.okudum_icon_dolu);
            else
                holder.ibReadList.setImageResource(R.drawable.okudum_icon);

            if (checkDB(publication, DBHandler.TABLE_LIKE))
                holder.ibLike.setImageResource(R.drawable.swipe_like_dolu);
            else
                holder.ibLike.setImageResource(R.drawable.swipe_like);
        }

        holder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    String url = Constant.SHARE_NEWS + publication.getYayin_id();

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, publication.getYtitle());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, publication.getYtitle() + " " + url);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);
                }
            }
        });

        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    if (!checkDB(publication, DBHandler.TABLE_LIKE)) {
                        try {
                            dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_LIKE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_LIKE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ImageButton imageButton = (ImageButton) view;
                    if (!checkDB(publication, DBHandler.TABLE_LIKE))
                        imageButton.setImageResource(R.drawable.swipe_like);
                    else
                        imageButton.setImageResource(R.drawable.swipe_like_dolu);
                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);

                }
            }
        });

        holder.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    if (!checkDB(publication, DBHandler.TABLE_FAVORITE)) {
                        try {
                            dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_FAVORITE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_FAVORITE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ImageButton imageButton = (ImageButton) view;
                    if (checkDB(publication, DBHandler.TABLE_FAVORITE))
                        imageButton.setImageResource(R.drawable.swipe_favorites_dolu);
                    else
                        imageButton.setImageResource(R.drawable.swipe_favorites);
                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);

                }

            }
        });


        holder.ibReadList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {


                    if (!checkDB(publication, DBHandler.TABLE_READ_LIST)) {
                        try {
                            dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_READ_LIST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_READ_LIST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ImageButton imageButton = (ImageButton) view;
                    if (checkDB(publication, DBHandler.TABLE_READ_LIST))
                        imageButton.setImageResource(R.drawable.okudum_icon_dolu);
                    else
                        imageButton.setImageResource(R.drawable.okudum_icon);
                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);
                }
            }
        });
        holder.frontOfPublicationClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PublicationDetails.class);
                try {
                    intent.putExtra("class", Publication.toDBData(publication));
                    intent.putExtra("fromWhere", Constant.DETAILS_FROM_POST);
                    context.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    class PublicationHolder {

        TextView titleOfPublication;
        TextView dateOfPublication;
        ImageButton ibShare;
        ImageButton ibLike;
        ImageButton ibFavorite;
        ImageButton ibReadList;
        RelativeLayout frontOfPublicationClick;
    }

    public void loadMore() {

        JSONObject params = new JSONObject();
        try {
            params.put("pageNumber", pageNumber);
        } catch (JSONException e) {
            params = null;
            e.printStackTrace();
        }

        aq.post(HttpURL.createURL(HttpURL.publication), params, JSONArray.class, new AjaxCallback<JSONArray>() {

            @Override
            public void callback(String url, JSONArray object, AjaxStatus status) {

                List<Publication> temp = new LinkedList<Publication>();

                if (object != null && object.length() != 0) {
                    for (int i = 0; i < object.length(); i++) {
                        try {
                            Publication tmpPub = Publication.fromJSON(object.getJSONObject(i));
                            if (publicationType.equals(context.getString(R.string.Research_And_Publications)))
                                temp.add(tmpPub);
                            else if (publicationType.equals(context.getString(R.string.Reports_And_Printed_Publications))) {
                                if (tmpPub.getYtype().equals(Constant.REPORTS) || tmpPub.getYtype().equals(Constant.PRINTED_PUBLICATIONS))
                                    temp.add(tmpPub);
                            } else if (tmpPub.getYtype().equals(publicationType))
                                temp.add(tmpPub);

                            tmpPub = null;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    publicationList.addAll(temp);
                    addAll(temp);
                    notifyDataSetChanged();

                    pageNumber++;
                }

                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
    }

    boolean checkDB(Publication publication, String table) {
        return offlineList.checkIfContains(table, publication.getId());
    }
}
