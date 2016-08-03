package com.tny.khondedriver;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Thitiphat on 4/17/2016.
 */
public class LocationLogService  extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener
{
    public static final String TAG = "KhondeeService";
    public static GoogleApiClient mGoogleApiClient;
    public static LocationRequest mLocationRequest;
    private int to_minute = 60000;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private int count_inaccurate = 0;

    int THE_INTERVAL = 1 *to_minute;
    int FASTEST_INTERVAL = 1 *to_minute;
    int RETRY_INTERVAL = 1 *to_minute;

    String province;
    static String tel="";
    String latlong = "0.0,0.0";
    int accuracy;
    Runnable myRunnable;
    Handler handler;
    Handler updateLocHandler;
    Runnable updateLocRunnable;

    public LocationLogService(){
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "Service onbind");
        return null;
    }
    @Override
    public void onDestroy(){
        Log.e(TAG, "Service onDestroy");
        mGoogleApiClient.disconnect();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
    @Override
    public void onCreate() {
        Log.e(TAG, "Service onCreate");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "Service onStartCommand");
        return START_STICKY;
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override //Start the location updates
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "API onConnected");

        handler = new Handler();

        mLocationRequest = new LocationRequest();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setInterval(THE_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e("CompareBestLocation", "Location: " + location.getLatitude() + "," + location.getLongitude() + " | accuracy: " + location.getAccuracy());
        SharedPreferences preferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
        SharedPreferences preferences2 = getSharedPreferences("LocLog",MODE_PRIVATE);

        tel = preferences.getString("tel","");

        if(!tel.equals("")) {
            latlong = location.getLatitude() + "," + location.getLongitude();
            getProvince();
        }
    }

    public void getProvince(){
        handler.removeCallbacks(myRunnable);
        SharedPreferences preferences2 = getSharedPreferences("LocLog",MODE_PRIVATE);

        if(tel.equals(""))
            return;

        Log.e("MapsTask", "Getting Province");

        new AsyncTask<String, Void, Boolean>() {
            String errorMsg = "";

            @Override
            protected void onPreExecute() {handler.removeCallbacks(myRunnable);}

            @Override
            protected Boolean doInBackground(String... params) {
                BufferedReader reader;
                StringBuilder buffer = new StringBuilder();
                String line;
                try {
                    URL u = new URL("http://maps.googleapis.com/maps/api/geocode/json?language=TH&latlng=" + latlong);
                    HttpURLConnection h = (HttpURLConnection)u.openConnection();
                    h.setRequestMethod("GET");
                    h.setDoOutput(true);

                    h.connect();

                    int response = h.getResponseCode();
                    if (response == 200) {
                        reader = new BufferedReader(new InputStreamReader(h.getInputStream(),"UTF-8"));
                        while((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }

                        //Start parsing JSON
                        JSONObject jMaps = new JSONObject(buffer.toString());
                        JSONArray jresults = jMaps.getJSONArray("results");
                        JSONObject childResults = jresults.getJSONObject(0);
                        JSONArray jComponents = childResults.getJSONArray("address_components");
                        for(int i=0;i<jComponents.length();i++){
                            JSONObject childComponents = jComponents.getJSONObject(i);
                            JSONArray jtypes = childComponents.getJSONArray("types");
                            String type = jtypes.getString(0);

                            if(type.equals("administrative_area_level_1")){
                                province = childComponents.getString("long_name");
                            }
                        }

                        Log.e("MapsTask", "Province: " + province);

                        updateLoc();
                        return true;
                    }
                    else {
                        errorMsg = "HTTP Error";
                    }
                } catch (MalformedURLException e) {
                    Log.e("WeatherTask", "URL Error");
                    errorMsg = "URL Error";
                } catch (IOException e) {
                    Log.e("MapsTask", "No internet connection, Retry soon");

                    myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            getProvince();
                        }
                    };
                    handler.postDelayed(myRunnable, RETRY_INTERVAL);
                } catch (JSONException e) {
                    Log.e("WeatherTask", "JSON Error");
                    errorMsg = "JSON Error";
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
            }
        }.execute();
    }


    public void updateLoc(){
        Log.e("UpdateLocTask", "Saving Location");
        new AsyncTask<String, Void, Boolean>() {
            String errorMsg = "";

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Boolean doInBackground(String... params) {
                BufferedReader reader;
                StringBuilder buffer = new StringBuilder();
                String line;
                try {
                    URL u = new URL(getString(R.string.website_url)+"/update_loc.php?latlong=" + latlong + "&tel=" + tel + "&province=" + URLEncoder.encode(province, "UTF-8"));
                    HttpURLConnection h = (HttpURLConnection)u.openConnection();
                    h.setRequestMethod("GET");
                    h.setDoOutput(true);
                    h.connect();

                    int response = h.getResponseCode();
                    if (response == 200) {
                        reader = new BufferedReader(new InputStreamReader(h.getInputStream(),"UTF-8"));
                        while((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }

                        Log.e("UpdateLocTask", "Return: " +buffer.toString());
                        return true;
                    }
                    else {
                        errorMsg = "HTTP Error";
                    }
                } catch (MalformedURLException e) {
                    Log.e("WeatherTask", "URL Error");
                    errorMsg = "URL Error";
                } catch (IOException e) {
                    Log.e("UpdateLocTask", "No internet connection, Retry soon");
                    errorMsg = "I/O Error";

                    myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            updateLoc();
                        }
                    };
                    handler.postDelayed(myRunnable, RETRY_INTERVAL);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {

            }
        }.execute();
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2){
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        dist = dist*1000;
        return dist;
    }

    public double deg2rad(double deg){
        return deg * (Math.PI/180);
    }

    public double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

}
