package com.ayan.architectureapp.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.ayan.architectureapp.models.location.CurrentLocation;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
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

public class LocationHelper {

    private static final String TAG = "LocationHelper";
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    public static final int LOCATION_REQUEST_CODE = 101;
    private Context context;
    private OnLocationChangeListener onLocationChangeListener;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private static LocationHelper instance;

    private LocationHelper(Context context) {
        this.context = context;
    }

    public LocationHelper() {
    }

    public static synchronized LocationHelper getInstance(Context context){
        if(instance == null){
            instance = new LocationHelper();
        }
        return instance;
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null){
                Log.d(TAG, "Location: "+ locationResult.getLocations().get(locationResult.getLocations().size()-1).getLatitude()+":"+locationResult.getLocations().get(locationResult.getLocations().size()-1).getLongitude());
                Log.d(TAG,String.valueOf(locationResult.getLocations().get(locationResult.getLocations().size()-1).getLatitude()));
                Log.d(TAG,String.valueOf(locationResult.getLocations().get(locationResult.getLocations().size()-1).getLongitude()));

                onLocationChangeListener.onLocationChange(new CurrentLocation(locationResult.getLocations().get(locationResult.getLocations().size()-1).getLatitude(),
                        locationResult.getLocations().get(locationResult.getLocations().size()-1).getLongitude()));

                if (mFusedLocationClient != null) {
                    mFusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }

        }

    };



    public void getLocation(Context context, OnLocationChangeListener locationChangeListener) {
        this.context = context;
        this.onLocationChangeListener = locationChangeListener;
        if (hasLocationPermission()) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);

            requestDeviceLocationAccess();

        } else {
            requestLocationPermission();
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }


    private void requestDeviceLocationAccess() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(context);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        builder.setAlwaysShow(true);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // On Gps enabled
            onLocationAvailable();

        } else {
            mSettingsClient
                    .checkLocationSettings(mLocationSettingsRequest)
                    .addOnSuccessListener((Activity) context, new OnSuccessListener<LocationSettingsResponse>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                            //  GPS is already enable, callback GPS status through listener
                            onLocationAvailable();
                        }
                    })
                    .addOnFailureListener((Activity) context, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult((Activity) context, LOCATION_REQUEST_CODE);
                                    } catch (IntentSender.SendIntentException sie) {
                                        Log.i("RestaurantListActivity", "PendingIntent unable to execute request.");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    String errorMessage = "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings.";
                                    Log.e("RestaurantListActivity", errorMessage);

                                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void onLocationAvailable() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        });
    }

    public void removeLocationUpdates(){
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
