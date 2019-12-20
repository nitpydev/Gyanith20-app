package com.barebrains.gyanith20.others;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barebrains.gyanith20.components.PostView;

public class PostViewHolder extends RecyclerView.ViewHolder{

    public PostView postView;
    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        postView = (PostView) itemView;
    }
}
