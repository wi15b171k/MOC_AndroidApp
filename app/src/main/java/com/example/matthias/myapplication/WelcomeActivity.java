package com.example.matthias.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mUserText;

    Button mMyLatestTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mUserText = (TextView) findViewById(R.id.tv_username);
        String user = getIntent().getStringExtra("user");
        mUserText.setText("Welcome " + user);

        mMyLatestTrip = (Button) findViewById(R.id.btn_my_latest_trip);
        mMyLatestTrip.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_my_latest_trip:
                openMyLatestTrip();
        }
    }

    private void openMyLatestTrip() {
        //TODO Trip laden und an Activity übergeben
        Intent intent = new Intent(this, ViewTripActivity.class);
        intent.putExtra("trip", "Dummy Trip 2018");
        startActivity(intent);
    }
}
