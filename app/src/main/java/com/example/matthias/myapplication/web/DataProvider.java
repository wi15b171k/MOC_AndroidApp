package com.example.matthias.myapplication.web;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.matthias.myapplication.Entities.Coordinates;
import com.example.matthias.myapplication.Entities.Person;
import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.Entities.UserImage;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthias on 28.01.2018.
 */

public class DataProvider {

    private static final String BASE_URL = "http://wi-gate.technikum-wien.at:60349/"; //Fürs HAndy - IP vom Laptop
    //private static final String BASE_URL = "http://10.0.2.2:48897/"; //Für den Emulatr
    private static final String LOG_TAG = DataProvider.class.getCanonicalName();

    public static boolean register(String firstname, String lastname, String mail, String password, String passwordConfirm) throws IOException {
        String urlString = BASE_URL + "api/Account/Register";

        JSONObject toSend = new JSONObject();

        try {
            toSend.put("Email", mail);
            toSend.put("Password", password);
            toSend.put("ConfirmPassword", passwordConfirm);
            toSend.put("FirstName", firstname);
            toSend.put("LastName", lastname);
        } catch (JSONException e) {
            return false;
        }

        String response = InternetConnection.sendJSONtoServer(urlString, toSend, "", false);

        return response != InternetConnection.BAD_REQUEST ? true : false;
    }

    public static String login(String mail, String password) throws IOException {
        String urlString = BASE_URL + "token";

        String toSend = "username=" + mail + "&password=" + password +"&grant_type=password";

        String response = InternetConnection.sendStringToServer(urlString, toSend, InternetConnection.REQUEST_POST,"", false);
        Log.d(LOG_TAG, response);

        String accessToken = "";

        try {
            JSONObject json = new JSONObject(response);
            accessToken = json.getString("access_token");
            Log.d(LOG_TAG, "Access token: " + accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return accessToken;
    }

    public static boolean logout(String token) throws IOException {
        String urlString = BASE_URL + "api/Account/Logout";

        String response = InternetConnection.sendStringToServer(urlString, "", InternetConnection.REQUEST_POST, token, true);

        if (response != InternetConnection.BAD_REQUEST) {
            return true;
        }

        return false;
    }

    public static Trip getLatestTrip(String token) throws IOException {
        String urlString = BASE_URL + "api/trip";

        String response = InternetConnection.sendStringToServer(urlString, "", InternetConnection.REQUEST_GET, token, true);

        Trip trip = null;
        
        try {
            if (response != InternetConnection.BAD_REQUEST) {
                trip = new Trip();
                JSONObject json = new JSONObject(response);
                trip.id = json.getInt("TripId");
                trip.name = json.getString("Title");
                trip.isUserTrip = true;

                trip.coordinates = new ArrayList<Coordinates>();

                JSONArray coordinates = json.getJSONArray("Coordinates");
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONObject item = coordinates.getJSONObject(i);

                    double lat = item.getDouble("Latitude");
                    double lng = item.getDouble("Longitude");

                    trip.coordinates.add(new Coordinates(lat, lng));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            trip = null;
        }

        return trip;
    }

    public static List<UserImage> getPicInfoByTripId(String token, int tripId) throws IOException {
        String urlString = BASE_URL + "api/pics/" + tripId;

        String response = InternetConnection.sendStringToServer(urlString, "", InternetConnection.REQUEST_GET, token, true);
        Log.d(LOG_TAG, "String response: " + response);

        ArrayList<UserImage> userImages = new ArrayList<UserImage>();

        if (response != InternetConnection.BAD_REQUEST) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    UserImage userImage = new UserImage();
                    userImage.id = item.getInt("PhotoId");
                    userImage.coordinates = new Coordinates(
                            item.getJSONObject("Coordinate").getDouble("Latitude"),
                            item.getJSONObject("Coordinate").getDouble("Longitude")
                    );

                    userImages.add(userImage);
                }
            } catch (JSONException e) {
                userImages = new ArrayList<UserImage>();
            }
        }

        return userImages;
    }

    public static Bitmap getPicByPicId(String token, int picId, int width, int height) throws IOException {
        String urlString = BASE_URL + "api/pic/" + picId + "/" + width + "/" + height;

        Bitmap response = InternetConnection.getImageFromServer(urlString, "", InternetConnection.REQUEST_GET, token, true);

        Log.d(LOG_TAG, "Response from image: " + response);

        return response;
    }

    public static List<UserImage> getMultiplePicsByCoordinates(String token, int tripId, List<Coordinates> coordinates) {
        //TODO call data provider
        return new ArrayList<UserImage>();
    }

    public static boolean savePicForTrip(String token, int tripId, Bitmap image, Coordinates coordinate) {
        //TODO call data provider
        return true;
    }

    public static boolean deletePicById(String token, int picId) {
        //TODO call data provider
        return true;
    }

    public static boolean meldePicById(String token, int picId) {
        //TODO call data provider
        return true;
    }

    public static boolean createTrip(String token, int userId, String name) {
        //TODO call data provider
        return true;
    }

    public static List<Trip> getTripsByUserId(String token, int userId) {
        //TODO call data provider
        return new ArrayList<Trip>();
    }

    public static Trip getTripById(String token, int tripId, int userId) {
        //TODO call data provider
        return new Trip();
    }

    public List<Person> getFriendsByUserId(String token, int userId) {
        //TODO call data provider
        return new ArrayList<Person>();
    }

    public List<Person> searchFriendsByName(String token, String searchString) {
        //TODO call data provider
        return new ArrayList<Person>();
    }



    public boolean respondToInvitationByUserId(String token, int followerId, int folgt_Id, boolean accepts) {
        //TODO call data provider
        return true;
    }

    public List<Person> getInvitationsByUserId(String token, int userId) {
        //TODO call data provider
        return new ArrayList<Person>();
    }
}
