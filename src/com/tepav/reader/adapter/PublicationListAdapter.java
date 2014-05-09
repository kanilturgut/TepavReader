package com.tepav.reader.adapter;

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
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.model.Publication;
import com.tepav.reader.service.TepavService;
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
    TepavService tepavService = null;
    ProgressDialog progressDialog = null;

    boolean isPressedLike = false;
    boolean isPressedFavorite = false;
    boolean isPressedReadList = false;

    public PublicationListAdapter(Context ctx, String type, int number) {
        super(ctx, R.layout.custom_publication_row);

        this.context = ctx;
        this.publicationType = type;
        this.pageNumber = number;

        dbHandler = DBHandler.getInstance(context);
        aq = new AQuery(context);
        tepavService = TepavService.getInstance();
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

        if (tepavService != null) {
            isPressedFavorite = tepavService.checkIfContains(DBHandler.TABLE_FAVORITE, publication.getId());
            isPressedReadList = tepavService.checkIfContains(DBHandler.TABLE_READ_LIST, publication.getId());
            isPressedLike = tepavService.checkIfContains(DBHandler.TABLE_LIKE, publication.getId());
        }

        if (isPressedFavorite)
            holder.ibFavorite.setImageResource(R.drawable.swipe_favorites_dolu);
        else
            holder.ibFavorite.setImageResource(R.drawable.swipe_favorites);

        if (isPressedReadList)
            holder.ibReadList.setImageResource(R.drawable.okudum_icon_dolu);
        else
            holder.ibReadList.setImageResource(R.drawable.okudum_icon);

        if (isPressedLike)
            holder.ibLike.setImageResource(R.drawable.swipe_like_dolu);
        else
            holder.ibLike.setImageResource(R.drawable.swipe_like);

        holder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Constant.SHARE_NEWS + publication.getYayin_id();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, publication.getYtitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT,  publication.getYtitle() + " " + url);
                context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
            }
        });

        holder.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isPressedFavorite) {
                    try {
                        dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_FAVORITE);
                        tepavService.addItemToFavoriteListOfTepavService(Publication.toDBData(publication));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_FAVORITE);
                        tepavService.removeItemFromFavoriteListOfTepavService(Publication.toDBData(publication));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                isPressedFavorite = !isPressedFavorite;
                ImageButton imageButton = (ImageButton) view;
                if (isPressedFavorite)
                    imageButton.setImageResource(R.drawable.swipe_favorites_dolu);
                else
                    imageButton.setImageResource(R.drawable.swipe_favorites);

            }
        });

        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isPressedLike) {
                    try {
                        dbHandler.insert(Publication.toDBData(publication),DBHandler.TABLE_LIKE);
                        tepavService.addItemToLikeListOfTepavService(Publication.toDBData(publication));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        dbHandler.delete(Publication.toDBData(publication),DBHandler.TABLE_LIKE);
                        tepavService.removeItemFromLikeListOfTepavService(Publication.toDBData(publication));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                isPressedLike = !isPressedLike;
                ImageButton imageButton = (ImageButton) view;
                if (!isPressedLike)
                    imageButton.setImageResource(R.drawable.swipe_like);
                else
                    imageButton.setImageResource(R.drawable.swipe_like_dolu);
            }
        });

        holder.ibReadList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isPressedReadList) {
                    try {
                        dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_READ_LIST);
                        tepavService.addItemToReadingListOfTepavService(Publication.toDBData(publication));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_READ_LIST);
                        tepavService.removeItemFromReadingListOfTepavService(Publication.toDBData(publication));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                isPressedReadList = !isPressedReadList;
                ImageButton imageButton = (ImageButton) view;
                if (isPressedReadList)
                    imageButton.setImageResource(R.drawable.okudum_icon_dolu);
                else
                    imageButton.setImageResource(R.drawable.okudum_icon);
            }
        });
        holder.frontOfPublicationClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PublicationDetails.class);
                intent.putExtra("class", publication);
                context.startActivity(intent);
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
                            }
                            else if (tmpPub.getYtype().equals(publicationType))
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
}
