package com.barebrains.gyanith20.fragments;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class botSheet extends BottomSheetDialogFragment {

    public static int EMAIL_REQUEST_ID = 11;
    

    private FragmentManager fragmentManager;

    private String title,body;
    private String action;
    
    private CompletionListener listener;

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
    
    public botSheet setActionListener(CompletionListener listener){
        this.listener = listener;
        return this;
    }

    public botSheet show(){
        show(fragmentManager, title);
        return this;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        //Set the custom view
        View botRoot = LayoutInflater.from(getContext()).inflate(R.layout.botsheet, null);
        ((TextView)botRoot.findViewById(R.id.bot_title)).setText(title);
        ((TextView)botRoot.findViewById(R.id.bot_body)).setText(body);
        TextView btn = botRoot.findViewById(R.id.bot_btn);
        if (action != null) {
            btn.setText(action);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  if (listener != null)
                      listener.OnComplete();
                }
            });
        }
        else{
            btn.setVisibility(View.GONE);
            btn.setOnClickListener(null);
        }

        dialog.setContentView(botRoot);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if (listener != null)
        listener.OnError(null);
        super.onCancel(dialog);
    }

    @Override
    public void onDestroyView() {
        if (listener != null)
            listener.OnError(null);
        super.onDestroyView();
    }
}
