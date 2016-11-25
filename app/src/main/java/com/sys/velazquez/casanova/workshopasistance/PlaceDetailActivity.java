package com.sys.velazquez.casanova.workshopasistance;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.sys.velazquez.casanova.workshopasistance.fragment.DFragment;
import com.sys.velazquez.casanova.workshopasistance.model.CommentsList;
import com.sys.velazquez.casanova.workshopasistance.model.Place;
import com.sys.velazquez.casanova.workshopasistance.service.VolleyService;
import com.sys.velazquez.casanova.workshopasistance.utils.Const;
import com.sys.velazquez.casanova.workshopasistance.utils.RequestPlaces;
import com.sys.velazquez.casanova.workshopasistance.utils.Utils;
import com.sys.velazquez.casanova.workshopasistance.utils.Singleton;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Created by EverNote on 08/07/16.
 */
public class PlaceDetailActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener, LocationListener {

    private final static String TAG = PlaceDetailActivity.class.getName();
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5;

    private Place place;
    private Toolbar toolbar;

    private TextView tv_name;
    private TextView tv_respo;
    private TextView tv_address;
    private TextView tv_phones;
    private TextView tv_schedule;
    private TextView tv_distance;
    private TextView tv_qualification;
    private TextView tv_comments;
    private EditText ed_comment;
    private TextView tv_rating;
    private RatingBar ratingBar;
    private RatingBar ratingBarQualification;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogComment;
    private Double lat, lon = 0d;
    private Boolean locationEnabled = false;
    private LocationManager mLocManager;
    private CommentsList commentsList;
    private int numComments = 0;

    //private PhotoViewAttacher image_viewer;
    private ImageView iv_workshop;
    private LinearLayout ll_phone_content;
    private String all_phones;

    // The gesture threshold expressed in dp
    private static final float GESTURE_THRESHOLD_DP = 16.0f;

    private static  int blue_color =  Color.parseColor("#0a69b6");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        tv_name = (TextView) findViewById(R.id.tvPlaceName);
        tv_respo = (TextView) findViewById(R.id.tvResp);
        tv_address = (TextView) findViewById(R.id.tvAddress);
        tv_phones = (TextView) findViewById(R.id.tvPhones);
        tv_schedule = (TextView) findViewById(R.id.tvSchelud);
        tv_distance = (TextView) findViewById(R.id.tvDistance);
        tv_qualification = (TextView) findViewById(R.id.tvQualification);
        tv_comments = (TextView) findViewById(R.id.tvNoComments);
        ratingBarQualification = (RatingBar) findViewById(R.id.rbQualificarion);

        ed_comment = (EditText) findViewById(R.id.ed_comment);

        ratingBar = (RatingBar) findViewById(R.id.rb_cat);
        tv_rating = (TextView) findViewById(R.id.tv_ranking);

        ll_phone_content= (LinearLayout) findViewById(R.id.layout_phones_content);
        iv_workshop = (ImageView) findViewById(R.id.iv_workshop);

        if (getIntent().getExtras() != null) {
            place = (Place) getIntent().getExtras().getSerializable(Const.PLACE);
            lat = getIntent().getDoubleExtra(Const.LAT, 0);
            lon = getIntent().getDoubleExtra(Const.LON, 0);
            //setPlaceInView();
            serviceComments();
        }

        mLocManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);

