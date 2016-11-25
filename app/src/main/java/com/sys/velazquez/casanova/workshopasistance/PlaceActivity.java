package com.sys.velazquez.casanova.workshopasistance;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.sys.velazquez.casanova.workshopasistance.adapter.PlaceAdapter;
import com.sys.velazquez.casanova.workshopasistance.iface.PlaceSelector;
import com.sys.velazquez.casanova.workshopasistance.model.Place;
import com.sys.velazquez.casanova.workshopasistance.model.PlaceList;
import com.sys.velazquez.casanova.workshopasistance.service.VolleyService;
import com.sys.velazquez.casanova.workshopasistance.utils.Const;
import com.sys.velazquez.casanova.workshopasistance.utils.RequestPlaces;
import com.sys.velazquez.casanova.workshopasistance.utils.Utils;

import static com.sys.velazquez.casanova.workshopasistance.utils.Const.ID_CAT;

/**
 * Created by EverNote on 08/07/16.
 */
public class PlaceActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, PlaceSelector {

    private final static String TAG = PlaceActivity.class.getName();
    private static final int REQUEST_CODE_LOCATION = 2;
    private static final int REQUEST_WRITE_STORAGE = 112;

    Context context;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    RecyclerView recycler;
    PlaceAdapter placeAdapter;
    List<Place> placeList = new ArrayList<>();
    private int CAT = 0;
    private String title = null;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng latLng;

    private SearchView sv_workshop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recycler = (RecyclerView) findViewById(R.id.recycler_view);

        context = this.getApplicationContext();

        if (getIntent().getExtras() != null) {
            CAT = getIntent().getExtras().getInt(ID_CAT);
            init();
        } else {
            CAT = Utils.getCat(getApplicationContext());
            init();
        }

        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        placeAdapter = new PlaceAdapter(placeList, getApplicationContext(), this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(placeAdapter);

        buildGoogleApiClient();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Buscando talleres cercanos...");
        progressDialog.show();
        progressDialog.setCancelable(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        SearchManager SManager =  (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.it_search);
        SearchView searchViewAction = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchViewAction.setSearchableInfo(SManager.getSearchableInfo(getComponentName()));
        searchViewAction.setIconifiedByDefault(false);
        searchViewAction.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(PlaceActivity.this, "You searched " + s, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                final List<Place> filteredModelList = filter(placeList, s);
                placeAdapter.setFilter(filteredModelList);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private List<Place> filter(List<Place> tmp_places, String query) {
        query = query.toLowerCase();

        final List<Place> filteredModelList = new ArrayList<>();

        for (Place model : tmp_places) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    public void init() {
        title = Const.MECANICO;
        toolbar.setTitle(title);
        toolbar.setLogo(Utils.getIconByCategory(getApplicationContext()));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_LOCATION);
        } else {
            getMyLocation();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length == 1 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getMyLocation();
            }
        }
    }

    protected void getMyLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.d(TAG, " Latitude: " + mLastLocation.getLatitude() + " Longitude: " + mLastLocation.getLongitude());
            loadService();
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
            latLng = null;
        }

    }

    void loadService() {

        String url = RequestPlaces.API_GET_LOCATION();

        final String json = Utils.getJsonLocation(CAT, latLng);
        Log.d(TAG, "LoadService: " + url);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        String decode;

                        try {
                            decode = URLDecoder.decode(URLEncoder.encode(response, "iso8859-1"),"UTF-8");
                            PlaceList mPlaceList = new Gson().fromJson(decode, PlaceList.class);
                            Log.d(TAG, "tamanio " + placeList.size());

                            placeList.addAll(mPlaceList.getPlaceList());
                            placeAdapter.notifyDataSetChanged();

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
                return "application/json; charset=utf-8";
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

    @Override
    public void selector(Place place) {
        place.setIdCat(CAT);
        Intent i = new Intent(this.getApplication(), PlaceDetailActivity.class);
        i.putExtra(Const.LAT, latLng.latitude);
        i.putExtra(Const.LON, latLng.longitude);
        i.putExtra(Const.PLACE, place);
        startActivity(i);
    }


}
