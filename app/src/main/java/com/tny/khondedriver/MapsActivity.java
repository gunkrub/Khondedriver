package com.tny.khondedriver;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        ArrayList<String> gps_coor = intent.getStringArrayListExtra("gps_coor");
        ArrayList<String> address = intent.getStringArrayListExtra("address");
        ArrayList<String> datetime = intent.getStringArrayListExtra("datetime");
        String focus_coor = intent.getStringExtra("focus_coor");

        String[] latlong0;
        LatLng point =null;
        LatLng centerFocus = new LatLng(13.2472542, 101.483119);

        for(int i=gps_coor.size()-1; i>=1;i--){
            LatLng old_point = point;

            latlong0 = gps_coor.get(i).split(",");
            point = new LatLng(Double.parseDouble(latlong0[0]), Double.parseDouble(latlong0[1]));

            if(old_point!=null){
                mMap.addPolyline(new PolylineOptions()
                        .add(old_point, point)
                        .width(4)
                        .color(Color.RED));
            }

            Marker current_marker = mMap.addMarker(new MarkerOptions()
                    .position(point)
                    .title(datetime.get(i))
                    .snippet(address.get(i))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_icon_small)));

            if(gps_coor.get(i).equals(focus_coor)) {
                current_marker.showInfoWindow();
                centerFocus = point;
            }

        }

        LatLng old_point = point;

        latlong0 = gps_coor.get(0).split(",");
        point = new LatLng(Double.parseDouble(latlong0[0]),Double.parseDouble(latlong0[1]));

        Marker current_marker = mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(datetime.get(0))
                .snippet(address.get(0))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_small)));

        if(gps_coor.get(0).equals(focus_coor)) {
            current_marker.showInfoWindow();
            centerFocus = point;
        }

        mMap.addPolyline(new PolylineOptions()
                .add(old_point, point)
                .width(4)
                .color(Color.RED));


        mMap.setMyLocationEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(centerFocus));
        mMap.moveCamera(CameraUpdateFactory.zoomTo((float) 5.8));
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }
}
