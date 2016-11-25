package com.sys.velazquez.casanova.workshopasistance.utils;

/**
 * Created by EverNote on 08/07/16.
 */
public class RequestPlaces {

    private static String HOST = "http://54.87.140.128/";
    private static String API_PATH = "ServiceApp/services/rest/";
    private static String API_GET_COMMENTS = "getComentarios";
    private static String API_SEND_COMMENT = "setComentario";
    private static String API_GET_LOCATION = "getUbicaciones2";

    public static String API_GET_COMMENTS() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HOST);
        stringBuilder.append(API_PATH);
        stringBuilder.append(API_GET_COMMENTS);
        return stringBuilder.toString();
    }

    public static String API_SEND_COMMENT() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HOST);
        stringBuilder.append(API_PATH);
        stringBuilder.append(API_SEND_COMMENT);
        return stringBuilder.toString();
    }

    public static String API_GET_LOCATION() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HOST);
        stringBuilder.append(API_PATH);
        stringBuilder.append(API_GET_LOCATION);
        return stringBuilder.toString();
    }

}
