package com.example.matthias.myapplication;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mMailAddress;
    EditText mPassword;

    TextView mRegisterButton;

    Button mLogin;
    ProgressBar mLoginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

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

        String user = mMailAddress.getText().toString();
        String password = mPassword.getText().toString();

        if (DataProvider.login(user, password).isValid()) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.putExtra("user", user);
            mMailAddress.setText("");
            startActivity(intent);
        } else {
            //TODO failed login behandeln
        }

        mLoginProgress.setVisibility(View.INVISIBLE);
    }
}
