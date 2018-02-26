package com.example.matthias.myapplication;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.matthias.myapplication.Adapter.FriendsAddAdapter;
import com.example.matthias.myapplication.Adapter.FriendsMainAdapter;
import com.example.matthias.myapplication.Entities.Person;
import com.example.matthias.myapplication.Loader.PersonLoader;
import com.example.matthias.myapplication.web.DataProvider;

import java.util.LinkedList;
import java.util.List;

public class FriendsRequestActivity extends AppCompatActivity implements FriendsAddAdapter.IFriendsAddClickListener, LoaderManager.LoaderCallbacks<List<Person>>{
    RecyclerView mFriendsList;
    FriendsAddAdapter mAdapter;
    public Person mClick;

    SharedPreferences settings;
    String accessToken;
    String user;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final String SCROLL_POSITION = "scroll_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_requests);

        settings = getSharedPreferences(getResources().getString(R.string.shared_preferences), MODE_PRIVATE);
        accessToken = settings.getString("access_token", "");
        user = settings.getString("user_name", "");

        mFriendsList = (RecyclerView) findViewById(R.id.rv_friends_request);
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
        else
            loaderManager.restartLoader(PersonLoader.LOADER_ID,loaderArgs,this);

    }
    @Override
    public Loader<List<Person>> onCreateLoader(int id, Bundle args) {
        return new PersonLoader(this,3,accessToken,user,"");
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
    public void onListItemClick(final Person clickedPerson) {
        mClick = clickedPerson;

        new AsyncTask<Void,Void,Boolean>(){
            boolean ok;
            @Override
            protected Boolean doInBackground(Void... voids) {
                return DataProvider.respondToInvitationByUserId(accessToken,Integer.parseInt(clickedPerson.userId),0,true);
            }

            @Override
            protected void onPostExecute(Boolean response) {
                Toast.makeText(FriendsRequestActivity.this, "Request accepted.", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
    @Override
    public void onPause() {
        super.onPause();
        mPosition = ((LinearLayoutManager)mFriendsList.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }
}
