package com.example.matthias.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.matthias.myapplication.Adapter.TripsAdapter;
import com.example.matthias.myapplication.Entities.Person;
import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.Loader.TripsLoader;
import com.example.matthias.myapplication.web.DataProvider;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FriendsTripsActivity extends AppCompatActivity implements TripsAdapter.ITripsItemClickListener, LoaderManager.LoaderCallbacks<List<Trip>>{
    String mUserId;
    TextView mTitle;
    TripsAdapter mAdapter;
    RecyclerView mTripsList;

    SharedPreferences settings;
    String accessToken;
    String user;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String SCROLL_POSITION = "scroll_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_trips);

        mTitle = (TextView) findViewById(R.id.tv_trips_title);
        mTitle.setText(mTitle.getText() + intent.getStringExtra("Name"));

        mUserId = intent.getStringExtra("Id");

        settings = getSharedPreferences(getResources().getString(R.string.shared_preferences), MODE_PRIVATE);
        accessToken = settings.getString("access_token", "");
        user = settings.getString("user_name", "");


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new TripsAdapter(new LinkedList<Trip>(),this);

        mTripsList = (RecyclerView) findViewById(R.id.rv_all_trips);
        mTripsList.setLayoutManager(layoutManager);
        mTripsList.setAdapter(mAdapter);

        startLoader(false);


    }
    private void startLoader(boolean reload){

        Bundle loaderArgs = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();
        if(!reload)
            loaderManager.initLoader(TripsLoader.LOADER_ID,loaderArgs,this);
        else {
            loaderManager.restartLoader(TripsLoader.LOADER_ID, loaderArgs, this);
        }

    }
    @Override
    public void onListItemClick(final Trip clickedTrip) {
        new AsyncTask<Void, Void, Trip>() {
            @Override
            protected Trip doInBackground(Void... voids) {
                return clickedTrip;
            }
            @Override
            protected void onPostExecute(Trip response) {
                Intent intent = new Intent(FriendsTripsActivity.this, ViewTripActivity.class);
                intent.putExtra("trip", response);
                startActivity(intent);
            }
        }.execute();
    }

    @Override
    public Loader<List<Trip>> onCreateLoader(int id, Bundle args) {
        return new TripsLoader(this,mUserId,accessToken);
    }

    @Override
    public void onLoadFinished(Loader<List<Trip>> loader, List<Trip> data) {
        int position = ((LinearLayoutManager)mTripsList.getLayoutManager()).findFirstVisibleItemPosition();
        if(position != RecyclerView.NO_POSITION) {
            mPosition = position;
        }
        mAdapter.swapList(data);
        if (mPosition != RecyclerView.NO_POSITION)
            ((LinearLayoutManager) mTripsList.getLayoutManager()).scrollToPositionWithOffset(mPosition, 0);
    }

    @Override
    public void onLoaderReset(Loader<List<Trip>> loader) {
        mAdapter.swapList(new LinkedList<Trip>());
    }

    @Override
    public void onPause() {
        super.onPause();
        mPosition = ((LinearLayoutManager)mTripsList.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }
}
