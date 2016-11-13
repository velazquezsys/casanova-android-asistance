package com.sys.velazquez.casanova.workshopasistance.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EverNote on 08/07/16.
 */
public class PlaceList {

    @SerializedName("ubicaciones")
    private List<Place> placeList = new ArrayList<>();

    public List<Place> getPlaceList() {
        return placeList;
    }
}
