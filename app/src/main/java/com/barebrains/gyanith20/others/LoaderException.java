package com.barebrains.gyanith20.others;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * pass this Exception through Resource for getting handled by loader
 * this has info about the feedback that should be given to the user
 * index is the errorIndex corresponding to a empty state in Loader
 * message is an additional toast that can be shown while displaying the above empty state
 */

public class LoaderException extends Exception {

    private Integer index;

    public LoaderException(@Nullable Integer index) {
        super();
        this.index = index;
    }

    public LoaderException(@Nullable Integer index,@Nullable String message) {
        super(message);
        this.index = index;
    }

    @Nullable
    public Integer getIndex(){
        return index;
    }
}
