package com.barebrains.gyanith20.statics;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.GyanithUser;
import com.barebrains.gyanith20.others.NetworkChangeReceiver;

import java.util.Objects;
import java.util.Random;

public class NetworkManager {

    private static final String NETWORK_CHANGE_ACTION = "com.barebrains.gyanith20.NETWORK_CHANGED";

    public static Boolean internet_value = false;

    public static MutableLiveData<Boolean> internet = new MutableLiveData<>();
    public static void init(final Context context){
        internet.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean internet) {
                Resource<GyanithUser> res = GyanithUserManager.loggedUser_value;
                internet_value = internet;
                if (!internet) {
                    if (res == null || res.value == null)
                        Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Offline Mode", Toast.LENGTH_SHORT).show();
                }

            }
        });

            IntentFilter filter = new IntentFilter();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                registerNetworkCallbacks(context);
                filter.addAction(NETWORK_CHANGE_ACTION);
            } else {
                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            }

            context.registerReceiver(new NetworkChangeReceiver(), filter);
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
        if (internet_value == null || internet_value != state )
            internet.postValue(state);
    }



    //The value returned by this is the truth
    @Deprecated
    private static void rectifyInternet()
    {
        String url = "http://numbersapi.com/" + (new Random()).nextInt(1000) + "/math";
        StringRequest request = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setInternet(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setInternet(false);
            }
        });
        VolleyManager.requestQueue.add(request);
    }

}
