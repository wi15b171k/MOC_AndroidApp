package com.example.matthias.myapplication.Loader;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.web.DataProvider;

import java.util.List;

/**
 * Created by FH on 24.02.2018.
 */

public class TripsLoader extends AsyncTaskLoader<List<Trip>>{
    public static final int LOADER_ID = 33;
    private String mUserId;
    private String mAccessToken;

    public TripsLoader(Context context, String mUserId, String mAccessToken) {
        super(context);
        this.mUserId = mUserId;
        this.mAccessToken = mAccessToken;
    }

    @Override
    public List<Trip> loadInBackground() {
        return DataProvider.getTripsByUserId(this.mAccessToken,this.mUserId);
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}
