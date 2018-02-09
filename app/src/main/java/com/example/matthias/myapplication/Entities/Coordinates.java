package com.example.matthias.myapplication.Entities;

import java.io.Serializable;

/**
 * Created by matthiask on 09.02.2018.
 */

public class Coordinates implements Serializable {

    public double latitude;
    public double longitude;

    public Coordinates(double lat, double lng) {
        latitude = lat;
        longitude = lng;
    }

}
