package pl.eiti.cociekawego;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.eiti.cociekawego.utils.Constants;
import pl.eiti.cociekawego.utils.Helper;

/**
 * Created by krystian on 2016-05-04.
 */
public class PreferenceActivity extends Activity {

    private ListView listView;


    private String[] values; //preference values taken from resource array

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preference_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Constants.PREFERENCES);

        listView = (ListView) findViewById(R.id.preference_listview);
        values = Helper.getPreferencesArray(this);


        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i< values.length; i++){
            list.add(values[i]);
        }

        final StableArrayAdapter  adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, list);
        listView.setAdapter(adapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    protected void onResume (){

        super.onResume();
        SharedPreferences settings = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        for (int i = 0; i<values.length;i++){
            String val = settings.getString(values[i], "null");
            if (val.equals("true")){
                listView.setItemChecked(i, true);
            }
        }


    }


    protected void onPause (){
        super.onPause();
        //go throught all values array to check which elements has been checked by user and save
        // this choice do SharedPreferences
        SharedPreferences settings = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        //clear all previous choices
        editor.clear();
        for (int i=0; i< values.length; i++){
            if(listView.isItemChecked(i)){
                editor.putString(values[i], "true");
            }else{
                editor.putString(values[i], "false");
            }
        }
        editor.commit();

    }

    private class StableArrayAdapter extends ArrayAdapter<String>{
        HashMap<String, Integer> mIdMap = new HashMap<>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects){
            super(context, textViewResourceId, objects);
            for (int i=0; i< objects.size(); i++){
                mIdMap.put(objects.get(i),i);
            }
        }

        @Override
        public long getItemId(int position){
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds(){
            return true;
        }


    }
}
