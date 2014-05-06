package com.tepav.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.tepav.reader.R;
import com.tepav.reader.activity.BlogDetails;
import com.tepav.reader.activity.NewsDetails;
import com.tepav.reader.activity.PublicationDetails;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.roundedimageview.RoundedImageView;
import com.tepav.reader.helpers.swipelistview.SwipeListView;
import com.tepav.reader.model.Blog;
import com.tepav.reader.model.DBData;
import com.tepav.reader.model.News;
import com.tepav.reader.model.Publication;
import com.tepav.reader.service.TepavService;
import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

/**
 * Author : kanilturgut
 * Date : 29.04.2014
 * Time : 16:46
 */
public class ReadListListAdapter extends ArrayAdapter<DBData> {

    String TAG = "ReadListListAdapter";
    Context context;
    DBHandler dbHandler;
    List<DBData> dbDataList;
    AQuery aq;
    TepavService tepavService = null;
    SwipeListView swipeListView;

    boolean isPressedLike = false;
    boolean isPressedFavorite = false;
    boolean isPressedArchive = false;

    News news = null;
    Blog blog = null;
    Publication publication = null;

    public ReadListListAdapter(Context context, SwipeListView swipeListView, List<DBData> dbDataList) {
        super(context, R.layout.custom_read_list_row, dbDataList);

        this.context = context;
        this.swipeListView = swipeListView;
        this.dbDataList = dbDataList;

        dbHandler = DBHandler.getInstance(context);
        aq = new AQuery(context);
        tepavService = TepavService.getInstance();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        isPressedLike = false;
        isPressedFavorite = false;
        isPressedArchive = false;

        ReadListHolder holder;
        final DBData dbData = dbDataList.get(position);

        if (dbData.getType() == DBData.TYPE_NEWS) {
            try {
                news = News.fromDBData(dbData);
                blog = null;
                publication = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (dbData.getType() == DBData.TYPE_BLOG) {
            try {
                blog = Blog.fromDBData(dbData);
                news = null;
                publication = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (dbData.getType() == DBData.TYPE_PUBLICATION) {
            try {
                publication = Publication.fromDBData(dbData);
                news = null;
                blog = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_read_list_row, parent, false);

            holder = new ReadListHolder();

            //front view
            holder.frontOfReadListClick = (RelativeLayout) convertView.findViewById(R.id.frontOfReadListClick);
            holder.imageOfReadList = (RoundedImageView) convertView.findViewById(R.id.ivImageOfReadList);
            holder.titleOfReadList = (TextView) convertView.findViewById(R.id.tvTitleOfReadList);
            holder.dateOfReadList = (TextView) convertView.findViewById(R.id.tvDateOfReadList);

            //back view
            holder.ibShare = (ImageButton) convertView.findViewById(R.id.ibShare);
            holder.ibLike = (ImageButton) convertView.findViewById(R.id.ibLike);
            holder.ibFavorite = (ImageButton) convertView.findViewById(R.id.ibFavorite);
            holder.ibArchive = (ImageButton) convertView.findViewById(R.id.ibArchive);
            holder.ibDelete = (ImageButton) convertView.findViewById(R.id.ibDelete);

            convertView.setTag(holder);

        } else {
            holder = (ReadListHolder) convertView.getTag();
        }

        ImageOptions options = new ImageOptions();
        options.fileCache = true;
        options.memCache = true;
        options.targetWidth = 0;
        options.fallback = R.drawable.no_image;

        if (news != null) {

            holder.titleOfReadList.setText(news.getHtitle());
            holder.dateOfReadList.setText(news.getHdate());

            if (tepavService != null) {
                isPressedFavorite = tepavService.checkIfContains(DBHandler.TABLE_FAVORITE, news.getId());
                isPressedArchive = tepavService.checkIfContains(DBHandler.TABLE_ARCHIVE, news.getId());
                isPressedLike = tepavService.checkIfContains(DBHandler.TABLE_LIKE, news.getId());
            }

            Bitmap bmp = aq.getCachedImage(news.getHimage());
            if (bmp == null) {
                aq.id(holder.imageOfReadList).image(news.getHimage(), options);
                Log.i(TAG, "image received from server");
            } else {
                holder.imageOfReadList.setImageBitmap(bmp);
                Log.i(TAG, "image received from cache");
            }

        } else if (blog != null) {
            holder.titleOfReadList.setText(blog.getBtitle());
            holder.dateOfReadList.setText(blog.getBtitle());

            if (tepavService != null) {
                isPressedFavorite = tepavService.checkIfContains(DBHandler.TABLE_FAVORITE, blog.getId());
                isPressedArchive = tepavService.checkIfContains(DBHandler.TABLE_ARCHIVE, blog.getId());
                isPressedLike = tepavService.checkIfContains(DBHandler.TABLE_LIKE, blog.getId());
            }

            Bitmap bmp = aq.getCachedImage(blog.getPimage());
            if (bmp == null) {
                aq.id(holder.imageOfReadList).image(blog.getPimage(), options);
                Log.i(TAG, "image received from server");
            } else {
                holder.imageOfReadList.setImageBitmap(bmp);
                Log.i(TAG, "image received from cache");
            }

        } else if (publication != null) {
            holder.titleOfReadList.setText(publication.getYtitle());
            holder.dateOfReadList.setText(publication.getYdate() + ", " + publication.getYtype());
            holder.imageOfReadList.setImageResource(R.drawable.no_image);

            if (tepavService != null) {
                isPressedFavorite = tepavService.checkIfContains(DBHandler.TABLE_FAVORITE, publication.getId());
                isPressedArchive = tepavService.checkIfContains(DBHandler.TABLE_ARCHIVE, publication.getId());
                isPressedLike = tepavService.checkIfContains(DBHandler.TABLE_LIKE, publication.getId());
            }
        }

        if (isPressedFavorite)
            holder.ibFavorite.setImageResource(R.drawable.swipe_favorites_dolu);
        else
            holder.ibFavorite.setImageResource(R.drawable.swipe_favorites);

        if (isPressedArchive)
            holder.ibArchive.setImageResource(R.drawable.okudum_icon_dolu);
        else
            holder.ibArchive.setImageResource(R.drawable.okudum_icon);

        if (isPressedLike)
            holder.ibLike.setImageResource(R.drawable.swipe_like_dolu);
        else
            holder.ibLike.setImageResource(R.drawable.swipe_like);

        holder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (news != null) {

                    String url = Constant.SHARE_NEWS + news.getHaber_id();

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, news.getHtitle());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, news.getHtitle() + " " + url);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));

                } else if (blog != null) {
                    String url = Constant.SHARE_BLOG + blog.getGunluk_id();

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, blog.getBtitle());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, blog.getBtitle() + " " + url);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
                } else if (publication != null) {
                    String url = Constant.SHARE_PUBLICATION + publication.getYayin_id();

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, publication.getYtitle());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, publication.getYtitle() + " " + url);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
                }

            }
        });

        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPressedLike) {
                    if (news != null) {
                        try {
                            dbHandler.insert(News.toDBData(news), DBHandler.TABLE_LIKE);
                            tepavService.addItemToLikeListOfTepavService(News.toDBData(news));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (blog != null) {
                        try {
                            dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_LIKE);
                            tepavService.addItemToLikeListOfTepavService(Blog.toDBData(blog));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (publication != null) {
                        try {
                            dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_LIKE);
                            tepavService.addItemToLikeListOfTepavService(Publication.toDBData(publication));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (news != null) {
                        try {
                            dbHandler.delete(News.toDBData(news), DBHandler.TABLE_LIKE);
                            tepavService.removeItemFromLikeListOfTepavService(News.toDBData(news));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (blog != null) {
                        try {
                            dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_LIKE);
                            tepavService.removeItemFromLikeListOfTepavService(Blog.toDBData(blog));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (publication != null) {
                        try {
                            dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_LIKE);
                            tepavService.removeItemFromLikeListOfTepavService(Publication.toDBData(publication));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        holder.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPressedFavorite) {

                    if (news != null) {
                        try {
                            dbHandler.insert(News.toDBData(news), DBHandler.TABLE_FAVORITE);
                            tepavService.addItemToFavoriteListOfTepavService(News.toDBData(news));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (blog != null) {
                        try {
                            dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_FAVORITE);
                            tepavService.addItemToFavoriteListOfTepavService(Blog.toDBData(blog));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (publication != null) {
                        try {
                            dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_FAVORITE);
                            tepavService.addItemToFavoriteListOfTepavService(Publication.toDBData(publication));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    if (news != null) {
                        try {
                            dbHandler.delete(News.toDBData(news), DBHandler.TABLE_FAVORITE);
                            tepavService.removeItemFromFavoriteListOfTepavService(News.toDBData(news));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (blog != null) {
                        try {
                            dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_FAVORITE);
                            tepavService.removeItemFromFavoriteListOfTepavService(Blog.toDBData(blog));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (publication != null) {
                        try {
                            dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_FAVORITE);
                            tepavService.removeItemFromFavoriteListOfTepavService(Publication.toDBData(publication));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        holder.ibArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPressedArchive) {

                    if (news != null) {
                        try {
                            dbHandler.insert(News.toDBData(news), DBHandler.TABLE_ARCHIVE);
                            tepavService.addItemToArchiveListOfTepavService(News.toDBData(news));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (blog != null) {
                        try {
                            dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_ARCHIVE);
                            tepavService.addItemToArchiveListOfTepavService(Blog.toDBData(blog));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (publication != null) {
                        try {
                            dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_ARCHIVE);
                            tepavService.addItemToArchiveListOfTepavService(Publication.toDBData(publication));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    if (news != null) {
                        try {
                            dbHandler.delete(News.toDBData(news), DBHandler.TABLE_ARCHIVE);
                            tepavService.removeItemFromArchiveListOfTepavService(News.toDBData(news));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (blog != null) {
                        try {
                            dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_ARCHIVE);
                            tepavService.removeItemFromArchiveListOfTepavService(Blog.toDBData(blog));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (publication != null) {
                        try {
                            dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_ARCHIVE);
                            tepavService.removeItemFromArchiveListOfTepavService(Publication.toDBData(publication));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                isPressedArchive = !isPressedArchive;
                ImageButton imageButton = (ImageButton) view;
                if (isPressedArchive)
                    imageButton.setImageResource(R.drawable.okudum_icon_dolu);
                else
                    imageButton.setImageResource(R.drawable.okudum_icon);
            }
        });

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (news != null) {
                    try {
                        dbHandler.delete(News.toDBData(news), DBHandler.TABLE_READ_LIST);
                        tepavService.removeItemFromReadingListOfTepavService(News.toDBData(news));
                        dbDataList.remove(News.toDBData(news));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (blog != null) {
                    try {
                        dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_READ_LIST);
                        tepavService.removeItemFromReadingListOfTepavService(Blog.toDBData(blog));
                        dbDataList.remove(Blog.toDBData(blog));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (publication != null) {
                    try {
                        dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_READ_LIST);
                        tepavService.removeItemFromReadingListOfTepavService(Publication.toDBData(publication));
                        dbDataList.remove(Publication.toDBData(publication));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                remove(dbData);
                notifyDataSetChanged();

                swipeListView.closeAnimate(position);
            }
        });

        holder.frontOfReadListClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;

                if (news != null) {
                    intent = new Intent(context, NewsDetails.class);
                    intent.putExtra("class", news);
                } else if (blog != null) {
                    intent = new Intent(context, BlogDetails.class);
                    intent.putExtra("class", blog);
                } else if (publication != null) {
                    intent = new Intent(context, PublicationDetails.class);
                    intent.putExtra("class", publication);
                }

                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ReadListHolder {

        RoundedImageView imageOfReadList;
        TextView titleOfReadList;
        TextView dateOfReadList;
        ImageButton ibShare;
        ImageButton ibLike;
        ImageButton ibFavorite;
        ImageButton ibArchive;
        ImageButton ibDelete;
        RelativeLayout frontOfReadListClick;
    }
}


