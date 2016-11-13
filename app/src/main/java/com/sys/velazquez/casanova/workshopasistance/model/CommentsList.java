package com.sys.velazquez.casanova.workshopasistance.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by EverNote on 08/07/16.
 */
public class CommentsList implements Serializable {

    @SerializedName("noComentarios")
    private int nComments;

    @SerializedName("calificacionTotal")
    private Double sumQualification;

    @SerializedName("comentarios")
    private List<Comments> commentsList;

    public int getnComments() {
        return nComments;
    }

    public void setnComments(int nComments) {
        this.nComments = nComments;
    }

    public Double getSumQualification() {
        return sumQualification;
    }

    public void setSumQualification(Double sumQualification) {
        this.sumQualification = sumQualification;
    }

    public List<Comments> getCommentsList() {
        Collections.sort(commentsList);
        Collections.reverse(commentsList);
        return commentsList;
    }

    public void setCommentsList(List<Comments> commentsList) {
        this.commentsList = commentsList;
    }

}
