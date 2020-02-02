package com.barebrains.gyanith20.interfaces;

import android.webkit.WebView;

import com.barebrains.gyanith20.activities.Web;

public interface UrlLoadListener {
    void onLoad(Web web, WebView view, String url);

}
