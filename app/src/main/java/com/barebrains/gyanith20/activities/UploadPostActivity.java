package com.barebrains.gyanith20.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.ClickableViewPager;
import com.barebrains.gyanith20.services.PostUploadService;
import com.barebrains.gyanith20.statics.Anim;
import com.barebrains.gyanith20.statics.Util;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import org.w3c.dom.Text;

import java.io.File;

public class UploadPostActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener {

    Bitmap[] imgBitmaps;

    SliderLayout sliderPanel;
    View tapPanel;
    EditText captionsText;
    TextView captionPrompt;

    final RequestOptions requestOptions = (new RequestOptions())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.l2)
            .error(R.drawable.gyanith_error);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        tapPanel = findViewById(R.id.upd_post_tap_panel);
        sliderPanel = findViewById(R.id.uploadpost_viewpager);
        captionsText = findViewById(R.id.post_captions);
        captionPrompt = findViewById(R.id.caption_prompt);
        captionsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (captionsText.getText().length() != 0)
                    captionPrompt.setVisibility(View.GONE);
                else
                    captionPrompt.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.upload_post_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        tapPanel.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        final String[] imgPaths = bundle.getStringArray("EXTRA_IMG_PATHS");

        if (imgPaths == null)
            finish();

        for (String path : imgPaths){
            DefaultSliderView item = new DefaultSliderView(this);
            item.image(new File(path))
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(this);
            sliderPanel.addSlider(item);
        }

        findViewById(R.id.post_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadPost(imgPaths);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void UploadPost(String[] imgPaths){
        Intent intent = new Intent(this, PostUploadService.class);
        Bundle bundle  = new Bundle();
        bundle.putStringArray("EXTRA_IMG_PATHS",imgPaths);
        intent.putExtras(bundle);
        EditText captionsTxt = findViewById(R.id.post_captions);
        intent.putExtra("EXTRA_CAPTIONS",captionsTxt.getText().toString());
        startService(intent);
    }

    private void captionStateToggle(){
        if (tapPanel.getVisibility() == View.VISIBLE) {
            Anim.crossfade(tapPanel, sliderPanel,0f,350);
            Anim.zoom(tapPanel,1f, 0.7f,450);
            hideSoftKeyboard();

        } else {
            Anim.crossfade(sliderPanel,tapPanel,0.3f,350);
            Anim.zoom(tapPanel,0.7f,1f,400);
            showSoftKeyboard(captionsText);
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        captionStateToggle();
    }

    public void showSoftKeyboard(View view) {
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                Log.d("asd","not null");
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_NOT_ALWAYS);
                //imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    public void hideSoftKeyboard() {

        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (inputMethodManager != null && view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}


