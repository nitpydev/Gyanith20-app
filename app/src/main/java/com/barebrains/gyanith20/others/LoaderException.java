package com.barebrains.gyanith20.others;

import androidx.annotation.NonNull;

public class LoaderException extends Exception {

    private int index;

    public LoaderException(@NonNull int index) {
        super();
        this.index = index;
    }

    @NonNull
    public int getIndex(){
        return index;
    }
}
