package pl.eiti.cociekawego;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.eiti.cociekawego.callers.AsyncResponse;
import pl.eiti.cociekawego.callers.CallApi;
import pl.eiti.cociekawego.utils.Constants;
import pl.eiti.cociekawego.utils.CustomInfoWindowAdapter;

/**
 * Created by krystian on 2016-05-04.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AsyncResponse, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private String url;
    private String[] geoLocation = new String[]{null, null}; //geoLocation[0] is  latitude
                                                            //geoLocation[1] is longitude

    private CallApi callApi;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getDataFromIntent();

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    protected void onStart(){
        mGoogleApiClient.connect();
        super.onStart();
    }


    @Override
    protected void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

    }


    public JSONObject processFinish(JSONObject result){
        this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(geoLocation[0]), Double.parseDouble(geoLocation[1])), 14.0f));
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
        ArrayList<String[]> jsonData = new ArrayList<>();

        try {
            JSONArray data = result.getJSONArray(Constants.veturiloData);
            Log.d("CoCiekawego MapsActivity", "dlugość tablicy: " + data.length());
            for (int i = 0; i<data.length(); i++){
                JSONObject object = data.getJSONObject(i);

                JSONObject geometry = object.getJSONObject(Constants.veturiloGeometry);
                JSONArray properties = object.getJSONArray(Constants.veturiloProperties);



                JSONObject coordinates = geometry.getJSONObject(Constants.veturiloCoordinates);
                String lat = coordinates.getString(Constants.veturiloLatitude);
                String lon = coordinates.getString(Constants.veturiloLongitude);

                String nrStacji = properties.getJSONObject(2).getString(Constants.value);  //get JSONObject which contains  station name
                String bikesLeft = properties.getJSONObject(3).getString(Constants.value); //get information how many bikes are avaliable in that station
                String standLeft = properties.getJSONObject(4).getString(Constants.value); //get information how many stands are avaliable in that station

                String[] stationData = new String[]{nrStacji, lat, lon, bikesLeft, standLeft};
                jsonData.add(stationData);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addMapMarkers(jsonData);
        return result;
    }

    private void addMapMarkers(ArrayList<String[]> jsonData) {

        for (int i = 0; i<jsonData.size();i++){
            Double lat = Double.parseDouble(jsonData.get(i)[1]);
            Double lon = Double.parseDouble(jsonData.get(i)[2]);
            String stationName = new String (Constants.stationNumber  + jsonData.get(i)[0]);
            String snippet  = new String (Constants.bikeLeft + jsonData.get(i)[3] + "\r\n" + Constants.standLeft + jsonData.get(i)[4]);

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat,lon))
                    .title(stationName)
                    .snippet(snippet));
        }

    }

    @Override
    public void onConnected(Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == 0 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == 0){
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null){
                this.geoLocation[0] = String.valueOf(mLastLocation.getLatitude());
                this.geoLocation[1] = String.valueOf(mLastLocation.getLongitude());
                Log.d("CoCiekawego MapsActivity", "Location: " + geoLocation[0] + geoLocation[1]);
                callApi = new CallApi(this);
                callApi.delegate = this;
                callApi.execute(url, geoLocation[1], geoLocation[0], "1000");

            }else{
                Log.d("CoCiekawego MapsActivity", "mLastLocation null");
            }
        }else{
            Log.d("CoCiekawego MapsActivity", "permissionProblems xD ");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        //nothing
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //nothing
    }



    //Get url data from intent
    public void getDataFromIntent() {
        Intent intent = getIntent();
        this.url = intent.getStringExtra("url");
    }
}
