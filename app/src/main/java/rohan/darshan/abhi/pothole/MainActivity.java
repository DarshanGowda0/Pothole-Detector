package rohan.darshan.abhi.pothole;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private GoogleMap map;
    RelativeLayout loading;
    //    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    //12.905320, 77.562684

    ArrayList<String> latitude = new ArrayList<>();
    ArrayList<String> longitude = new ArrayList<>();

    ArrayList<Double> latVal = new ArrayList<>();
    ArrayList<Double> lonVal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loading = (RelativeLayout) findViewById(R.id.loading);
        loadTask initial = new loadTask();
        initial.execute();

        floatingButton();

    }

    private void floatingButton() {

        Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);

        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.refresh);

        final FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

//        actionButton.setBackgroundDrawable(drawable);

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
// repeat many times:
        Drawable uploadDrawable = getResources().getDrawable(R.drawable.refresh);
        ImageView itemUploadIcon = new ImageView(this);
//        itemUploadIcon.setImageDrawable(uploadDrawable);

        SubActionButton UploadButton = itemBuilder.setContentView(itemUploadIcon).build();
        UploadButton.setBackgroundDrawable(uploadDrawable);


//        button1.setBackground();

        Drawable searchDrawable = getResources().getDrawable(R.drawable.refresh);
        ImageView itemSearchIcon = new ImageView(this);
//        itemSearchIcon.setImageDrawable(searchDrawable);
        SubActionButton searchButton = itemBuilder.setContentView(itemSearchIcon).build();
        searchButton.setBackgroundDrawable(searchDrawable);


        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(UploadButton)
                .addSubActionView(searchButton)
                .attachTo(actionButton)

                .build();


    }

    private void addMap() {
        ArrayList<Marker> markers = new ArrayList<>(latVal.size());
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        for (int k = 0; k < latVal.size(); k++) {

//            LatLng customMarkerLoc = new LatLng(latVal.get(0), lonVal.get(0));//12.978301, 77.571945
           /* Marker any = map.addMarker(new MarkerOptions()
                            .position(customMarkerLoc)
            );*/

            map.addMarker(new MarkerOptions().position(new LatLng(latVal.get(k), lonVal.get(k))));


        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.978301, 77.571945), 14.0f));

        // Move the camera instantly to hamburg with a zoom of 15.
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(customMarkerLoc, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }


    public class loadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://bitsmate.in/pothole/getloc.php");
            try {
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                Log.d(service.TAG, data);

                try {
                    JSONObject object = new JSONObject(data);
                    for (int i = 0; i < object.length(); i++) {
                        JSONObject locObject = object.getJSONObject("" + i);
                        String lon = locObject.getString("long");
                        String lat = locObject.getString("lat");
                        latitude.add(lat);
                        longitude.add(lon);
                        Log.d(service.TAG, "" + locObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (int j = 0; j < latitude.size(); j++) {
                latVal.add(Double.parseDouble(latitude.get(j)));
                lonVal.add(Double.parseDouble(longitude.get(j)));
            }

            Log.d(service.TAG, "" + lonVal);
            loading.setVisibility(View.GONE);
            addMap();
        }
    }
}
