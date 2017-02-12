package com.kfouri.sismos;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Iterator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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

        Bundle b = getIntent().getExtras();
        Double xLat = b.getDouble("xLat");
        Double xLon = b.getDouble("xLon");
        String xFecha = b.getString("xFecha");
        String xMagnitud = b.getString("xMag");
        String xCiudad = b.getString("xCiudad");

        // Add a marker in Sydney and move the camera
        LatLng Point = new LatLng(xLat, xLon);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.esfera);

        Iterator iter = Principal.ListaSismos.iterator();
        while (iter.hasNext())
        {
            Sismo s = (Sismo)iter.next();

            LatLng punto = new LatLng(Double.parseDouble(s.getLat().replace("Lat: ","")), Double.parseDouble(s.getLon().replace("Lon: ", "")));

            mMap.addMarker(new MarkerOptions().position(punto)
                    .title(s.getCiudad()+" Mag("+s.getMag()+")")
                    .snippet(s.getFecha())
                    .icon(icon)
                    .anchor(0.5f, 0.5f)
            );

            if (Float.parseFloat(s.getMag())<4)
            {
                /*
                mMap.addCircle(new CircleOptions()
                                .center(punto)
                                .radius(2000)
                                .strokeColor(Color.parseColor("#4CAF50"))
                                .fillColor(Color.parseColor("#4CAF50"))
                );
               */
                mMap.addCircle(new CircleOptions()
                                .center(punto)
                                .radius(100000)
                                .strokeColor(Color.parseColor("#4CAF50"))
                                .fillColor(0x550000FF)
                );

            }
            else if (Float.parseFloat(s.getMag())>=4 && Float.parseFloat(s.getMag())<=6)
            {
/*
                mMap.addCircle(new CircleOptions()
                                .center(punto)
                                .radius(2000)
                                .strokeColor(Color.parseColor("#FBC02D"))
                                .fillColor(Color.parseColor("#FBC02D"))
                );
*/
                mMap.addCircle(new CircleOptions()
                                .center(punto)
                                .radius(300000)
                                .strokeColor(Color.parseColor("#FBC02D"))
                                .fillColor(0x550000FF)
                );

            }
            else
            {
/*
                mMap.addCircle(new CircleOptions()
                                .center(punto)
                                .radius(2000)
                                .strokeColor(Color.parseColor("#DD2C00"))
                                .fillColor(Color.parseColor("#DD2C00"))
                );
*/
                mMap.addCircle(new CircleOptions()
                                .center(punto)
                                .radius(500000)
                                .strokeColor(Color.parseColor("#DD2C00"))
                                .fillColor(0x550000FF)
                );

            }
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Point, 4));

    }


}
