package com.example.matthias.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.matthias.myapplication.Adapter.FriendsMainAdapter;
import com.example.matthias.myapplication.Entities.Person;
import com.example.matthias.myapplication.Loader.PersonLoader;
import com.example.matthias.myapplication.web.DataProvider;

import java.util.LinkedList;
import java.util.List;

public class FriendsMainActivity extends AppCompatActivity implements FriendsMainAdapter.IFriendsMainClickListener, LoaderManager.LoaderCallbacks<List<Person>>, SharedPreferences.OnSharedPreferenceChangeListener{
    ImageView mAddFriends;
    ImageView mRequests;
    private static final String SCROLL_POSITION = "scroll_position";
    private FriendsMainAdapter mAdapter;
    private RecyclerView mFriendsList;
    private int mPosition = RecyclerView.NO_POSITION;

    SharedPreferences settings;
    String accessToken;
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_main);

        settings = getSharedPreferences(getResources().getString(R.string.shared_preferences), MODE_PRIVATE);
        accessToken = settings.getString("access_token", "");
        user = settings.getString("user_name", "");

        mFriendsList = (RecyclerView) findViewById(R.id.rv_friends_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mFriendsList.setLayoutManager(layoutManager);

        mAdapter = new FriendsMainAdapter(new LinkedList<Person>(),this);
        mFriendsList.setAdapter(mAdapter);

        mAddFriends = (ImageView) findViewById(R.id.iv_add_friends);
        mAddFriends.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent intent = new Intent(FriendsMainActivity.this,FriendsAddActivity.class);
                startActivity(intent);
                return false;
            }
        });
        mRequests = (ImageView) findViewById(R.id.iv_friend_requests);
        mRequests.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent intent = new Intent(FriendsMainActivity.this,FriendsRequestActivity.class);
                startActivity(intent);
                return false;
            }
        });
        startLoader();
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(SCROLL_POSITION, RecyclerView.NO_POSITION);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
    private void startLoader(){
        Bundle loaderArgs = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(PersonLoader.LOADER_ID,loaderArgs,this);

    }
    @Override
    public void onListItemClick(Person clickedPerson) {
        Intent intent = new Intent(FriendsMainActivity.this,FriendsTripsActivity.class);
        intent.putExtra("Id",clickedPerson.userId);
        intent.putExtra("Name",clickedPerson.name);
        startActivity(intent);
    }

    @Override
    public Loader<List<Person>> onCreateLoader(int id, Bundle args) {
        return new PersonLoader(this,1,accessToken,user,"");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onLoadFinished(Loader<List<Person>> loader, List<Person> data) {
        int position = ((LinearLayoutManager)mFriendsList.getLayoutManager()).findFirstVisibleItemPosition();
        if(position != RecyclerView.NO_POSITION) {
            mPosition = position;
        }
        mAdapter.swapList(data);
        if (mPosition != RecyclerView.NO_POSITION)
            ((LinearLayoutManager) mFriendsList.getLayoutManager()).scrollToPositionWithOffset(mPosition, 0);
    }
    @Override
    public void onPause() {
        super.onPause();
        mPosition = ((LinearLayoutManager)mFriendsList.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }

    @Override
    public void onLoaderReset(Loader<List<Person>> loader) {
        mAdapter.swapList(new LinkedList<Person>());
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCROLL_POSITION, mPosition);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
