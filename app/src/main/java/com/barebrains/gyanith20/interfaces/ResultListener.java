package com.barebrains.gyanith20.interfaces;

public class ResultListener<T> {
    public void OnResult(T t){}
    public void OnError(String error){}

    public void OnComplete(T t,String error){}
}
