package com.example.matthias.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.web.DataProvider;

import java.io.IOException;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = WelcomeActivity.class.getCanonicalName();
    TextView mUserText;

    Button mMyLatestTrip;
    Button mStartNewTrip;
    Button mFriends;
    Button mTrips;
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
        mUserText.setText("Welcome!");
        fetchUserName();

        mMyLatestTrip = (Button) findViewById(R.id.btn_my_latest_trip);
        mMyLatestTrip.setOnClickListener(this);

        mStartNewTrip = (Button) findViewById(R.id.btn_start_new_trip);
        mStartNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this,StartNewTrip.class);
                startActivity(intent);

            }
        });

        mFriends = (Button) findViewById(R.id.btn_friends_trips);
        mFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this,FriendsMainActivity.class);
                startActivity(intent);
            }
        });

        mTrips = (Button) findViewById(R.id.btn_my_trips);
        mTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this,FriendsTripsActivity.class);
                intent.putExtra("Id","xxx");
                intent.putExtra("Name",user);
                startActivity(intent);
            }
        });

        mLogout = (TextView) findViewById(R.id.tv_logout);
        mLogout.setOnClickListener(this);

        mProgress = (ProgressBar) findViewById(R.id.pb_welcome_progress_bar);
    }

    private void fetchUserName() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    return DataProvider.getFullNameByUserId(accessToken);
                } catch (IOException e) {
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                if (s != "") {
                    mUserText.setText("Welcome " + s);
                } else {
                    mUserText.setText("Name not found");
                }
            }
        }.execute();
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
                if (response == null) {
                    Toast.makeText(WelcomeActivity.this, "No trip available yet. Start a new one!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, ViewTripActivity.class);
                    intent.putExtra("trip", response);
                    startActivity(intent);
                }

                mProgress.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }
}
