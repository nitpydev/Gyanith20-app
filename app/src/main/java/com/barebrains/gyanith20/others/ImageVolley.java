package com.barebrains.gyanith20.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

//singletonVolley class for loading image icons

public class ImageVolley {

    private static ImageVolley imagevolley;
    private  static Context mcontext;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;


    private ImageVolley(Context context) {
        this.mcontext = context;
        this.requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized ImageVolley getInstance(Context context) {
        if (imagevolley == null) {
            imagevolley = new ImageVolley(context);
        }
        return imagevolley;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            Cache cache = new DiskBasedCache(mcontext.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }


}
