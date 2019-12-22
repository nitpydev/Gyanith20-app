package com.barebrains.gyanith20.others;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;

import com.barebrains.gyanith20.interfaces.NetworkStateListener;
import com.barebrains.gyanith20.statics.NetworkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetworkChangeReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(final Context context, final Intent intent) {
        NetworkManager.getInstance().OnnetworkChange();
    }
}