package com.barebrains.gyanith20.interfaces;

import android.util.Log;

public class Resource<T> {
    public T[] value;
    public Throwable error;

    public Resource(T[] value,Throwable error){
        this.value = value;
        this.error = error;
    }
}
