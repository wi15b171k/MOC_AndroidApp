package com.example.matthias.myapplication.web;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Matthias on 01.02.2018.
 */

public class InternetConnection {

    private static final String LOG_TAG = InternetConnection.class.getCanonicalName();

    public static final String BAD_REQUEST = InternetConnection.class.getCanonicalName() + " BAD_REQUEST";
    public static final String REQUEST_GET = "GET";
    public static final String REQUEST_POST = "POST";

    /* Returns response Code */
    public static String sendJSONtoServer(String urlString, JSONObject toSend, String token, boolean useToken) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(REQUEST_POST);
        connection.setRequestProperty("Host", "localhost:48897");
        connection.setRequestProperty("Content-Type", "application/json");

        if (useToken) {
            connection.setRequestProperty("authorization", "bearer " + token);
        }

        String message = toSend.toString();
        Log.d(LOG_TAG, "Connecting to URL: " + urlString);
        Log.d(LOG_TAG, "JSON to send: " + message);

        Log.d(LOG_TAG, connection.getRequestProperties().toString());

        connection.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(message);
        out.flush();
        out.close();

        int responseCode = connection.getResponseCode();
        String responseMessage= connection.getResponseMessage();
        Log.d(LOG_TAG, "Code " + responseCode + ": " + responseMessage);

        connection.disconnect();

        return (responseCode == 200 || responseCode == 201) ? responseMessage : BAD_REQUEST;
    }

    public static String sendStringToServer(String urlString, String message, String requestMethod, String token, boolean useToken) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("Host", "localhost:48897");

        if (useToken) {
            connection.setRequestProperty("authorization", "bearer " + token);
        }

        Log.d(LOG_TAG, "Connecting to URL: " + urlString);
        Log.d(LOG_TAG, "Message to send: " + message);

        Log.d(LOG_TAG, connection.getRequestProperties().toString());

        if (requestMethod == REQUEST_POST) {
            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(message);
            out.flush();
            out.close();
        }

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        Log.d(LOG_TAG, "Code " + responseCode + ": " + responseMessage);

        String inMessage = BAD_REQUEST;

        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            inMessage = in.readLine();
            Log.d(LOG_TAG, "Response message: " + inMessage);
        }

        connection.disconnect();

        return inMessage;
    }
}
