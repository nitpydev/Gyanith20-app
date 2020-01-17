package com.barebrains.gyanith20.interfaces;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.others.Response;
import com.barebrains.gyanith20.statics.NetworkManager;

import static com.barebrains.gyanith20.others.Response.DATA_EMPTY;
import static com.barebrains.gyanith20.others.Response.NO_DATA_AND_NET;

public class ArrayResource<T> {
    public T[] value;
    public Response response = new Response();

    public ArrayResource(T[] value){
        if (value != null && value.length == 0) {
            value = null;
            response.setCode(DATA_EMPTY);
        }

        this.value = value;
    }

    public static <V> ArrayResource<V> withValue(V[] value){
        return new ArrayResource<>(value);
    }

    public ArrayResource<T> toasts(String toast){
        response.setToast(toast);
        return this;
    }

    public ArrayResource<T> withCode(Integer code){
        response.setCode(code);
        return this;
    }

    public static<V> ArrayResource<V> onlyCode(Integer code){
        return new ArrayResource<V>(null).withCode(code);
    }

    public static<V> ArrayResource<V> onlyToasts(String toast){return new ArrayResource<V>(null).toasts(toast);}

    public static<V> ArrayResource<V> onlyResponse(Response response){
        ArrayResource<V> resource = new ArrayResource<>(null);
        resource.response = response;
        return resource;
    }


    public static<V> ArrayResource<V> autoRespond(){return onlyResponse(Response.autoRespond());}


    public boolean handleWithLoader(@NonNull Loader loader) {

        if (response.handleWithLoader(loader))
            return true;


        //if no response is present but the value is absent then set code to dataEmpty
        if (value == null || value.length == 0) {
            loader.error(DATA_EMPTY);
            return true;
        }

        //No Error so show loaded value
        loader.loaded();
        return false;
    }

    public ArrayResource<T> passFilter(T[] filteredValue){
        Integer code = response.getCode();
        if (filteredValue == null || filteredValue.length == 0) {
            if (code == null)
                code = DATA_EMPTY;
        }
        return ArrayResource.withValue(filteredValue).withCode(code).toasts(response.getToast());
    }
}
