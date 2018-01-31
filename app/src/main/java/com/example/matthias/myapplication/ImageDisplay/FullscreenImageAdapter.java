package com.example.matthias.myapplication.ImageDisplay;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.matthias.myapplication.FullscreenImageViewActivity;
import com.example.matthias.myapplication.R;

import java.util.ArrayList;

/**
 * Created by Matthias on 31.01.2018.
 */

public class FullscreenImageAdapter extends PagerAdapter {
    FullscreenImageViewActivity activity;
    ArrayList<Bitmap> images;
    LayoutInflater inflater;

    public FullscreenImageAdapter(FullscreenImageViewActivity activity) {
        this.activity = activity;
        images = new ArrayList<Bitmap>();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.fullscreen_single_image, container, false);

        ImageView image = itemView.findViewById(R.id.iv_image_fullscreen_view);
        image.setImageBitmap(images.get(position));

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {activity.toggle();
            }
        });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View)object);
    }

    public void addImage(Bitmap bitmap) {
        images.add(bitmap);
        notifyDataSetChanged();
    }
}
