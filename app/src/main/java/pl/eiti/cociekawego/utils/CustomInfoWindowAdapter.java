package pl.eiti.cociekawego.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by krystian on 2016-05-15.
 */
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context mContext;

    public CustomInfoWindowAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        LinearLayout info = new LinearLayout(mContext);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(mContext);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());

        TextView snippet = new TextView(mContext);
        snippet.setTextColor(Color.GRAY);
        snippet.setText(marker.getSnippet());

        info.addView(title);
        info.addView(snippet);

        return info;
    }
}
