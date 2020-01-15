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

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.activities.LoginActivity;
import com.barebrains.gyanith20.interfaces.CompletionListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private static int EMAIL_REQUEST_ID = 11;

    private String title,body;

    private boolean action;
    private CompletionListener listener;

    public BottomSheetFragment(String title, String body, boolean action, CompletionListener listener) {
        this.title = title;
        this.body = body;
        this.action = action;
        this.listener = listener;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        //Set the custom view
        View botRoot = LayoutInflater.from(getContext()).inflate(R.layout.botsheet, null);
        ((TextView)botRoot.findViewById(R.id.bot_title)).setText(title);
        ((TextView)botRoot.findViewById(R.id.bot_body)).setText(body);
        View btn = botRoot.findViewById(R.id.bot_btn);
        if (action) {
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        Intent email = new Intent(Intent.ACTION_MAIN);
                        email.addCategory(Intent.CATEGORY_APP_EMAIL);
                        startActivityForResult(email,EMAIL_REQUEST_ID);}catch (ActivityNotFoundException n){ Toast.makeText(getContext(),"Error opening Default Email app, sorry",Toast.LENGTH_SHORT).show();}}
            });
        }
        dialog.setContentView(botRoot);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == EMAIL_REQUEST_ID)
        {
            listener.OnComplete();
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDestroyView() {
        if (!action)
            listener.OnComplete();
        super.onDestroyView();
    }
}
