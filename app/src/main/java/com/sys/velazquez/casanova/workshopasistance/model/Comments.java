package com.sys.velazquez.casanova.workshopasistance.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by EverNote on 08/07/16.
 */
public class Comments implements Serializable,Comparable<Comments> {

    @SerializedName("fcComentario")
    private String comment;

    @SerializedName("fnCalificacion")
    private int qualification;

    @SerializedName("fdFecha")
    private String date;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getQualification() {
        return qualification;
    }

    public void setQualification(int qualification) {
        this.qualification = qualification;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(Comments other) {
        return this.date.compareTo(other.date);
    }
}
