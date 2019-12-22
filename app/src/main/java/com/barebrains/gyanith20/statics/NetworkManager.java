package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.barebrains.gyanith20.interfaces.NetworkStateListener;
import com.barebrains.gyanith20.others.NetworkChangeReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager {
    private static NetworkManager instance;

    private Map<Integer, NetworkStateListener> listeners;
    private ArrayList<NetworkStateListener> listenersUnmapped;

    private ConnectivityManager cm;

    private boolean callbackRegistered;
    private boolean lastAvailability = false;


    public static NetworkManager getInstance(){
        if (instance == null)
            instance = new NetworkManager();
        if (instance.listeners == null)
            instance.listeners = new HashMap<>();
        if (instance.listenersUnmapped == null)
            instance.listenersUnmapped = new ArrayList<>();

        if (!instance.callbackRegistered && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            instance.registerCallback();


        return instance;
    }

    public static void initialize(Context context){
        if (instance == null)
            instance = new NetworkManager();
        if (instance.cm == null)
            instance.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void addListener(NetworkStateListener listener){
        respondListener(listener,lastAvailability);
        listenersUnmapped.add(listener);
    }
    public void addListener(Integer code,NetworkStateListener listener){
        respondListener(listener,lastAvailability);
        listeners.put(code,listener);
    }

    public void removeListener(NetworkStateListener listener){
        listenersUnmapped.remove(listener);
    }
    public void removeListener(Integer code){
        listeners.remove(code);
    }

    private void respondListeners(boolean isAvailable){
        for (NetworkStateListener listener : listenersUnmapped) {
           respondListener(listener,isAvailable);
        }

        for (NetworkStateListener listener : listeners.values()) {
            respondListener(listener,isAvailable);
        }
    }
    private void respondListener(NetworkStateListener listener, boolean isAvailable){
        listener.OnChange();
        if (isAvailable)
            listener.OnAvailable();
        else
            listener.OnDisconnected();
    }

    public boolean isNetAvailable(){
        return lastAvailability;
    }


    //For Api level Greater than 29
    private ConnectivityManager.NetworkCallback networkCallback;


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void registerCallback(){
        if (networkCallback == null)
            networkCallback = new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull Network network) {
                    lastAvailability = true;
                    respondListeners(true);
                }

                @Override
                public void onLost(@NonNull Network network) {
                    lastAvailability = false;
                    respondListeners(false);
                }
            };
        cm.registerDefaultNetworkCallback(networkCallback);
        callbackRegistered = true;
    }


    //For API level less than 29
    public void OnnetworkChange(){
        respondListeners(isNetworkAvailableOld());
    }

    boolean isNetworkAvailableOld(){
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }



}
