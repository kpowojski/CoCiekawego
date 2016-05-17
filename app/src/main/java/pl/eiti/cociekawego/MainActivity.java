package pl.eiti.cociekawego;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import pl.eiti.cociekawego.callers.AsyncResponse;
import pl.eiti.cociekawego.callers.CallApi;
import pl.eiti.cociekawego.utils.Constants;
import pl.eiti.cociekawego.utils.Helper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    TextView text;
    Toolbar toolbar;
    NavigationView navigationView;
    HashMap<String, Object> navDrawerElements;


    //for test
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navDrawerElements = new HashMap<>();
        mapElements();

        text = (TextView) findViewById(R.id.my_text);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences settings = getSharedPreferences("UserPreferences", 0);
        String tmp = settings.getString("Rozrywka", null);
        if (tmp == null){
            //application has never been run, so SharedPreferences are empty
            //in any other situation user preferences will be avaliable

        }else{
            Log.i("CoCiekawego MainActivity", "Other run");
        }

    }



    @Override
    protected void onResume(){
        super.onResume();
        String[] pref = Helper.getPreferencesArray(this);
        StringBuilder builder = new StringBuilder();
        SharedPreferences settings = getSharedPreferences("UserPreferences", 0);
        for (int i = 0;i<pref.length;i++){
            String value = settings.getString(pref[i], "null");
            if (value.equals("true")){
                navigationView.getMenu().findItem((Integer)navDrawerElements.get(pref[i])).setVisible(true);
            }else{
                navigationView.getMenu().findItem((Integer)navDrawerElements.get(pref[i])).setVisible(false);
            }
            builder.append(pref[i] + " : " + value + "\r\n");
        }
        text.setText(builder.toString());
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_public_transport) {

        } else if (id == R.id.nav_parking) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra(Constants.url, "https://api.bihapi.pl/wfs/warszawa/parkAndRide?circle=");
            intent.putExtra(Constants.atraction, Constants.parkAndRide);
            startActivityForResult(intent,1);

        } else if (id == R.id.nav_sport_facilities) {

        } else if (id == R.id.nav_veturilo) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra(Constants.url, "https://api.bihapi.pl/wfs/warszawa/veturilo?circle=");
            intent.putExtra(Constants.atraction, Constants.veturilo);
            startActivityForResult(intent,1);

        } else if (id == R.id.nav_entertainment) {

        } else if (id == R.id.nav_preferences){
            Intent intent = new Intent(this, PreferenceActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void mapElements() {
        //it's not elegant but we are lazy students
        navDrawerElements.put("Transport publiczny", R.id.nav_public_transport);
        navDrawerElements.put("Parking P+R", R.id.nav_parking);
        navDrawerElements.put("Obiekty sportowe", R.id.nav_sport_facilities);
        navDrawerElements.put("Stacje Veturilo", R.id.nav_veturilo);
        navDrawerElements.put("Rozrywka", R.id.nav_entertainment);
    }


}
