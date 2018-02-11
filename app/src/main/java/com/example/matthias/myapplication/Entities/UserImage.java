package com.example.matthias.myapplication.Entities;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Matthias on 28.01.2018.
 */

public class UserImage implements Serializable {
    public int id;
    public Bitmap image;
    public Coordinates coordinates;

    public UserImage copyPicInfo() {
        UserImage ret = new UserImage();

        ret.id = id;
        ret.coordinates = new Coordinates(coordinates.latitude, coordinates.longitude);

        return ret;
    }
}
