package com.example.matthias.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.web.DataProvider;
import com.example.matthias.myapplication.web.InternetConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = WelcomeActivity.class.getCanonicalName();
    TextView mUserText;

    Button mMyLatestTrip;
    Button mStartNewTrip;

    TextView mLogout;

    ProgressBar mProgress;

    SharedPreferences settings;
    String accessToken;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        settings = getSharedPreferences(getResources().getString(R.string.shared_preferences), MODE_PRIVATE);
        accessToken = settings.getString("access_token", "");
        user = settings.getString("user_name", "");
        Log.d(LOG_TAG, "Stored Access Token: " + accessToken);

        mUserText = (TextView) findViewById(R.id.tv_username);
        mUserText.setText("Welcome " + user);

        mMyLatestTrip = (Button) findViewById(R.id.btn_my_latest_trip);
        mMyLatestTrip.setOnClickListener(this);

        mStartNewTrip = (Button) findViewById(R.id.btn_start_new_trip);
        mStartNewTrip.setOnClickListener(this);

        mLogout = (TextView) findViewById(R.id.tv_logout);
        mLogout.setOnClickListener(this);

        mProgress = (ProgressBar) findViewById(R.id.pb_welcome_progress_bar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_my_latest_trip:
                openMyLatestTrip();
                break;
            case R.id.btn_start_new_trip:
                startNewTrip();
                break;
            case R.id.tv_logout:
                logout();
                break;
        }
    }

    private void logout() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    return DataProvider.logout(accessToken);
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.clear();
                    editor.commit();

                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }.execute();
    }

    private void startNewTrip() {

    }

    private void openMyLatestTrip() {

        mProgress.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, Trip>() {
            @Override
            protected Trip doInBackground(Void... voids) {
                try {
                    return DataProvider.getLatestTrip(accessToken);
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Trip response) {
                Intent intent = new Intent(WelcomeActivity.this, ViewTripActivity.class);
                intent.putExtra("trip", response);
                startActivity(intent);

                mProgress.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }
}
