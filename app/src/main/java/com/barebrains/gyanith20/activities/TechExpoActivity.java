package com.barebrains.gyanith20.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barebrains.gyanith20.R;
import com.barebrains.gyanith20.components.ImageSlider;
import com.barebrains.gyanith20.components.Loader;
import com.barebrains.gyanith20.interfaces.Resource;
import com.barebrains.gyanith20.models.TechExpoData;
import com.barebrains.gyanith20.statics.DataRepository;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class TechExpoActivity extends AppCompatActivity {

    View bottom_sheet;
    TextView descText;
    CoordinatorLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_expo);


        findViewById(R.id.tech_expo_backbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TechExpoActivity.super.onBackPressed();
            }
        });

        bottom_sheet = findViewById(R.id.te_desc_card);
        descText = findViewById(R.id.te_desc);
        bottom_sheet.setVisibility(View.GONE);
        parent = findViewById(R.id.bot_parent);


        RecyclerView recyclerView = findViewById(R.id.tech_expo_imgs);
        final Loader loader = findViewById(R.id.techexpo_loader);

        final ImgGridAdapter adapter = new ImgGridAdapter();

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(TechExpoActivity.this,RecyclerView.VERTICAL,false));
        recyclerView.setHasFixedSize(true);

        DataRepository.getTechExpoData().observe(this, new Observer<Resource<TechExpoData>>() {
            @Override
            public void onChanged(Resource<TechExpoData> res) {
                if (res.handleWithLoader(loader))
                    return;

                adapter.urls = res.value.urls.toArray(new String[0]);
                adapter.notifyDataSetChanged();
                setDescription(res.value.desc);

            }
        });
        handleDescStates();
    }

    private class ImgGridAdapter extends RecyclerView.Adapter<ImgGridViewHolder> {

        public String[] urls = new String[]{
                "https://www.incimages.com/uploaded_files/image/970x450/getty_769729163_200013341653767170567_404088.jpg"
                ,"http://elitebusinessmagazine.co.uk/cache/com_zoo/images/Why%20should%20you%20adopt%20an%20open%20working%20culture%20in%20the%20age%20of%20the%20individual1%20-%20elitebusinessmagazine_c9c911ed5eb267dd453a7a4ee6b8ae32.jpg"
                ,"https://content.thriveglobal.com/wp-content/uploads/2019/07/remote-work-productivity-tips.png?w=1550"
        };

        @NonNull
        @Override
        public ImgGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View item = LayoutInflater.from(TechExpoActivity.this).inflate(R.layout.item_img_grid,parent,false);

            return new ImgGridViewHolder(item);
        }

        @Override
        public void onBindViewHolder(@NonNull ImgGridViewHolder holder, int position) {
            holder.load(urls[position]);
        }

        @Override
        public int getItemCount() {
            return urls.length;
        }
    }

    private class ImgGridViewHolder extends RecyclerView.ViewHolder{

        ImageView img;

        public ImgGridViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_grid_item_img);
        }

        public void load(String url){
            Glide.with(TechExpoActivity.this)
                    .load(url)
                    .apply(ImageSlider.requestOptions)
                    .fitCenter()
                    .into(img);
        }
    }


    private void setDescription(final String desc){
        if (desc == null || desc.equals("")) {
            bottom_sheet.setVisibility(View.GONE);
            return;
        }
        descText.setText(desc);
        bottom_sheet.setVisibility(View.VISIBLE);

    }

    private void handleDescStates(){
        final BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        descText.setAlpha(1);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        descText.setAlpha(1);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                descText.setAlpha(v);
            }
        });
    }
}
