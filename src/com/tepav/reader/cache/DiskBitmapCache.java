package com.tepav.reader.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * Author : kanilturgut
 * Date : 17.04.2014
 * Time : 15:26
 */
public class DiskBitmapCache extends DiskBasedCache implements ImageLoader.ImageCache {

    public DiskBitmapCache(File rootDirectory, int maxCacheSizeInBytes) {
        super(rootDirectory, maxCacheSizeInBytes);
    }

    public DiskBitmapCache(File cacheDir) {
        super(cacheDir);
    }

    public Bitmap getBitmap(String url) {

        final String volleyFileName = getFilenameForKey(url);

        try {
            return BitmapFactory.decodeFile(volleyFileName);
        } catch (Exception e) {
            return null;
        }
    }

    public void putBitmap(String url, Bitmap bitmap) {
        final Entry entry = new Entry();

        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(buffer);
        entry.data = buffer.array();

        put(url, entry);
    }

    // Volley creates a filename for the url with the following function, so we'll use the same function
    // for translating the url back to said filename
    private String getFilenameForKey(String key) {
        int firstHalfLength = key.length() / 2;
        String localFilename = String.valueOf(key.substring(0, firstHalfLength).hashCode());
        localFilename += String.valueOf(key.substring(firstHalfLength).hashCode());
        return localFilename;
    }
}