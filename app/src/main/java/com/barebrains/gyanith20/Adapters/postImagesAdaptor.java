package com.barebrains.gyanith20.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.barebrains.gyanith20.R;

public class postImagesAdaptor extends PagerAdapter {
    public Bitmap[] imgBitmaps;
    private LayoutInflater layoutInflater;

    public postImagesAdaptor(Context context,Bitmap[] imgBitmaps){
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imgBitmaps = imgBitmaps;
    }

    @Override
    public int getCount() {
        return imgBitmaps.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.item_post_item_img, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.post_item_img);

        if (imgBitmaps[position] != null) {
            imageView.setImageBitmap(imgBitmaps[position]);
            imageView.setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.post_item_img_progress).setVisibility(View.GONE);
        }
        else {
            imageView.setVisibility(View.GONE);
            itemView.findViewById(R.id.post_item_img_progress).setVisibility(View.VISIBLE);
        }
        itemView.setTag(position);
        container.addView(itemView);
        return itemView;
    }

    public void UpdatePosition(ViewGroup viewGroup, int position){
        View itemView = viewGroup.findViewWithTag(position);
        if (itemView == null)
            return;
        ImageView imageView = (ImageView) itemView.findViewById(R.id.post_item_img);

        if (imgBitmaps[position] != null) {
            imageView.setImageBitmap(imgBitmaps[position]);
            imageView.setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.post_item_img_progress).setVisibility(View.GONE);
        }
        else {
            imageView.setVisibility(View.GONE);
            itemView.findViewById(R.id.post_item_img_progress).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {

        return super.getItemPosition(object);
    }
}
