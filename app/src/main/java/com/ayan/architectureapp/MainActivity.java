package com.ayan.architectureapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    public static final int LOCATION_REQUEST_CODE = 101;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private TextView tvLat;
    private TextView tvLng;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null){
                Log.d("MainActivity", "Location: "+ locationResult.getLocations().get(locationResult.getLocations().size()-1).getLatitude()+":"+locationResult.getLocations().get(locationResult.getLocations().size()-1).getLongitude());
                tvLat.setText(String.valueOf(locationResult.getLocations().get(locationResult.getLocations().size()-1).getLatitude()));
                tvLng.setText(String.valueOf(locationResult.getLocations().get(locationResult.getLocations().size()-1).getLongitude()));
                if (mFusedLocationClient != null) {
                    mFusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }


        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLat = findViewById(R.id.tvLat);
        tvLng = findViewById(R.id.tvLng);

    }



    private void getLocation() {
        if (hasLocationPermission()) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);

            requestDeviceLocationAccess(this);

        } else {
            requestLocationPermission();
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void onLocationPermissionRequest(@NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // request location
            getLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestDeviceLocationAccess(final Context application) {
        LocationManager locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(application);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // On Gps enabled
            onLocationAvailable(locationRequest);

        } else {
            mSettingsClient
                    .checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener((Activity) application, new OnSuccessListener<LocationSettingsResponse>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                            //  GPS is already enable, callback GPS status through listener
                            onLocationAvailable(locationRequest);
                        }
                    })
                    .addOnFailureListener((Activity) application, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult((Activity) application, LOCATION_REQUEST_CODE);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.i("MainActivity", "PendingIntent unable to execute request.");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    String errorMessage = "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings.";
                                    Log.e("MainActivity", errorMessage);

                                    Toast.makeText((Activity) application, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void onLocationAvailable(final LocationRequest locationRequest) {
        //mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //Log.d("MainActivity", "Location2: "+ location.getLatitude()+":"+location.getLongitude());
                /*if(location != null){
                    tvLat.setText(String.valueOf(location.getLatitude()));
                    tvLng.setText(String.valueOf(location.getLongitude()));
                }else {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }*/
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                onLocationPermissionRequest(grantResults);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOCATION_REQUEST_CODE:
                    onLocationAvailable(locationRequest);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
        super.onDestroy();
    }

    public void getCurrentLocation(View view) {
        getLocation();
    }
}
