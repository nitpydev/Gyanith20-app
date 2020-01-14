package com.barebrains.gyanith20.interfaces;

import android.widget.Toast;

import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.others.LoaderException;

public class Resource<T> {
    public T value;
    public LoaderException error;

    public boolean internet = false;

    public Resource(T value, LoaderException error){
        this.value = value;
        this.error = error;
    }

    public Resource(boolean internet,Resource<T> resource){
        this.internet = internet;
        this.value = resource.value;
        this.error = resource.error;
    }

    //If error contains message toast it if it has errorIndex(must have) use loader to display it

    public boolean handleLoader(Loader loader){
        if (loader == null)
            return false;

        if (error != null) {
            if (error.getMessage() != null)
                Toast.makeText(loader.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            if (error.getIndex() != null) {
                loader.error(error.getIndex());
                return true;
            }
        }
        if (value != null) {
            loader.loaded();
        }
        return false;
    }
}
