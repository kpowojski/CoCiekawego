package pl.eiti.cociekawego.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import pl.eiti.cociekawego.R;

/**
 * Created by krystian on 2016-05-05.
 */
public class Helper {


    //method takes String array with preferences from resources
    public static String[] getPreferencesArray(Context context){
        TypedArray preferences_list = context.getResources().obtainTypedArray(R.array.preferencje);

        String[] values = new String[preferences_list.length()];
        for (int i=0;i<preferences_list.length();i++){
            values[i] = preferences_list.getString(i);
        }
        return values;
    }


}
