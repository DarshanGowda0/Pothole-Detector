package rohan.darshan.abhi.pothole;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class service extends Service implements SensorEventListener, LocationListener {
    private static final String SERVER_URL = "http://bitsmate.in/pothole/putloc.php?lat=12.365&long=983.25";
    private SensorManager sensorManager;
    double ax, ay, az;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;
    public static final String TAG = "DARSHANROHAN";
    Location location;
    double latitude;
    double longitude;
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean canGetLocation = false;
    Criteria criteria;
    boolean isNetworkEnabled;
    String bestProvider;
    Location loc;


    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.d("DARSHANROHAN", "SERVICE is created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DARSHANROHAN", "SERVICE is started");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        checkForPothole();

        return START_STICKY;
    }


    public Location getLocation() {
        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.d(TAG, " no network provider is enabled");
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            1, this);
                    Log.d(TAG, "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                1000,
                                1, this);
                        Log.d(TAG, "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

//}
//}


    private void checkForPothole() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DARSHANROHAN", "SERVICE is destroyed");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = Math.sqrt(ax * ax + ay * ay + az * az);
            double delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;


//            Log.d("Test"," "+ax+" "+ay+" "+az);
            int temp = compare((int) ax, (int) ay, (int) az);

            if (temp == 0) {
                //orientation x
//                Log.d("test","X orientation");
//                Log.d("test",""+(mAccelLast-mAccelCurrent));
                if ((mAccelLast - mAccelCurrent) > 7) {
//                    Toast.makeText(this, "pothole x", Toast.LENGTH_SHORT).show();
                    Log.d("DARSHANROHAN", "pothole x");
                    if (loc == null) {
                        loc = getLocation();
                    }
                    double latitude = loc.getLatitude();
                    double longitude = loc.getLongitude();
                    Log.d(TAG, "location : " + latitude + " " + longitude);
                    sendLocationData(latitude, longitude);
                }
            } else if (temp == 1) {
                //orientation y
//                Log.d("test","y orientation");
//                Log.d("test",""+(mAccelLast-mAccelCurrent));
                if ((mAccelLast - mAccelCurrent) > 7) {
//                    Toast.makeText(this, "pothole y", Toast.LENGTH_SHORT).show();
                    Log.d("DARSHANROHAN", "pothole y");
                    if (loc == null) {
                        loc = getLocation();
                    }
                    double latitude = loc.getLatitude();
                    double longitude = loc.getLongitude();
                    Log.d(TAG, "location : " + latitude + " " + longitude);
                    sendLocationData(latitude, longitude);

                }
            } else if (temp == 2) {
                //orientation z
//                Log.d("test","cur:"+mAccelCurrent+"      last:"+mAccelLast);
                if ((mAccelLast - mAccelCurrent) > 7) {
//                    Toast.makeText(this, "pothole z", Toast.LENGTH_SHORT).show();
//                    Log.d("test",""+(mAccelLast-mAccelCurrent));
                    Log.d("DARSHANROHAN", "pothole z");
                    if (loc == null) {
                        loc = getLocation();
                    }
                    double latitude = loc.getLatitude();
                    double longitude = loc.getLongitude();

                    Log.d(TAG, "location : " + latitude + " " + longitude);
                    sendLocationData(latitude, longitude);

                }
            }

        }

    }

    private int compare(int ax, int ay, int az) {
        ax = Math.abs(ax);
        ay = Math.abs(ay);
        az = Math.abs(az);
        if (ax > ay) {
            if (ax > az) return 0;
        } else if (ay > az) return 1;
        else return 2;

        return -1;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onlocationchange called");
        loc = location;
        Log.d(TAG, "" + location);
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.d("DARSHANROHAN", "lat:" + latitude + "" + "lng:" + longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void sendLocationData(double lat, double lon) {

        try {
            FileOutputStream fos = openFileOutput("abhi.darshan.rohan", MODE_APPEND);
            String lati = "" + lat;
            String longi = "" + lon;
            String data = "$" + lat + "&" + longi;
            fos.write(data.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        class darsh implements Runnable {
            double lat, lon;

            darsh(double lat, double lon) {
                this.lat = lat;
                this.lon = lon;
            }

            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://bitsmate.in/pothole/putloc.php?lat=" + lat + "&long=" + lon);
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity entity = response.getEntity();
                    String resp = EntityUtils.toString(entity);
                    Log.d(TAG, "" + resp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Thread thread = new Thread(new darsh(lat, lon));


        thread.start();

    }
}
