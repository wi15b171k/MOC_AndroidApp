package com.example.matthias.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.Entities.UserImage;
import com.example.matthias.myapplication.ImageDisplay.FullscreenImageAdapter;
import com.example.matthias.myapplication.web.DataProvider;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenImageViewActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            /*ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }*/
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    ViewPager mImageViewer;
    FullscreenImageAdapter mViewerAdapter;
    ProgressBar mLoadingProgress;
    Button mPicAction;

    Trip trip;

    SharedPreferences settings;
    String accessToken;

    AtomicInteger imagesToLoad;
    ArrayList<UserImage> images;

    boolean allowDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_image_view);

        allowDelete = false; //Löschen erst aktivieren, wenn alle Bilder geladen sind, sonst gibt's einen Fehler

        Intent intent = getIntent();
        trip = (Trip)intent.getSerializableExtra("trip");
        images = (ArrayList<UserImage>)intent.getSerializableExtra("picInfos");

        settings = getSharedPreferences(getResources().getString(R.string.shared_preferences), MODE_PRIVATE);
        accessToken = settings.getString("access_token", "");

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading_images);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        mPicAction = (Button) findViewById(R.id.dummy_button);

        if (trip.isUserTrip) {
            mPicAction.setText("Löschen");
        } else {
            mPicAction.setText("Melden");
        }

        mPicAction.setOnTouchListener(mDelayHideTouchListener);
        mPicAction.setOnClickListener(this);

        mImageViewer = findViewById(R.id.vp_image_viewer);
        mViewerAdapter = new FullscreenImageAdapter(this);
        mImageViewer.setAdapter(mViewerAdapter);
        mImageViewer.addOnPageChangeListener(mViewerAdapter);
        //loadDummyImages();
        startLoading();
    }

    private void startLoading() {
        mLoadingProgress.setVisibility(View.VISIBLE);
        imagesToLoad = new AtomicInteger(images.size());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        final int height = metrics.heightPixels;
        final int width = metrics.widthPixels;

        for (UserImage item :
                images) {
            final int picId = item.id;

            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... voids) {
                    try {
                        return DataProvider.getPicByPicId(accessToken, picId, 0, 0);
                    } catch (IOException e) {
                        return BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.error);
                    }
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    addImage(picId, bitmap);
                }
            }.execute();
        }
    }

    private void addImage(int picId, Bitmap bitmap) {
        for (UserImage item :
                images) {
            if (item.id == picId) {
                item.image = bitmap;
                mViewerAdapter.addImage(item);

                if (imagesToLoad.decrementAndGet() == 0) {
                    mLoadingProgress.setVisibility(View.INVISIBLE);
                    allowDelete = true;
                }
            }
        }
    }

    /*private void loadDummyImages() {
        Bitmap im1  = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.globus);
        Bitmap im2  = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.info);
        Bitmap im3  = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.kamera);
        Bitmap im4  = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.klammeraffe);
        Bitmap im5  = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.kontakte);
        Bitmap im6  = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo);

        mViewerAdapter.addImage(im1);
        mViewerAdapter.addImage(im2);
        mViewerAdapter.addImage(im3);
        mViewerAdapter.addImage(im4);
        mViewerAdapter.addImage(im5);
        mViewerAdapter.addImage(im6);
    }*/

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    public void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dummy_button:
                if (!allowDelete) {
                    Toast.makeText(this, "Please wait until all images are loaded", Toast.LENGTH_SHORT).show();
                } else {
                    if (trip.isUserTrip) {
                        deleteCurrentPicture();
                    } else {
                        meldeCurrentPicture();
                    }
                }
                break;
        }
    }

    private void meldeCurrentPicture() {
        final int currentPicId = mViewerAdapter.getCurrentPicId();

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    return DataProvider.meldePicById(accessToken, currentPicId);
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(FullscreenImageViewActivity.this, "Successullfy reported picture", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FullscreenImageViewActivity.this, "Reporting failed. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void deleteCurrentPicture() {
        final int currentPicId = mViewerAdapter.getCurrentPicId();

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    return DataProvider.deletePicById(accessToken, currentPicId);
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    mViewerAdapter.removeImage(currentPicId);
                    mImageViewer.setAdapter(mViewerAdapter);
                    Toast.makeText(FullscreenImageViewActivity.this, "Delete successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FullscreenImageViewActivity.this, "Delete failed. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
