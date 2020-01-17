package com.barebrains.gyanith20.others;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.statics.NetworkManager;

import static com.barebrains.gyanith20.gyanith20.appContext;

/**
 * pass this Exception for getting handled by loader
 * this has info about the feedback that should be given to the user
 * code is the errorCode corresponding to a empty state in Loader
 * message is an additional toast that can be shown while displaying along with the above empty state
 *
 *
 * Note :
 * A ErrorCode needs to be present if value is null and a error code must not be present when value is not null
 */

public class Response {

    //RESPONSE CODES
    public static final int DATA_EMPTY = 0;
    public static final int NO_DATA_AND_NET = 1;
    public static final int ILLEGAL_STATE = 2;
    public static final int UNKNOWN_ERROR = -1;

    private Integer code = null;
    private String toast = null;

    public Response(){}

    public Response(@Nullable Integer code) {
        this.code = code;
    }

    public Response(@Nullable Integer code, @Nullable String toast) {
        this.toast = toast;
        this.code = code;
    }

    public Response setCode(Integer code){this.code = code; return this;}
    public Response setToast(String toast){this.toast = toast; return this;}


    @Nullable
    public Integer getCode(){
        return code;
    }

    @Nullable
    public String getToast(){return toast;}


    public boolean handleWithLoader(@NonNull Loader loader){
        //Toast if any present
        if (toast != null)
            Toast.makeText(loader.getContext(), toast, Toast.LENGTH_SHORT).show();

        //if response has code let loader consume it
        if (code != null) {
            Log.d("asd","code : " + code);
            loader.error(code);
            return true;
        }

        return false;
    }

    public boolean handle(){
        if (getToast() != null)
            Toast.makeText(appContext, getToast(), Toast.LENGTH_SHORT).show();
        if (getCode() != null)
            return true;

        return false;
    }

    public static Response autoRespond(){
        if (NetworkManager.internet_value)
            return new Response(DATA_EMPTY);
        else
            return new Response(NO_DATA_AND_NET);
    }

}
