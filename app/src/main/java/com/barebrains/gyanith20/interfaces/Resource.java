package com.barebrains.gyanith20.interfaces;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.others.Response;

import static com.barebrains.gyanith20.others.Response.DATA_EMPTY;

public class Resource<T> {
    public T value = null;
    public Response response = new Response();


    private Resource(T value){
        this.value = value;
    }

    public static<V> Resource<V> withValue(V value){
        return new Resource<>(value);
    }

    public Resource<T> toasts(String toast){
        response.setToast(toast);
        return this;
    }

    public Resource<T> withCode(Integer code){
        response.setCode(code);
        return this;
    }

    public static<V> Resource<V> onlyCode(Integer code){return new Resource<V>( null).withCode(code);}

    public static<V> Resource<V> onlyToasts(String toast){return new Resource<V>( null).toasts(toast);}

    public static<V> Resource<V> onlyResponse(Response response){
        Resource<V> resource = new Resource<>(null);
        resource.response = response;
        return resource;
    }

   public static<V> Resource<V> autoRespond(){return onlyResponse(Response.autoRespond());}

    public boolean handleWithLoader(@NonNull Loader loader) {

      if (response.handleWithLoader(loader))
          return true;

        //if no response is present but the value is absent then set code to dataEmpty
        if (value == null) {
            loader.error(DATA_EMPTY);
            return true;
        }

        //No Error so show loaded value
        loader.loaded();
        return false;
    }
}
