package com.tepav.reader.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

/**
 * Author : kanilturgut
 * Date : 17.04.2014
 * Time : 22:22
 */
public class Cache {

    private static Cache instance = null;
    private static final int cacheSize = 4 * 1024 * 1024; //4MiB
    private LruCache<String, Bitmap> cache = null;
    private final static String TAG = "Cache";

    private Cache() {
        cache = new LruCache<String, Bitmap>(cacheSize);
    }

    public static Cache getInstance() {
        if (instance == null)
            instance = new Cache();

        return instance;
    }

    public void getImageFromCache(Context context, String url, ImageView iv) {
        Log.d(TAG, "Cache URL: " + url);
        Bitmap bmp;
        synchronized (cache) {
            bmp = cache.get(url);
        }
        if (bmp != null) {
            Log.d(TAG, "Cache HIT: " + url);
            iv.setImageBitmap(bmp);
        } else {
            Log.d(TAG, "Cache MISS: " + url);



        }
    }
}