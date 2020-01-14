package com.barebrains.gyanith20.statics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.NetworkStateListener;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.EventItem;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.others.NetworkChangeReceiver;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class NetworkManager {

    private static final String NETWORK_CHANGE_ACTION = "com.barebrains.gyanith20.NETWORK_CHANGED";

    public static MutableLiveData<Boolean> internet = new MutableLiveData<>();

    public static void init(final Context context){
            IntentFilter filter = new IntentFilter();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                registerNetworkCallbacks(context);
                filter.addAction(NETWORK_CHANGE_ACTION);
            } else {
                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            }

            context.registerReceiver(new NetworkChangeReceiver(), filter);


            internet.observeForever(new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean internet) {
                    Resource<GyanithUser> res = GyanithUserManager.getCurrentUser().getValue();
                    if (!internet) {
                        if (res == null || res.value == null)
                            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Offline Mode", Toast.LENGTH_SHORT).show();
                    }

                }
            });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void registerNetworkCallbacks(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Objects.requireNonNull(cm).registerNetworkCallback( new NetworkRequest.Builder().build(),new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                setInternet(true);
            }

            @Override
            public void onLost(@NonNull Network network) {
               setInternet(false);
            }
        });
    }


    public static void setInternet(boolean state){
        Log.d("asd","internet : " + state);
        if (internet.getValue() == null || internet.getValue() != state )
            internet.postValue(state);
    }


















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
@Deprecated
    public static void initialize(Context context){
        if (instance == null)
            instance = new NetworkManager();
        if (instance.cm == null)
            instance.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        rectifyInternet();
    }

    @Deprecated
    public void completeOnNetAvailable(CompletionListener listener){
        if (!isNetAvailable()) {
            netAvaillisteners.add(listener);
        }else {
            rectifyInternet();
            listener.OnComplete();
        }
    }

    @Deprecated
    public void addListener(Integer code,NetworkStateListener listener){
        respondListener(listener,lastAvailability);
        listeners.put(code,listener);
    }

    @Deprecated
    public void removeListener(Integer code){
        if (listeners.containsKey(code))
            listeners.remove(code);
    }

    @Deprecated
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
    @Deprecated
    private void respondListener(NetworkStateListener listener, boolean isAvailable){
        listener.OnChange();
        if (isAvailable)
            listener.OnAvailable();
        else
            listener.OnDisconnected();
    }

    @Deprecated
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
