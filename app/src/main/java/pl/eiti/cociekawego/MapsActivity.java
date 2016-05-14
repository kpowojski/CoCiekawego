package pl.eiti.cociekawego;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONObject;

import pl.eiti.cociekawego.callers.AsyncResponse;
import pl.eiti.cociekawego.callers.CallApi;

/**
 * Created by krystian on 2016-05-04.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AsyncResponse {

    private GoogleMap mMap;

    private CallApi call;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CallApi callApi = new CallApi(this);
        callApi.delegate = this;
        callApi.execute();

    }


    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-34,151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Sydneeey"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));



    }


    public JSONObject processFinish(JSONObject result){

        return result;
    }
}
