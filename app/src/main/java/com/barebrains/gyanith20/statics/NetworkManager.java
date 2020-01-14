package com.barebrains.gyanith20.statics;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.NetworkStateListener;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.others.NetworkChangeReceiver;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//TODO: THIS CLASS SHOULD USE LIVEDATA TO RESPOND TO OBSERVERS THIS OLD APPROACH PRONE TO ERRORS LIKE NOT RUNNING ON UI THREAD AND PROBLEM WHEN NOT REMOVED OBSERVER
public class NetworkManager {
    private static NetworkManager instance;

    private Map<Integer, NetworkStateListener> listeners;
    private ArrayList<CompletionListener> netAvaillisteners = new ArrayList<>();

    private ConnectivityManager cm;

    private boolean callbackRegistered;
    private boolean lastAvailability = false;


    public static NetworkManager getInstance(){
        if (instance == null)
            instance = new NetworkManager();
        if (instance.listeners == null)
            instance.listeners = new HashMap<>();

        if (!instance.callbackRegistered && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            instance.registerCallback();
        return instance;
    }

    public static void initialize(Context context){
        if (instance == null)
            instance = new NetworkManager();
        if (instance.cm == null)
            instance.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        rectifyInternet();
    }

    public void completeOnNetAvailable(CompletionListener listener){
        if (!isNetAvailable()) {
            netAvaillisteners.add(listener);
        }else {
            rectifyInternet();
            listener.OnComplete();
        }
    }

    public void addListener(Integer code,NetworkStateListener listener){
        respondListener(listener,lastAvailability);
        listeners.put(code,listener);
    }

    public void removeListener(Integer code){
        if (listeners.containsKey(code))
            listeners.remove(code);
    }

    private void respondListeners(boolean isAvailable){

        for (NetworkStateListener listener : listeners.values()) {
            respondListener(listener,isAvailable);
        }

        if (isAvailable) {
            for (CompletionListener listener : netAvaillisteners) {
                listener.OnComplete();
                netAvaillisteners.remove(listener);
            }
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


    //The value returned by this is the truth
    private static void rectifyInternet()
    {
        String url = "http://numbersapi.com/" + (new Random()).nextInt(1000) + "/math";
        StringRequest request = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                instance.lastAvailability = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                instance.lastAvailability = false;
            }
        });
        VolleyManager.requestQueue.add(request);
    }

}
