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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.matthias.myapplication.Adapter.FriendsAddAdapter;
import com.example.matthias.myapplication.Adapter.FriendsMainAdapter;
import com.example.matthias.myapplication.Entities.Person;
import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.Loader.PersonLoader;
import com.example.matthias.myapplication.web.DataProvider;
import com.example.matthias.myapplication.web.InternetConnection;

import java.util.LinkedList;
import java.util.List;

public class FriendsAddActivity extends AppCompatActivity implements FriendsAddAdapter.IFriendsAddClickListener, LoaderManager.LoaderCallbacks<List<Person>>{
    RecyclerView mFriendsList;
    FriendsAddAdapter mAdapter;
    public Person mClick;

    ImageView mSearchFriends;
    EditText mSearchString;
    SharedPreferences settings;
    String accessToken;
    String user;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String SCROLL_POSITION = "scroll_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_add);

        settings = getSharedPreferences(getResources().getString(R.string.shared_preferences), MODE_PRIVATE);
        accessToken = settings.getString("access_token", "");
        user = settings.getString("user_name", "");

        mSearchFriends = (ImageView) findViewById(R.id.iv_filter_friends);
        mSearchString = (EditText) findViewById(R.id.et_search_string);
        mSearchFriends.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                startLoader(true);
                return false;
            }
        });
        mSearchString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                startLoader(true);

            }
        });

        mFriendsList = (RecyclerView) findViewById(R.id.rv_friends_add);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mFriendsList.setLayoutManager(layoutManager);

        mAdapter = new FriendsAddAdapter(new LinkedList<Person>(),this);
        mFriendsList.setAdapter(mAdapter);

        startLoader(false);
    }
    private void startLoader(boolean reload){

        Bundle loaderArgs = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();
        if(!reload)
            loaderManager.initLoader(PersonLoader.LOADER_ID,loaderArgs,this);
        else {
            loaderManager.restartLoader(PersonLoader.LOADER_ID, loaderArgs, this);
        }

    }
    @Override
    public Loader<List<Person>> onCreateLoader(int id, Bundle args) {
        return new PersonLoader(this,2,accessToken,user,mSearchString.getText().toString());
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
    public void onLoaderReset(Loader<List<Person>> loader) {
        mAdapter.swapList(new LinkedList<Person>());
    }

    @Override
    public void onListItemClick(Person clickedPerson) {
        mClick = clickedPerson;

        new AsyncTask<Void,Void,Boolean>(){
            boolean ok;
            @Override
            protected Boolean doInBackground(Void... voids) {
                return DataProvider.sendInvitationToUser(accessToken,mClick.userId);
            }
            @Override
            protected void onPostExecute(Boolean response) {
                Toast.makeText(FriendsAddActivity.this, "Invitation sent.", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
    @Override
    public void onPause() {
        super.onPause();
        mPosition = ((LinearLayoutManager)mFriendsList.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }
}
