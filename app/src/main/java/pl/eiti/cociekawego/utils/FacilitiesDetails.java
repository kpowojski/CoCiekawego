package pl.eiti.cociekawego.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;

import pl.eiti.cociekawego.R;
import pl.eiti.cociekawego.callers.AsyncResponse;

/**
 * Created by krystian on 2016-06-12.
 */
public class FacilitiesDetails extends Activity {

    private Button share;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facilities_details_layout);

        Intent intent = getIntent();
        final String title = intent.getStringExtra(Constants.title);
        ((TextView) findViewById(R.id.facilities_details_title)).setText(title);


        final String snippet = intent.getStringExtra(Constants.snippet);
        ((TextView) findViewById(R.id.facilities_details_snippet)).setText(snippet);

        final String lat = intent.getStringExtra(Constants.latitude);
        final String lon = intent.getStringExtra(Constants.longitude);

        share = (Button) findViewById(R.id.facilities_details_share);
        ratingBar = (RatingBar) findViewById(R.id.facilities_details_rating_bar);

        share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String rating = new Float(ratingBar.getRating()).toString();
                String text = "Mój wybór: " + title + "\r\n" + snippet + "\r\n" + "Ocena: " + rating + "\r\nSprawdz na mapie: " + "http://maps.google.com/maps?q=loc:" + lat + "," + lon;
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Message");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(shareIntent, "Udostępnij"));
            }

        });


            String url = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lon + "&zoom=18&size=400x400&sensor=true";
            new DownloadImageTask((ImageView) findViewById(R.id.facilities_details_image)).execute(url);
        }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
