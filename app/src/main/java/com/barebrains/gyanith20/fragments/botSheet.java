package com.barebrains.gyanith20.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.barebrains.gyanith20.interfaces.ResultListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class botSheet extends BottomSheetDialogFragment {

    public static int EMAIL_REQUEST_ID = 11;
    

    private FragmentManager fragmentManager;

    private String title,body;
    private String action;
    private Integer contentResID;
    
    private CompletionListener actionListener;
    private ResultListener<View> contentListener;

    private botSheet() {
    }
    
    public static botSheet makeBotSheet(FragmentManager fragmentManager){
        botSheet botSheet = new botSheet();
        botSheet.fragmentManager = fragmentManager;
        return botSheet;
    }
    
    public botSheet setTitle(String title){
        this.title = title;
        return this;
    }

    public botSheet setBody(String body){
        this.body = body;
        return this;
    }

    public botSheet setAction(String action){
        this.action = action;
        return this;
    }

    public botSheet setContentRes(Integer resId){
        this.contentResID = resId;
        return this;
    }

    public botSheet setContentListener(ResultListener<View> contentListener){
        this.contentListener = contentListener;
        return this;
    }
    
    public botSheet setActionListener(CompletionListener listener){
        this.actionListener = listener;
        return this;
    }

    public botSheet show(){
        show(fragmentManager, title);
        return this;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        //Creating base botSheet
        View botRoot = LayoutInflater.from(getContext()).inflate(R.layout.botsheet, null);

        TextView bot_Title = botRoot.findViewById(R.id.bot_title);
        TextView bot_body = botRoot.findViewById(R.id.bot_body);
        TextView actionBtn = botRoot.findViewById(R.id.bot_btn);
        FrameLayout contentHolder = botRoot.findViewById(R.id.bot_content_holder);

        //Set title if exists
        if (title != null) {
            bot_Title.setVisibility(View.VISIBLE);
            bot_Title.setText(title);
        } else
            bot_Title.setVisibility(View.GONE);

        //Set body if exists
        if (body != null) {
            bot_body.setVisibility(View.VISIBLE);
            bot_body.setText(body);
        } else
            bot_body.setVisibility(View.GONE);

        //set Content if it exists and call its listener
        if (contentResID != null) {
            contentHolder.setVisibility(View.VISIBLE);
            View content = LayoutInflater.from(getContext()).inflate(contentResID,contentHolder,false);
            contentHolder.addView(content);
            if (contentListener != null)
                contentListener.OnResult(content);
        }
        else {
            contentHolder.setVisibility(View.GONE);
        }


        //Set Action btn if exists
        if (action != null) {
            actionBtn.setText(action);
            actionBtn.setVisibility(View.VISIBLE);
            actionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  if (actionListener != null)
                      actionListener.OnComplete();
                }
            });
        }
        else{
            actionBtn.setVisibility(View.GONE);
            actionBtn.setOnClickListener(null);
        }

        dialog.setContentView(botRoot);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if (actionListener != null)
            actionListener.OnError(null);
        super.onCancel(dialog);
    }

    @Override
    public void onDestroyView() {
        if (actionListener != null)
            actionListener.OnError(null);
        super.onDestroyView();
    }
}
