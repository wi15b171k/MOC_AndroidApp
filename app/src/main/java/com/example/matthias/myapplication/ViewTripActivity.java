package com.example.matthias.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matthias.myapplication.Camera.CameraHandler;
import com.example.matthias.myapplication.Entities.Coordinates;
import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.Entities.UserImage;
import com.example.matthias.myapplication.ImageDisplay.TripImagesAdapter;
import com.example.matthias.myapplication.web.DataProvider;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class ViewTripActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener, TripImagesAdapter.UserImageClickedListener {

    TextView mTripName;
    private GoogleMap mMap;
    RecyclerView mImageList;

    Trip trip;

    TripImagesAdapter mAdapter;
    List<UserImage> thumbnails;
    List<Marker> markers;

    ProgressBar mThumbnailsLoading;
    ProgressBar mSavingPicture;
    FloatingActionButton mTakePicture;

    private SharedPreferences settings;
    private String accessToken;

    AtomicInteger thumbnailsToLoad;

    File photoFile;

    LocationManager locationManager;
    LocationListener locationListener;
    Location currentLocation;

    boolean setCameraToCurrentLocation = false; //Wird nur auf true gesetzt, wenn ein neues Foto aufgenommen wurde

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);

        settings = getSharedPreferences(getResources().getString(R.string.shared_preferences), MODE_PRIVATE);
        accessToken = settings.getString("access_token", "");

        trip = (Trip) getIntent().getSerializableExtra("trip");

        mThumbnailsLoading = (ProgressBar) findViewById(R.id.pb_load_thumbnails);
        mSavingPicture = (ProgressBar) findViewById(R.id.pb_saving_image);

        mTripName = (TextView) findViewById(R.id.tv_trip_name);
        mTripName.setText(trip.name);

        mImageList = (RecyclerView) findViewById(R.id.rv_trip_images);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mImageList.setLayoutManager(layoutManager);

        mTakePicture = (FloatingActionButton) findViewById(R.id.fab_take_picture);
        if (trip.isUserTrip) {

            mTakePicture.setOnClickListener(this);

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLocation = location;
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (currentLocation == null) { currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  }
        } else {
            mTakePicture.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        thumbnails = new ArrayList<UserImage>();
        markers = new ArrayList<Marker>();

        mAdapter = new TripImagesAdapter(this);
        mImageList.setAdapter(mAdapter);

        loadMap();
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

        if (markers.size() > 0 && !setCameraToCurrentLocation) {
            LatLng position = markers.get(0).getPosition();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
        }

        if (setCameraToCurrentLocation && currentLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        ArrayList<UserImage> imagesToDisplay = new ArrayList<UserImage>();
        LatLng position = marker.getPosition();


        for (UserImage item :
                thumbnails) {
            if (item.coordinates.latitude == position.latitude && item.coordinates.longitude == position.longitude) {
                imagesToDisplay.add(item.copyPicInfo());
            }
        }

        Intent intent = new Intent(this, FullscreenImageViewActivity.class);
        intent.putExtra("trip", trip);
        intent.putExtra("picInfos", imagesToDisplay);
        startActivity(intent);

        return true;
    }

    @Override
    public void onClick(View view) {
        if (currentLocation == null) {
            Toast.makeText(this, "No location available. Turn on GPS and try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = CameraHandler.createImageFile(this);
            } catch (IOException ex) {
                Toast.makeText(this, "Error taking the picture", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.matthias.myapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CameraHandler.REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraHandler.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            mSavingPicture.setVisibility(View.VISIBLE);

            new AsyncTask<File, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(File... files) {
                    try {
                        return DataProvider.savePicForTrip(accessToken, trip.id, files[0], new Coordinates(currentLocation.getLatitude(),currentLocation.getLongitude()));
                    } catch (IOException e) {
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (aBoolean) {
                        mSavingPicture.setVisibility(View.INVISIBLE);
                        Toast.makeText(ViewTripActivity.this, "Pciture saved", Toast.LENGTH_SHORT).show();
                        setCameraToCurrentLocation = true;
                        onResume();
                    } else {
                        Toast.makeText(ViewTripActivity.this, "Saving failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute(photoFile);
        }
    }

    @Override
    public void onUserImageClicked(UserImage userImage) {
        LatLng coordinates = new LatLng(userImage.coordinates.latitude, userImage.coordinates.longitude);

        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
        }

        for (Marker item :
                markers) {
            if (item.getPosition().equals(coordinates)) {
                item.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            } else {
                item.setIcon(BitmapDescriptorFactory.defaultMarker());
            }
        }
    }
}
