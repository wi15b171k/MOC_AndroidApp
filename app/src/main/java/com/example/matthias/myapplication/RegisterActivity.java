package com.example.matthias.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.matthias.myapplication.web.DataProvider;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mFirstname;
    EditText mLastname;
    EditText mMail;
    EditText mPassword;
    EditText mPasswordConfirm;
    Button mLogin;

    CheckBox mTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstname = (EditText) findViewById(R.id.et_register_first_name);
        mLastname = (EditText) findViewById(R.id.et_register_last_name);
        mMail = (EditText) findViewById(R.id.et_register_mail);
        mPassword = (EditText) findViewById(R.id.et_register_pwd);
        mPasswordConfirm = (EditText) findViewById(R.id.et_register_pwd_confirm);
        mLogin = (Button) findViewById(R.id.btn_register);
        mLogin.setOnClickListener(this);

        mTerms = (CheckBox) findViewById(R.id.cb_terms);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                if (mTerms.isChecked()) {
                    register();
                } else {
                    Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void resetFields() {
        mFirstname.setText("");
        mLastname.setText("");
        mMail.setText("");
        mPassword.setText("");
        mPasswordConfirm.setText("");
    }

    private void register() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                String firstname = mFirstname.getText().toString();
                String lastname = mLastname.getText().toString();
                String mail = mMail.getText().toString();
                String password = mPassword.getText().toString();
                String passwordConfirm = mPasswordConfirm.getText().toString();

                try {
                    return DataProvider.register(firstname, lastname, mail, password, passwordConfirm);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                resetFields();

                if (aBoolean) {
                    Toast.makeText(RegisterActivity.this, "Register successful. Please log in.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Register failed. Please try again", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}
