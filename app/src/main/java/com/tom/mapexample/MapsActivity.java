package com.tom.mapexample;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    LocationRequest locationRequest;
    GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_LOCATION = 2;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        } else{
            setMyLocation();
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng taipei101 = new LatLng(25.033408,121.564099);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(taipei101, 15));
        Marker marker101 = mMap.addMarker(new MarkerOptions()
                .position(taipei101)
                .title("101")
                .snippet("這裡是台北101")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bobble))
        );

        marker101.showInfoWindow();

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

/*
                View view = getLayoutInflater().inflate(R.layout.info_window, null);
                TextView title = (TextView) view.findViewById(R.id.info_title);
                TextView snippet = (TextView) view.findViewById(R.id.info_snippet);
                title.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());
                return view;
*/
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.info_window, null);
                TextView title = (TextView) view.findViewById(R.id.info_title);
                TextView snippet = (TextView) view.findViewById(R.id.info_snippet);
                title.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());
                return view;
            }
        });


   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //noinspection MissingPermission
                    setMyLocation();
                }else{

                }
                break;
        }

    }

    private void setMyLocation() {
        //noinspection MissingPermission
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                String provider = locationManager.getBestProvider(criteria, true);
                //noinspection MissingPermission
                Location location = locationManager.getLastKnownLocation(provider);

                if (location != null){
                    Log.i("LOCATION", location.getLatitude() + "/" + location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),15));

                }

                return false;
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

/*
        //noinspection MissingPermission
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, locationRequest, this);
*/
        //noinspection MissingPermission
        Location location = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (location != null){
            Log.i("LOCATION", location.getLatitude() + "/" + location.getLongitude());
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(),
                                        location.getLongitude()),
                            15));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            Log.d("LOCATION", location.getLatitude() + "," + location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
        }

    }
}

