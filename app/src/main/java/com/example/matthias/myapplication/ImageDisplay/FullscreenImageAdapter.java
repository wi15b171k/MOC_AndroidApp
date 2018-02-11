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
import android.widget.Toast;

import com.example.matthias.myapplication.Entities.UserImage;
import com.example.matthias.myapplication.FullscreenImageViewActivity;
import com.example.matthias.myapplication.R;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Matthias on 31.01.2018.
 */

public class FullscreenImageAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    FullscreenImageViewActivity activity;
    CopyOnWriteArrayList<UserImage> images;
    LayoutInflater inflater;

    int currentPosition;

    public FullscreenImageAdapter(FullscreenImageViewActivity activity) {
        this.activity = activity;
        images = new CopyOnWriteArrayList<UserImage>();
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
        image.setImageBitmap(images.get(position).image);

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

    public void addImage(UserImage userImage) {
        images.add(userImage);
        notifyDataSetChanged();
    }

    public void removeImage(int picId) {
        for (UserImage item :
                images) {
            if (item.id == picId) {
                images.remove(item);
                notifyDataSetChanged();
            }
        }
    }

    public int getCurrentPicId() {
        return images.get(currentPosition).id;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