//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ratingBar.setOnRatingBarChangeListener(this);

    }

    private void setPlaceInView() {
        Log.d(TAG, "setPlaceInView");
        try {
            if (place != null) {
                toolbar.setLogo(Utils.getIconByCategory(getApplicationContext()));
                toolbar.setTitle(place.getName());
                //tv_name.setText(place.getName());
                tv_address.setText(place.getAddress());
                tv_schedule.setText(place.getSchedule());
                tv_distance.setText(place.getDistance());
                tv_qualification.setText("" + place.getRanking());
                ratingBarQualification.setRating(Float.parseFloat(place.getRanking()));

                if (numComments == 1) {
                    tv_comments.setText(numComments + " Usuario ha calificado");
                } else {
                    tv_comments.setText(numComments + " Usuarios han calificado");
                }
                if (place.getAllRespon() != null) {
                    tv_respo.setText(place.getAllRespon());
                }

                all_phones=place.getAllPhones();

                if (all_phones != null) {
                    String[] phone_divider = all_phones.split("-");
                    if(phone_divider.length > 1) {
                        for (int i=0; i < phone_divider.length ; i++){
                            if(i == 0 ){
                                tv_phones.setText(phone_divider[i]);
                            }else {

                                TextView other_num = new TextView(this);
                                other_num.setText(phone_divider[i]);
                                other_num.setTextSize(15);
                                other_num.setTextColor(blue_color);
                                other_num.setTypeface(null,Typeface.BOLD);
                                other_num.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onClickTelefono(v);
                                    }
                                });
                                ll_phone_content.addView(other_num);
                            }
                        }
                    }else
                        tv_phones.setText(phone_divider[0]);
                }

            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void sendComment(View view) {
        Log.d(TAG, "sendComment");
        String comment;
        comment = ed_comment.getText().toString();
        int numStarts = tv_rating.getText().toString().isEmpty() ? 0 : Integer.parseInt(tv_rating.getText().toString());
        if (comment != null && !comment.isEmpty()) {
            serviceSendComment(comment, numStarts);
        } else {
            Toast.makeText(this.getApplicationContext(), "El comentario no puede estar vacio", Toast.LENGTH_LONG).show();
        }
    }

    private void serviceSendComment(String comment, int num) {

        if (Utils.isOnline(this)) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Enviando comentario...");
            progressDialog.show();
            progressDialog.setCancelable(false);

            String url = RequestPlaces.API_SEND_COMMENT();
            final String json = Utils.getJsonComment(place.getId(), comment, num, place.getCoordinates());
            Log.d(TAG, "LoadService: " + url);

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (jsonObject.has("message")) {
                                    String resultMessage = jsonObject.getString("message").toUpperCase();
                                    if (resultMessage.contains("OK")) {
                                        Toast.makeText(getApplicationContext(), "¡Comentario enviado!", Toast.LENGTH_LONG).show();
                                        resetFields();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "No se pudo enviar el comentario, intentalo más tarde", Toast.LENGTH_LONG).show();
                                    }
                                }

                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse " + error.getMessage());
                    progressDialog.dismiss();
                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                protected String getParamsEncoding() {
                    return "utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return json == null ? null : json.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                json, "UTF-8");
                        return null;
                    }
                }
            };

            stringRequest.setRetryPolicy(Utils.getRetryPolicy());
            VolleyService.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    public void serviceComments() {
        if (Utils.isOnline(this)) {

            progressDialogComment = new ProgressDialog(this);
            progressDialogComment.setMessage("Obteniendo detalles del taller...");
            progressDialogComment.show();
            progressDialogComment.setCancelable(false);


            String url = RequestPlaces.API_GET_COMMENTS();
            final String json = Utils.getJsonAllComments(place.getId());
            Log.d(TAG, "LoadService: " + url);

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialogComment.dismiss();
                            String decode;

                            try {
                                decode = URLDecoder.decode(URLEncoder.encode(response, "iso8859-1"), "UTF-8");
                                commentsList = new Gson().fromJson(decode, CommentsList.class);
                                if (commentsList.getCommentsList().size() > 0) {
                                    numComments = commentsList.getnComments();
                                }
                                setPlaceInView();

                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse " + error.getMessage());
                    progressDialogComment.dismiss();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return json == null ? null : json.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                json, "utf-8");
                        return null;
                    }
                }
            };

            stringRequest.setRetryPolicy(Utils.getRetryPolicy());
            VolleyService.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
        } else {
            setPlaceInView();
        }
    }


    public void getAllComments(View view) {

        if (Utils.isOnline(this)) {

            progressDialogComment = new ProgressDialog(this);
            progressDialogComment.setMessage("Obteniendo comentarios...");
            progressDialogComment.show();
            progressDialogComment.setCancelable(false);

            String url = RequestPlaces.API_GET_COMMENTS();
            final String json = Utils.getJsonAllComments(place.getId());
            Log.d(TAG, "LoadService: " + url);

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialogComment.dismiss();
                            String decode;

                            try {
                                decode = URLDecoder.decode(URLEncoder.encode(response, "iso8859-1"), "UTF-8");
                                commentsList = new Gson().fromJson(decode, CommentsList.class);
                                if (commentsList.getCommentsList().size() > 0) {
                                    setValuesDialog(commentsList);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Aún no hay comentarios para este taller", Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse " + error.getMessage());
                    progressDialogComment.dismiss();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return json == null ? null : json.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                json, "utf-8");
                        return null;
                    }
                }
            };

            stringRequest.setRetryPolicy(Utils.getRetryPolicy());
            VolleyService.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    public void setValuesDialog(CommentsList commentsList) {
        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable("COMMENT", commentsList);
            FragmentManager fm = getSupportFragmentManager();

            DFragment dialogFragment = new DFragment();
            dialogFragment.setArguments(bundle);
            dialogFragment.show(fm, "TAG");

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void resetFields() {
        Log.d(TAG, "resetFields");
        ratingBar.setRating(0F);
        ed_comment.setText("");
    }


    public void goPlaceInMap(View view) {

        if (Utils.isOnline(this)) if (checkLocation()) {

            Uri mapUri;
            Intent mapIntent;

            try {
                mapUri = Uri.parse(String.format(Locale.ENGLISH, String.format("geo:0,0?q=%s (\"%s\")", place.getCoordinates(), place.getAddress())));
                mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                startActivity(mapIntent);

                /*Te muestra la ruta con google maps y puedes dar iniciar navegación
                mapUri = Uri.parse(String.format("http://maps.google.com/maps?daddr=%s (%s)", place.getCoordinates(), place.getAddress()));
                mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                startActivity(mapIntent);*/
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(this, "Please install a maps application", Toast.LENGTH_LONG).show();
            }

            /*Inicia navegación automaticamente Google Maps
            mapUri = Uri.parse(String.format("google.navigation:q=%s", place.getCoordinates()));
            mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);*/

            /*Waze
            mapUri = Uri.parse(String.format("waze://?ll=%s&navigate=yes&z=10",place.getCoordinates()));
            mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            startActivity(intent);*/
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Los servicios de localizacion estan desactivados, ¿desea activarlos?")
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        int value = (int) v;
        tv_rating.setText(String.valueOf(value));
    }


    public boolean checkLocation() {
        locationEnabled = true;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
            return false;
        }
        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        } else if (mLocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        } else {
            locationEnabled = false;
        }
        return locationEnabled;
    }

    public void onClickTelefono(View v) {
        TextView tv = (TextView) v;
        Intent intentCall;

        intentCall = new Intent(android.content.Intent.ACTION_DIAL, Uri.parse(String.format("tel:%s", tv.getText().toString())));
        startActivity(intentCall);
    }

    public void onClickImageWorkshop(View v) {

        Intent i = new Intent(this.getApplication(), PlaceViewer.class);

        Bitmap bmp=((BitmapDrawable)iv_workshop.getDrawable()).getBitmap();

        Singleton sin=Singleton.getInstance();
        sin.setWorkshop_image(bmp);
        i.putExtra("workshop_name",place.getName());
        i.putExtra("workshop_id",place.getIdCat());
        startActivity(i);
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

}
