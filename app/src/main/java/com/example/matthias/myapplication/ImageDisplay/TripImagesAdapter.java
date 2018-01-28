package com.example.matthias.myapplication.ImageDisplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.matthias.myapplication.R;

import java.util.ArrayList;

/**
 * Created by Matthias on 28.01.2018.
 */

public class TripImagesAdapter extends RecyclerView.Adapter<TripImagesAdapter.ImageViewHolder> {
    ArrayList<Bitmap> images;

    public TripImagesAdapter() {
        images = new ArrayList<Bitmap>();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.trip_image_view, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ImageViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_trip_image);
        }

        public void bind(int position) {
            image.setImageBitmap(images.get(position));
        }
    }

    public void addBitmap(Bitmap bitmap) {
        images.add(bitmap);
    }
}
