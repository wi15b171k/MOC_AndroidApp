package com.example.matthias.myapplication.web;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
    public static final String REQUEST_DELETE = "DELETE";

    private static boolean setHostName = false; //Muss auf true gesetzt werden, wenn über den Emulator getestet wird

    /* Returns response Code */
    public static String sendJSONtoServer(String urlString, JSONObject toSend, String token, boolean useToken) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(REQUEST_POST);

        if (setHostName) connection.setRequestProperty("Host", "localhost:48897"); //Fürs debuggen am Emulator


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

    public static String sendFiletoServer(String urlString, File toSend, String token, boolean useToken) throws IOException {
        //Variablen für File-Wrapping
        final String boundary = "===" + System.currentTimeMillis() + "===";
        final String LINE_FEED = "\r\n";

        //Header initialisieren
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setRequestMethod(REQUEST_POST);

        if (setHostName) connection.setRequestProperty("Host", "localhost:48897"); //Fürs debuggen am Emulator

        connection.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);

        if (useToken) {
            connection.setRequestProperty("authorization", "bearer " + token);
        }

        Log.d(LOG_TAG, "Connecting to URL: " + urlString);
        Log.d(LOG_TAG, connection.getRequestProperties().toString());

        //Streams aufbauen
        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter( connection.getOutputStream(),  "UTF-8"));

        //File senden
        String fileName = toSend.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + "uploadFile"
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: image/jpeg")
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(toSend);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();

        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        Log.d(LOG_TAG, "Code " + responseCode + ": " + responseMessage);

        connection.disconnect();

        return (responseCode == 200 || responseCode == 201) ? responseMessage : BAD_REQUEST;
    }

    public static String sendStringToServer(String urlString, String message, String requestMethod, String token, boolean useToken) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);

        if (setHostName) connection.setRequestProperty("Host", "localhost:48897"); //Fürs debuggen am Emulator

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

    public static Bitmap getImageFromServer(String urlString, String message, String requestMethod, String token, boolean useToken) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);

        if (setHostName) connection.setRequestProperty("Host", "localhost:48897"); //Fürs debuggen am Emulator

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

        Bitmap ret = null;

        if (responseCode == 200) {
            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
            ret = BitmapFactory.decodeStream(in);
            Log.d(LOG_TAG, "Response message: " + ret);
        }

        connection.disconnect();

        return ret;
    }
}
