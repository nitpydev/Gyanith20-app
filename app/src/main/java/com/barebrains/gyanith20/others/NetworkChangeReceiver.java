package com.barebrains.gyanith20.others;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.barebrains.gyanith20.statics.NetworkManager;

public class NetworkChangeReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (context != null)
            NetworkManager.setInternet(isNetworkAvailable(context));
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}