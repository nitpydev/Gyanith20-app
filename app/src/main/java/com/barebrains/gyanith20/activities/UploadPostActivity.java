package com.barebrains.gyanith20.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.ImageSlider;
import com.barebrains.gyanith20.services.PostUploadService;
import com.barebrains.gyanith20.statics.Anim;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


public class UploadPostActivity extends AppCompatActivity {


    ImageSlider imgSlider;
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
        imgSlider = findViewById(R.id.upd_img_slider);
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
          //  DefaultSliderView item = new DefaultSliderView(this);
         /*   item.image(new File(path))
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(this);
            imgSlider.addSlider(item);

          */
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
            Anim.crossfade(tapPanel, imgSlider,0f,350);
            Anim.zoom(tapPanel,1f, 0.7f,450);
            hideSoftKeyboard();

        } else {
            Anim.crossfade(imgSlider,tapPanel,0.3f,350);
            Anim.zoom(tapPanel,0.7f,1f,400);
        }
    }

   /* @Override
    public void onSliderClick(BaseSliderView slider) {
        captionStateToggle();
    }

    */

    public void hideSoftKeyboard() {

        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (inputMethodManager != null && view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}


