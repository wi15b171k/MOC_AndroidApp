package com.example.matthias.myapplication.Loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.AsyncTaskLoader;

import com.example.matthias.myapplication.Entities.Person;
import com.example.matthias.myapplication.R;
import com.example.matthias.myapplication.web.DataProvider;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by FH on 22.02.2018.
 */

public class PersonLoader extends AsyncTaskLoader<List<Person>> {
    public static final int LOADER_ID = 22;
    private int mFunctionIndex; /* 1 = getFriendsByUserId, 2 = searchFrindsByName, 3 = getInvitationsByUserId */
    private String accessToken;
    private String user;
    private String searchString;

    public PersonLoader(Context context, int mFunctionIndex, String accessToken, String user, String searchString) {
        super(context);
        this.mFunctionIndex = mFunctionIndex;
        this.accessToken = accessToken;
        this.user = user;
        this.searchString = searchString;
    }

    @Override
    public List<Person> loadInBackground() {
        List<Person> friends = new LinkedList<>();

        if(mFunctionIndex == 1) friends = DataProvider.getFriendsByUserId(accessToken,0);
        if(mFunctionIndex == 2) friends = DataProvider.searchFriendsByName(accessToken,searchString);
        if(mFunctionIndex == 3) friends = DataProvider.getInvitationsByUserId(accessToken,0);

        return friends;
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
