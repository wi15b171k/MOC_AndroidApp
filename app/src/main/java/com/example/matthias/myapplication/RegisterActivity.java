package com.example.matthias.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.matthias.myapplication.web.DataProvider;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    private boolean validateConfirm(String password, String confirmation){
       return password.equals(confirmation);
    }

    private boolean validateEmail(String email){
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(email);
        return m.find();
    }

    private boolean validatePassword(String password){
        if(password.length()<=3 || !containsNumber(password)) {
            return false;
        }
        return true;
    }
    private boolean containsNumber(String string)
    {
        return string.matches(".*\\d+.*");
    }

    private void register() {

        boolean validEmail = validateEmail(mMail.getText().toString());
        boolean validPassword = validatePassword(mPassword.getText().toString());
        boolean validConfirm = validateConfirm(mPassword.getText().toString(),mPasswordConfirm.getText().toString());
        boolean validForm = true;

        if(!validEmail){
            validForm = false;
            Toast.makeText(RegisterActivity.this, "Not a valid e-mail adress.", Toast.LENGTH_LONG).show();
        }
        if(!validPassword){
            validForm = false;
            Toast.makeText(RegisterActivity.this, "Password must contain at least 3 characters and contain at least one digit.", Toast.LENGTH_LONG).show();
        }
        if(!validConfirm){
            validForm = false;
            Toast.makeText(RegisterActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
        }

        if(validForm){
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
}
