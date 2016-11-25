package com.sys.velazquez.casanova.workshopasistance.utils;

import android.graphics.Bitmap;

/**
 * Created by edergomez on 10/11/16.
 */

public class Singleton {

    private static Singleton instance;

    public Bitmap getWorkshop_image() {
        return workshop_image;
    }

    public void setWorkshop_image(Bitmap workshop_image) {
        this.workshop_image = workshop_image;
    }

    private Bitmap workshop_image;

    protected Singleton() {
        // Exists only to defeat instantiation.
    }

    public static Singleton getInstance() {
        if (instance == null)
            instance = new Singleton();

        return instance;
    }

}
