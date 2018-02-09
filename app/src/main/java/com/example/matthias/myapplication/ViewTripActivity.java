package com.example.matthias.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.Entities.UserImage;
import com.example.matthias.myapplication.ImageDisplay.TripImagesAdapter;
import com.example.matthias.myapplication.web.DataProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewTripActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    TextView mTripName;
    private GoogleMap mMap;
    RecyclerView mImageList;

    Trip trip;

    TripImagesAdapter mAdapter;
    List<UserImage> thumbnails;
    List<Marker> markers;

    ProgressBar mThumbnailsLoading;

    private SharedPreferences settings;
    private String accessToken;

    AtomicInteger thumbnailsToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);

        settings = getSharedPreferences(getResources().getString(R.string.shared_preferences), MODE_PRIVATE);
        accessToken = settings.getString("access_token", "");

        trip = (Trip)getIntent().getSerializableExtra("trip");

        mThumbnailsLoading = (ProgressBar) findViewById(R.id.pb_load_thumbnails);
        thumbnails = new ArrayList<UserImage>();
        markers = new ArrayList<Marker>();

        loadMap();

        mTripName = (TextView) findViewById(R.id.tv_trip_name);
        mTripName.setText(trip.name);

        mImageList = (RecyclerView) findViewById(R.id.rv_trip_images);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mImageList.setLayoutManager(layoutManager);

        mAdapter = new TripImagesAdapter();
        //loadDummyImages();
        mImageList.setAdapter(mAdapter);
    }

    private void loadMap() {
        new AsyncTask<Void, Void, List<UserImage>>() {
            @Override
            protected List<UserImage> doInBackground(Void... voids) {
                try {
                    return DataProvider.getPicInfoByTripId(accessToken, trip.id);
                } catch (IOException e) {
                    return new ArrayList<UserImage>();
                }
            }

            @Override
            protected void onPostExecute(List<UserImage> userImage) {
                setThumbnails(userImage);

                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(ViewTripActivity.this);

                loadThumbnails();
            }
        }.execute();
    }

    private void loadThumbnails() {
        mThumbnailsLoading.setVisibility(View.VISIBLE);
        thumbnailsToLoad = new AtomicInteger( thumbnails.size() );

        for (UserImage item :
                thumbnails) {
            final int picId = item.id;
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... voids) {
                    try {
                        return DataProvider.getPicByPicId(accessToken, picId, 100, 100);
                    } catch (IOException e) {
                        return BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.error);
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    setThumbnailForUserImage(picId, bitmap);
                }
            }.execute();
        }
    }

    private void setThumbnailForUserImage(int picId, Bitmap bitmap) {
        for (UserImage item :
                thumbnails) {
            if (item.id == picId) {
                item.image = bitmap;
                if (!mAdapter.hasUserImage(picId)) {
                    mAdapter.addUserImage(item);
                }

                if ( thumbnailsToLoad.decrementAndGet() == 0 ) {
                    mThumbnailsLoading.setVisibility(View.INVISIBLE);
                }

            }
        }
    }

    private void setThumbnails(List<UserImage> userImage) {
        thumbnails = userImage;
    }

    /*private void loadDummyImages() {
        Bitmap im1  = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.globus);
        Bitmap im2  = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.info);
        Bitmap im3  = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.kamera);
        Bitmap im4  = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.klammeraffe);
        Bitmap im5  = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.kontakte);
        Bitmap im6  = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo);

        mAdapter.addBitmap(im1);
        mAdapter.addBitmap(im2);
        mAdapter.addBitmap(im3);
        mAdapter.addBitmap(im4);
        mAdapter.addBitmap(im5);
        mAdapter.addBitmap(im6);
    }*/


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);

        List<LatLng> set = new ArrayList<LatLng>();

        for (UserImage item :
                thumbnails) {
            LatLng coordinates = new LatLng(item.coordinates.latitude, item.coordinates.longitude);

            if (!set.contains(coordinates)) {
                set.add(coordinates);
                Marker marker = mMap.addMarker(new MarkerOptions().position(coordinates));
                markers.add(marker);
            }
        }

        if (markers.size() > 0) {
            LatLng position = markers.get(0).getPosition();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(this, FullscreenImageViewActivity.class);
        startActivity(intent);

        return true;
    }
}
