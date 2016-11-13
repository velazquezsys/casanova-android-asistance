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

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.sys.velazquez.casanova.workshopasistance.R;
import com.sys.velazquez.casanova.workshopasistance.utils.log4j.MyLogger;

import static com.sys.velazquez.casanova.workshopasistance.utils.Const.ID_CAT;

/**
 * Created by EverNote on 08/07/16.
 */
public class Utils {

    public final static String TAG = Utils.class.getSimpleName();
    private static Logger LOG;

    static {
        LOG = MyLogger.getLogger(Utils.class);
    }

    public static String getNameCategory(int cat) {
        String name = null;
        switch (cat) {
            case Const.CAT_MECANICO:
                name = Const.MECANICO;
                break;
            case Const.CAT_ELECTRICO:
                name = Const.ELECTRICO;
                break;
            case Const.CAT_OTRO:
                name = Const.OTRO;
                break;
        }
        Log.d(TAG, name);
        return name;
    }

    public static Drawable getIconByCategory(int cat, Context context) {
        Drawable imageId = null;
        switch (cat) {
            case Const.CAT_MECANICO:
                imageId = context.getResources().getDrawable(R.drawable.icon_mecanico_48);
                break;
            case Const.CAT_ELECTRICO:
                imageId = context.getResources().getDrawable(R.drawable.icon_electrico_48);
                break;
            case Const.CAT_OTRO:
                imageId = context.getResources().getDrawable(R.drawable.icon_otro_48);
                break;
        }
        return imageId;
    }

    public static boolean isOnline(Activity a) {

        LOG.info("Validando el acceso a internet...");
        ConnectivityManager cm = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            LOG.info("Acceso a internet ok");
            return true;
        } else {
            LOG.info("No se tiene conexion a internet :(");

            AlertDialog.Builder builder = new AlertDialog.Builder(a);
            builder.setMessage("No tienes conexion internet");
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
