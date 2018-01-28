package com.example.matthias.myapplication.web;

import android.graphics.Bitmap;

import com.example.matthias.myapplication.Entities.Coordinate;
import com.example.matthias.myapplication.Entities.Person;
import com.example.matthias.myapplication.Entities.Trip;
import com.example.matthias.myapplication.Entities.UserImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthias on 28.01.2018.
 */

public class DataProvider {

    public static boolean register(String firstname, String lastname, String mail, String password) {
        //TODO call data provider
        return true;
    }

    public static AccessToken login(String mail, String password) {
        //TODO call data provider
        return new AccessToken();
    }

    public static Trip getLatestTrip(AccessToken token, int userId) {
        //TODO call data provider
        return new Trip();
    }

    public List<Coordinate> getCoordinatesByTripId(int tripId) {
        //TODO call data provider
        return new ArrayList<Coordinate>();
    }

    public static List<Bitmap> getPicsByTripId(int tripId) {
        //TODO call data provider
        return new ArrayList<Bitmap>();
    }

    public static List<UserImage> getMultiplePicsByCoordinates(int tripId, List<Coordinate> coordinates) {
        //TODO call data provider
        return new ArrayList<UserImage>();
    }

    public static boolean savePicForTrip(int tripId, Bitmap image, Coordinate coordinate) {
        //TODO call data provider
        return true;
    }

    public static boolean deletePicById(int picId) {
        //TODO call data provider
        return true;
    }

    public static boolean meldePicById(int picId) {
        //TODO call data provider
        return true;
    }

    public static boolean createTrip(int userId, String name) {
        //TODO call data provider
        return true;
    }

    public static List<Trip> getTripsByUserId(int userId) {
        //TODO call data provider
        return new ArrayList<Trip>();
    }

    public static Trip getTripById(int tripId, int userId) {
        //TODO call data provider
        return new Trip();
    }

    public List<Person> getFriendsByUserId(int userId) {
        //TODO call data provider
        return new ArrayList<Person>();
    }

    public List<Person> searchFriendsByName(String searchString) {
        //TODO call data provider
        return new ArrayList<Person>();
    }



    public boolean respondToInvitationByUserId(int followerId, int folgt_Id, boolean accepts) {
        //TODO call data provider
        return true;
    }

    public List<Person> getInvitationsByUserId(int userId) {
        //TODO call data provider
        return new ArrayList<Person>();
    }
}
