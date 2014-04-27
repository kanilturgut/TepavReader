package com.tepav.reader.adapter;

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
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.Util;
import com.tepav.reader.model.Publication;
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

    public PublicationListAdapter(Context ctx, int number) {
        super(ctx, R.layout.custom_publication_row);

        this.context = ctx;
        this.pageNumber = number;

        dbHandler = DBHandler.getInstance(context);
        aq = new AQuery(context);
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
            holder.ibFavorite = (ImageButton) convertView.findViewById(R.id.ibFavorite);
            holder.ibFavorited = (ImageButton) convertView.findViewById(R.id.ibFavorited);
            holder.ibReadList = (ImageButton) convertView.findViewById(R.id.ibReadList);
            holder.ibReadListed = (ImageButton) convertView.findViewById(R.id.ibReadListed);

            convertView.setTag(holder);

        } else {
            holder = (PublicationHolder) convertView.getTag();
        }

        holder.titleOfPublication.setText(publication.getYtitle());
        holder.dateOfPublication.setText(publication.getYdate());

        MyOnClickListener myOnClickListener = new MyOnClickListener(position);
        holder.ibShare.setOnClickListener(myOnClickListener);
        holder.ibFavorite.setOnClickListener(myOnClickListener);
        holder.ibReadList.setOnClickListener(myOnClickListener);
        holder.ibFavorited.setOnClickListener(myOnClickListener);
        holder.ibReadListed.setOnClickListener(myOnClickListener);
        holder.frontOfPublicationClick.setOnClickListener(myOnClickListener);

        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_FAVORITE, publication.getId(), holder.ibFavorite, holder.ibFavorited);
        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_READ_LIST, publication.getId(), holder.ibReadList, holder.ibReadListed);

        return convertView;
    }

    class MyOnClickListener implements View.OnClickListener {

        int position;

        public MyOnClickListener(int pos) {
            this.position = pos;
        }

        @Override
        public void onClick(View view) {

            Publication publication = publicationList.get(position);

            switch (view.getId()) {
                case R.id.ibShare:
                    String url = Constant.SHARE_NEWS + publication.getYayin_id();

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, publication.getYtitle());
                    shareIntent.putExtra(Intent.EXTRA_TEXT,  publication.getYtitle() + " " + url);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
                    break;
                case R.id.ibFavorite:

                    try {
                        dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_FAVORITE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.ibFavorited:

                    try {
                        dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_FAVORITE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.ibReadList:

                    try {
                        dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_READ_LIST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.ibReadListed:
                    try {
                        dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_READ_LIST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.frontOfPublicationClick:

                    //Intent intent = new Intent(context, PublicationDetails.class);
                    //intent.putExtra("class", publication);
                    //context.startActivity(intent);
                    break;
            }
        }
    }

    class PublicationHolder {

        TextView titleOfPublication;
        TextView dateOfPublication;
        ImageButton ibShare;
        ImageButton ibFavorite;
        ImageButton ibFavorited;
        ImageButton ibReadList;
        ImageButton ibReadListed;
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
                            temp.add(Publication.fromJSON(object.getJSONObject(i)));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    publicationList.addAll(temp);
                    addAll(temp);
                    notifyDataSetChanged();

                    pageNumber++;
                }
            }
        });
    }
}
