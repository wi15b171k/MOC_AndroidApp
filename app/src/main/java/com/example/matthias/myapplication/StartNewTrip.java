package com.example.matthias.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.web.DataProvider;

import java.io.IOException;

public class StartNewTrip extends AppCompatActivity {
    private static final String LOG_TAG = StartNewTrip.class.getCanonicalName();
    ImageView startNewTrip;
    EditText mtripName;
    SharedPreferences settings;
    String accessToken;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_new_trip);
        settings = getSharedPreferences(getResources().getString(R.string.shared_preferences), MODE_PRIVATE);
        accessToken = settings.getString("access_token", "");
        user = settings.getString("user_name", "");
        Log.d(LOG_TAG, "Stored Access Token: " + accessToken);

        mtripName = (EditText) findViewById(R.id.et_new_trip_name);
        startNewTrip = (ImageView) findViewById(R.id.iv_start_new_trip);
        startNewTrip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                creatNewTrip();
                return false;
            }
        });
    }
    private void creatNewTrip(){
        new AsyncTask<Void,Void,Trip>(){

            @Override
            protected Trip doInBackground(Void... voids) {
                String tripName = mtripName.getText().toString();
                Boolean tripCreated;
                tripCreated = DataProvider.createTrip(accessToken,0,tripName);
                if(tripCreated){
                    try {
                        return DataProvider.getLatestTrip(accessToken);
                    } catch (IOException e) {
                        return null;
                    }
                }
                return null;

            }
            @Override
            protected void onPostExecute(Trip response) {
                Intent intent = new Intent(StartNewTrip.this, ViewTripActivity.class);
                intent.putExtra("trip", response);
                startActivity(intent);
            }
        }.execute();
        /*Intent intent = new Intent(StartNewTrip.this,MyTripsActivity.class);
        startActivity(intent);*/
    }
}
