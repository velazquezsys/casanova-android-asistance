package com.sys.velazquez.casanova.workshopasistance.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import com.sys.velazquez.casanova.workshopasistance.R;

import static com.sys.velazquez.casanova.workshopasistance.utils.Const.ID_CAT;

/**
 * Created by EverNote on 08/07/16.
 */
public class Utils {

    public final static String TAG = Utils.class.getSimpleName();

    public static Drawable getIconByCategory(Context context) {
        Drawable imageId = null;
        imageId = context.getResources().getDrawable(R.drawable.icon_mecanico_48);

        return imageId;
    }

    public static boolean isOnline(Activity a) {

        ConnectivityManager cm = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(a);
            builder.setMessage("Comprueba la conexión a internet");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
    }

    public static String getJsonLocation(int cat, LatLng latLng) {
        String jsonReult = null;
        JSONObject params = new JSONObject();
        try {
            params.put("idTipo", cat);
            params.put("idStatus", -1);
            params.put("ubicacionActual", latLng.latitude + "," + latLng.longitude);
            jsonReult = params.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            Log.d(TAG, "JSON: " + jsonReult);
            return jsonReult;
        }
    }

    public static String getJsonAllComments(int idLugar) {
        String jsonReult = null;
        JSONObject params = new JSONObject();
        try {
            params.put("idLugar", idLugar);

            jsonReult = params.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            Log.d(TAG, "JSON: " + jsonReult);
            return jsonReult;
        }
    }


    public static String getJsonComment(int idLugar, String comment, int c, String loc) {
        String jsonReult = null;
        JSONObject params = new JSONObject();
        try {
            params.put("idLugar", idLugar);
            params.put("usuario", "Android");
            params.put("comentario", comment);
            params.put("calificacion", c);
            params.put("coordenadas", loc);
            jsonReult = params.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            Log.d(TAG, "JSON: " + jsonReult);
            return jsonReult;
        }
    }

    public static RetryPolicy getRetryPolicy() {
        return new DefaultRetryPolicy(
                1500,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    public static void saveCat(Bundle bundle, Context context) {
        SharedPreferences sp = context.getSharedPreferences("place", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(ID_CAT, bundle.getInt(ID_CAT));
        editor.commit();
    }

    public static int getCat(Context context) {
        SharedPreferences sp = context.getSharedPreferences("place", 0);
        return sp.getInt(ID_CAT, 1);
    }


    /*
      AlertDialog.Builder builder = new AlertDialog.Builder(a);
            builder.setMessage("No tienes conexion a la red")
                    .setCancelable(true)
                    .setPositiveButton("OK", null);
            AlertDialog alert = builder.create();
            alert.show();
     */

}
