package com.example.matthias.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.ImageDisplay.TripImagesAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewTripActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    TextView mTripName;
    private GoogleMap mMap;
    RecyclerView mImageList;

    Trip trip;

    TripImagesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mTripName = (TextView) findViewById(R.id.tv_trip_name);
        trip = (Trip)getIntent().getSerializableExtra("trip");
        mTripName.setText(trip.name);

        mImageList = (RecyclerView) findViewById(R.id.rv_trip_images);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mImageList.setLayoutManager(layoutManager);

        mAdapter = new TripImagesAdapter();
        loadDummyImages();
        mImageList.setAdapter(mAdapter);
    }

    private void loadDummyImages() {
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
    }


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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(this, FullscreenImageViewActivity.class);
        startActivity(intent);

        return true;
    }
}
