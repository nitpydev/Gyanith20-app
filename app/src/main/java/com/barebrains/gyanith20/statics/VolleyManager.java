package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.webkit.CookieManager;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import java.net.CookieHandler;
import java.net.CookiePolicy;

public class VolleyManager {
    public static RequestQueue requestQueue;

    public static void setRequestQueue(Context context){
        if (requestQueue != null)
            return;


         //new CookieManager( cookies.getInstance(), CookiePolicy.ACCEPT_ALL );
        CookieHandler.setDefault( new WebkitCookieManagerProxy(cookies.getInstance(),CookiePolicy.ACCEPT_ALL));

        Cache cache = new DiskBasedCache(context.getCacheDir(), 10 * 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
    }
}

