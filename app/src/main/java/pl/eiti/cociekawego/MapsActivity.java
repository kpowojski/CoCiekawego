package pl.eiti.cociekawego;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import pl.eiti.cociekawego.callers.AsyncResponse;
import pl.eiti.cociekawego.callers.CallApi;
import pl.eiti.cociekawego.utils.Constants;
import pl.eiti.cociekawego.utils.CustomInfoWindowAdapter;

/**
 * Created by krystian on 2016-05-04.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
                                                        AsyncResponse,
                                                        GoogleApiClient.ConnectionCallbacks,
                                                        GoogleApiClient.OnConnectionFailedListener,
                                                        GoogleMap.OnCameraChangeListener{


    private static float[] mapMarkersColors = new float[]{BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_ROSE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_YELLOW};
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private String atraction;
    private String url;
    private String[] geoLocation = new String[]{null, null}; //geoLocation[0] is  latitude
                                                            //geoLocation[1] is longitude

    //central camera positions, use to enable camera moves xD
    private boolean cameraPositioned;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getDataFromIntent();


        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.cameraPositioned = false;
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == 0 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == 0) {
            this.mMap.setMyLocationEnabled(true);
            this.mMap.getUiSettings().setMyLocationButtonEnabled(true);
            this.mMap.getUiSettings().setZoomControlsEnabled(true);
            this.mMap.setOnCameraChangeListener(this);
        }

    }


    public boolean processFinish(JSONObject result){
        if (result == null){
            Toast.makeText(this, "Nie można połaczyć się z serwerem. Proszę sprobować ponownie później.", Toast.LENGTH_SHORT).show();
            return false;
        }

        this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(this.geoLocation[0]), Double.parseDouble(this.geoLocation[1])), 14.0f));
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));


        switch(this.atraction){
            case Constants.veturilo:
                parseVeturiloData(result);
                break;
            case Constants.parkAndRide:
                parseParkAndRideData(result);
                break;
            case Constants.sportFacilities:
                parseSportFacilities(result);
                break;
            case Constants.swimmingPools:
                parseSwimmingPools(result);
                break;
            case Constants.restaurant:
                parseRestaurnat(result);
                break;
            default:
                Toast.makeText(this, "Nie można połaczyć się z serwerem. Proszę sprobować ponownie później.", Toast.LENGTH_LONG).show();
        }

        return true;

    }

    private void parseRestaurnat(JSONObject data) {
        //parse messge received from Google Places API

        ArrayList<String[]> jsonData = new ArrayList<>();

        try{
            JSONArray results = data.getJSONArray(Constants.results);
            for (int i=0; i<results.length();i++){
                JSONObject singleResult = results.getJSONObject(i);

                JSONObject geometryLocation = singleResult.getJSONObject("geometry").getJSONObject(Constants.location);
                String lat = geometryLocation.getString(Constants.latitude);
                String lon = geometryLocation.getString(Constants.longitude_v2);

                String name = singleResult.getString("name");
                String street = singleResult.getString("vicinity");



                String[] tab = new String[] {lat, lon, name, street};
                jsonData.add(tab);
            }

        }catch (JSONException e){
            e.printStackTrace();
            wrongDataDownloaded();
        }catch (Exception e){
            communicationProblem();
        }

        addMapMarkersForRestaurant(jsonData);

    }



    private void parseSportFacilities(JSONObject result) {
        ArrayList<String[]> jsonData = new ArrayList<>();

        try{
            JSONArray data = result.getJSONArray(Constants.data);
            for (int i=0; i<data.length();i++){
                JSONObject singleObject = data.getJSONObject(i);

                JSONObject geometry = singleObject.getJSONObject(Constants.geometry);
                JSONArray properties = singleObject.getJSONArray(Constants.properties);

                JSONObject coordinates = geometry.getJSONObject(Constants.coordinates);
                String lat = coordinates.getString(Constants.latitude);
                String lon = coordinates.getString(Constants.longitude);

                String address = properties.getJSONObject(0).getString(Constants.value);
                String addressNr = properties.getJSONObject(1).getString(Constants.value);
                String description = properties.getJSONObject(2).getString(Constants.value);
                String telephone = properties.getJSONObject(5).getString(Constants.value);
                String[] facilitiesData = new String[]{lat,lon, address+" "+addressNr, description, telephone};
                jsonData.add(facilitiesData);
            }

        }catch(JSONException e){
            wrongDataDownloaded();
        }catch(Exception e){
            communicationProblem();
        }


        addMapMarkersForSportFacilities(jsonData);

    }


    private void parseVeturiloData(JSONObject result) {
        ArrayList<String[]> jsonData = new ArrayList<>();
        try {

            JSONArray data = result.getJSONArray(Constants.data);
            Log.d("CoCiekawego MapsActivity", "dlugość tablicy: " + data.length());
            for (int i = 0; i<data.length(); i++){
                JSONObject object = data.getJSONObject(i);

                JSONObject geometry = object.getJSONObject(Constants.geometry);
                JSONArray properties = object.getJSONArray(Constants.properties);



                JSONObject coordinates = geometry.getJSONObject(Constants.coordinates);
                String lat = coordinates.getString(Constants.latitude);
                String lon = coordinates.getString(Constants.longitude);

                String nrStacji = properties.getJSONObject(2).getString(Constants.value);  //get JSONObject which contains  station name
                String bikesLeft = properties.getJSONObject(3).getString(Constants.value); //get information how many bikes are avaliable in that station
                String standLeft = properties.getJSONObject(4).getString(Constants.value); //get information how many stands are avaliable in that station

                String[] stationData = new String[]{nrStacji, lat, lon, bikesLeft, standLeft};
                jsonData.add(stationData);


            }
        } catch (JSONException e) {
            e.printStackTrace();
            wrongDataDownloaded();
        } catch (Exception e){
            communicationProblem();
        }
        addMapMarkersForVeturilo(jsonData);


    }


    private void parseParkAndRideData(JSONObject result){
        ArrayList<String[]> jsonData = new ArrayList<>();
        try {

            JSONArray data = result.getJSONArray(Constants.data);
            Log.d("CoCiekawego MapsActivity", "dlugość tablicy: " + data.length());
            for (int i = 0; i<data.length(); i++){
                JSONObject object = data.getJSONObject(i);

                JSONObject geometry = object.getJSONObject(Constants.geometry);
                JSONArray properties = object.getJSONArray(Constants.properties);



                JSONObject coordinates = geometry.getJSONObject(Constants.coordinates);
                String lat = coordinates.getString(Constants.latitude);
                String lon = coordinates.getString(Constants.longitude);

                String name = properties.getJSONObject(4).getString(Constants.value);  //get JSONObject which contains  parking name
                String description = properties.getJSONObject(1).getString(Constants.value); //get description

                String[] stationData = new String[]{lat, lon, name, description};
                jsonData.add(stationData);


            }
        } catch (JSONException e) {
            wrongDataDownloaded();
        } catch (Exception e){
            communicationProblem();
        }

        addMapMarkersForParkAndRide(jsonData);
    }


    private void parseSwimmingPools(JSONObject result){
        ArrayList<String[]> jsonData = new ArrayList<>();
        try {

            JSONArray data = result.getJSONArray(Constants.data);
            for (int i = 0; i<data.length(); i++){
                JSONObject object = data.getJSONObject(i);

                JSONObject geometry = object.getJSONObject(Constants.geometry);
                JSONArray properties = object.getJSONArray(Constants.properties);



                JSONObject coordinates = geometry.getJSONObject(Constants.coordinates);
                String lat = coordinates.getString(Constants.latitude);
                String lon = coordinates.getString(Constants.longitude);

                String street = properties.getJSONObject(0).getString(Constants.value);  //get street name
                String buildingNr = properties.getJSONObject(1).getString(Constants.value); //get building number; this lines conncateneted will be used to generate facilities address
                String description = properties.getJSONObject(2).getString(Constants.value); //get attraction description
                String telephone = properties.getJSONObject(3).getString(Constants.value);
                String webPage = properties.getJSONObject(4).getString(Constants.value);
                String[] stationData = new String[]{lat, lon, street+" "+buildingNr, description, telephone, webPage};
                jsonData.add(stationData);


            }
        } catch (JSONException e) {
            wrongDataDownloaded();
        } catch (Exception e){
            communicationProblem();
        }

        addMapMarkersForSwimmingPools(jsonData);
    }

    private void addMapMarkersForSwimmingPools(ArrayList<String[]> jsonData) {
        this.mMap.clear();

        //lat, lon, street buildingNr, description, telephone, webPage
        for (int i = 0; i<jsonData.size();i++){
            Double lat = Double.parseDouble(jsonData.get(i)[0]);
            Double lon = Double.parseDouble(jsonData.get(i)[1]);
            String stationName = new String (Constants.name  + jsonData.get(i)[2]);
            String snippet  = new String (Constants.description + jsonData.get(i)[3]);

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat,lon))
                    .title(stationName)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(mapMarkersColors[new Random().nextInt(mapMarkersColors.length)])));
        }
        this.cameraPositioned = true;
    }

    private void addMapMarkersForParkAndRide(ArrayList<String[]> jsonData) {
        this.mMap.clear();

        //lat, lon, name, description
        for (int i = 0; i<jsonData.size();i++){
            Double lat = Double.parseDouble(jsonData.get(i)[0]);
            Double lon = Double.parseDouble(jsonData.get(i)[1]);
            String stationName = new String (Constants.name  + jsonData.get(i)[2]);
            String snippet  = new String (Constants.description + jsonData.get(i)[3]);

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat,lon))
                    .title(stationName)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(mapMarkersColors[new Random().nextInt(mapMarkersColors.length)])));
        }
        this.cameraPositioned = true;
    }

    private void addMapMarkersForVeturilo(ArrayList<String[]> jsonData) {

        this.mMap.clear();
        for (int i = 0; i<jsonData.size();i++){
            Double lat = Double.parseDouble(jsonData.get(i)[1]);
            Double lon = Double.parseDouble(jsonData.get(i)[2]);
            String stationName = new String (Constants.stationNumber  + jsonData.get(i)[0]);
            String snippet  = new String (Constants.bikeLeft + jsonData.get(i)[3] + "\r\n" + Constants.standLeft + jsonData.get(i)[4]);

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat,lon))
                    .title(stationName)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(mapMarkersColors[new Random().nextInt(mapMarkersColors.length)])));
        }
        this.cameraPositioned = true;
    }

    private void addMapMarkersForSportFacilities(ArrayList<String[]> jsonData) {
        this.mMap.clear();
        //jsonData[i] = lat,lon, address+" "+addressNr, description, telephone
        for (int i =0; i<jsonData.size();i++){
            Double lat = Double.parseDouble(jsonData.get(i)[0]);
            Double lon = Double.parseDouble(jsonData.get(i)[1]);

            String name = jsonData.get(i)[2];
            String snippet = new String(Constants.sportFacilitiesDescription + jsonData.get(i)[3]+ "\r\n" + Constants.telephone + jsonData.get(i)[4]);

            this.mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .title(name)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(mapMarkersColors[new Random().nextInt(mapMarkersColors.length)])));
        }
        this.cameraPositioned = true;
    }

    private void addMapMarkersForRestaurant(ArrayList<String[]> jsonData) {
        this.mMap.clear();
        //jsonData[i] = lat, lon, name, street, rating
        for (int i =0; i<jsonData.size();i++){
            Double lat = Double.parseDouble(jsonData.get(i)[0]);
            Double lon = Double.parseDouble(jsonData.get(i)[1]);

            String name = jsonData.get(i)[2];
            String snippet = "Adres " + jsonData.get(i)[3] + "\r\n";

            this.mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title(name)
                    .snippet(snippet)
                   .icon(BitmapDescriptorFactory.defaultMarker(mapMarkersColors[new Random().nextInt(mapMarkersColors.length)])));
        }
        this.cameraPositioned = true;
    }




    @Override
    public void onConnected(Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == 0 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == 0){
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null){
                this.geoLocation[0] = String.valueOf(mLastLocation.getLatitude());
                this.geoLocation[1] = String.valueOf(mLastLocation.getLongitude());
                Log.d("CoCiekawego MapsActivity", "Location: " + geoLocation[0] + geoLocation[1]);
                CallApi callApi = new CallApi(this);
                callApi.delegate = this;
                if (this.atraction.equals(Constants.restaurant)){
                    String url = prepareGooglePlacesURL();
                    callApi.execute(url);
                }else{
                    callApi.execute(url, geoLocation[1], geoLocation[0], "1000");
                }


            }else{
                Log.d("CoCiekawego MapsActivity", "mLastLocation null");
            }
        }else{
            Log.d("CoCiekawego MapsActivity", "permissionProblems xD ");
        }

    }

    private String prepareGooglePlacesURL() {
        StringBuilder builder = new StringBuilder();

        builder.append(this.url);
        builder.append(Constants.location+"=");
        builder.append(this.geoLocation[0]+","+this.geoLocation[1]+"&");
        builder.append("radius=500&type=restaurant&key=AIzaSyBj044oG-Pc4ZlSgKUbyv-JwFQAKLPiSXc");


        return builder.toString();

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
        this.atraction = intent.getStringExtra("atraction");
    }

    @Override
    public void onCameraChange (CameraPosition position){
        if (this.cameraPositioned)
        {
            LatLng tempPosition = this.mMap.getCameraPosition().target;
            double tempLatitude = tempPosition.latitude;
            double tempLongitude = tempPosition.longitude;
            if (Math.abs(Double.parseDouble(this.geoLocation[0]) - tempLatitude) > 0.015 || Math.abs(Double.parseDouble(this.geoLocation[1]) - tempLongitude) > 0.015  ){
                Log.d("CoCiekawego MapsActivity", "Mapa zmieniła znacząco pozycję");
                this.geoLocation[0] = String.valueOf(tempLatitude);
                this.geoLocation[1] = String.valueOf(tempLongitude);
                CallApi callApi = new CallApi(this);
                callApi.delegate = this;
                if (this.atraction.equals(Constants.restaurant)) {
                    String url = prepareGooglePlacesURL();
                    callApi.execute(url);
                }else {

                    callApi.execute(url, geoLocation[1], geoLocation[0], "1000");
                }
            }
        }else{
            Log.d("CoCiekawego MapsActivity", "Camera not positioned correctly");
        }

    }


    public void wrongDataDownloaded(){
        Toast.makeText(this, "Pobrano błędne dane. Prosze spróbować raz jeszcze.", Toast.LENGTH_LONG).show();
    }


    public void communicationProblem(){
        //this toast is displayed when something went wrong in communication with API
        Toast.makeText(this, "Problem z komunikacją z API. Proszę sprobować ponownie!", Toast.LENGTH_LONG).show();
    }
}
