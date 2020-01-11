package com.barebrains.gyanith20.interfaces;

import android.util.Log;

import com.barebrains.gyanith20.others.LoaderException;

public class Resource<T> {
    public T[] value;
    public LoaderException error;

    public Resource(T[] value,LoaderException error){
        this.value = value;
        this.error = error;
    }
}
