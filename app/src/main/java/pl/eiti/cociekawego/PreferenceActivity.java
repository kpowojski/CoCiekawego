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
        toolbar.setTitle(R.string.preferences);

        listView = (ListView) findViewById(R.id.preference_listview);

        TypedArray preferences_list = getResources().obtainTypedArray(R.array.preferencje);

        values = new String[preferences_list.length()];
        for (int i=0;i<preferences_list.length();i++){
            values[i] = preferences_list.getString(i);
        }

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i< values.length; i++){
            list.add(values[i]);
        }

        final StableArrayAdapter  adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


    }

    protected void onPause (){
        super.onPause();
        Log.i("CoCiekawego", "onPause Preference Activity");
        long[] positons = listView.getCheckedItemIds();

        Log.i("CoCiekawego", "Pozycja 1: " + positons[0]);
        SharedPreferences settings = getSharedPreferences("UserPreferences", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear(); //clear all preferences and add all new
        for (int i =0; i< positons.length; i++){
            int pos = (int) positons[i];

            editor.putString(values[pos], "true");
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
