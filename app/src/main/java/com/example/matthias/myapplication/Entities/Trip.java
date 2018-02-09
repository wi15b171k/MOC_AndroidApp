package com.example.matthias.myapplication.Entities;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Matthias on 28.01.2018.
 */

public class Trip implements Serializable {
    public int id;
    public String name;
    public transient List<Coordinates> coordinates;
    public boolean isUserTrip;
}
