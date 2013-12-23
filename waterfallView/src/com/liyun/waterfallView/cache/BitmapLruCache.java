package com.liyun.waterfallView.cache;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.liyun.waterfallView.tool.EnvironmentHelper;
import com.liyun.waterfallView.tool.Logger;
import com.liyun.waterfallView.tool.NetworkHelper;
import com.liyun.waterfallView.tool.StringHashHelper;


public class BitmapLruCache implements ImageLoader.ImageCache {
    private final Context mContext;
    private final Bitmap mNotAvailableBitmap;
    private static BitmapDiskLruCache mDiskLruCache;
    private LruCache<String, Bitmap> mLruCache;


    public BitmapLruCache(Context context) {
        mContext = context;
        mLruCache = new LruCache<String, Bitmap>(EnvironmentHelper.getMemoryCacheSize());

        try {
            mDiskLruCache = new BitmapDiskLruCache(
                    EnvironmentHelper.getCacheDir(mContext),
                    EnvironmentHelper.getDiskCacheSize(mContext));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mNotAvailableBitmap = getDefaultBitmap();
    }

    private Bitmap getDefaultBitmap() {
        try {
            InputStream bitmap = mContext.getAssets().open("keep-calm.png");
            return BitmapFactory.decodeStream(bitmap);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Bitmap getBitmap(String url) {
        String key = StringHashHelper.md5(url);
        Bitmap data = mLruCache.get(key);
        if (data == null) {
            try {
                data = mDiskLruCache.get(key);
                if (data != null) {
                    Logger.i("Disk cache hitted, put to memory cache.");
                    mLruCache.put(key, data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Logger.i("Memory cache hitted, directly return.");
        }


        if (data == null && !NetworkHelper.isWifiConnected(mContext)) {
            return mNotAvailableBitmap;
        }

        return data;
    }


    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        String key = StringHashHelper.md5(url);
        mLruCache.put(key, bitmap);

        if (mDiskLruCache.isContains(key)) {
            Logger.i("Local disk cache is exists, ignore.");
            return;
        }

        try {
            Logger.i("Save bitmap data to disk cache.");
            mDiskLruCache.put(key, bitmap);
        } catch (IOException e) {
            Logger.e(e.getMessage());
        }
    }
}
