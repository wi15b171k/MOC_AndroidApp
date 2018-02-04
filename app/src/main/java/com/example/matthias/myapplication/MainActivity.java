package com.example.matthias.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matthias.myapplication.web.DataProvider;
import com.example.matthias.myapplication.web.InternetConnection;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mMailAddress;
    EditText mPassword;

    TextView mRegisterButton;

    Button mLogin;
    ProgressBar mLoginProgress;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences(getResources().getString(R.string.shared_preferences), MODE_PRIVATE);
        if (settings.getString("access_token", "") != "") {
            redirectToWelcome();
        }

        mMailAddress = (EditText) findViewById(R.id.et_login_mail);
        mPassword = (EditText) findViewById(R.id.et_login_pwd);

        mRegisterButton = (TextView) findViewById(R.id.tv_register);
        mRegisterButton.setOnClickListener(this);

        mLogin = (Button) findViewById(R.id.btn_login);
        mLogin.setOnClickListener(this);

        mLoginProgress = (ProgressBar) findViewById(R.id.pb_login_in_progress);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                loginUser();
                break;
        }

    }

    private void loginUser() {
        mLoginProgress.setVisibility(View.VISIBLE);

        final String user = mMailAddress.getText().toString();
        final String password = mPassword.getText().toString();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    return DataProvider.login(user, password);
                } catch (IOException e) {
                    return InternetConnection.BAD_REQUEST;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                if (s != InternetConnection.BAD_REQUEST) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("access_token", s);
                    editor.putString("user_name", user);
                    editor.commit();

                    mMailAddress.setText("");
                    mPassword.setText("");
                    redirectToWelcome();
                } else {
                    Toast.makeText(MainActivity.this, "Login failed. Please try again", Toast.LENGTH_SHORT).show();
                    mPassword.setText("");
                }

                mLoginProgress.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    private void redirectToWelcome() {
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(intent);
    }
}
