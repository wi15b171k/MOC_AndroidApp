package com.example.matthias.myapplication.ImageDisplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.matthias.myapplication.Entities.UserImage;
import com.example.matthias.myapplication.R;

import java.util.ArrayList;

/**
 * Created by Matthias on 28.01.2018.
 */

public class TripImagesAdapter extends RecyclerView.Adapter<TripImagesAdapter.ImageViewHolder> {
    private static final String LOG_TAG = TripImagesAdapter.class.getCanonicalName();
    ArrayList<UserImage> images;

    private ImageView selectedView;
    UserImageClickedListener listener;

    public TripImagesAdapter(UserImageClickedListener listener) {
        images = new ArrayList<UserImage>();
        this.listener = listener;
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

    public interface OnClickListener {
        public void onClick(UserImage userImage);
    }

    public interface UserImageClickedListener {
        public void onUserImageClicked(UserImage userImage);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;

        public ImageViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_trip_image);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            image.setImageBitmap(images.get(position).image);
        }

        @Override
        public void onClick(View view) {
            Log.d(LOG_TAG, "Item clicked. Position " + getAdapterPosition());
            listener.onUserImageClicked(images.get(getAdapterPosition()));
            if (selectedView != null) selectedView.setPadding(0,0,0,0);
            ImageView sel = ((ImageView)view.findViewById(R.id.iv_trip_image));
            sel.setPadding(10,10,10,10);
            selectedView = sel;
        }
    }

    public void addUserImage(UserImage userImage) {
        images.add(userImage);
        notifyDataSetChanged();
    }

    public boolean hasUserImage(int picId) {
        for (UserImage item:
             images) {
            if (item.id == picId) return true;
        }

        return false;
    }
}
