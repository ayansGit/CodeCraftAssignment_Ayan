package com.ayan.architectureapp.repositories;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.ayan.architectureapp.location.LocationHelper;
import com.ayan.architectureapp.location.OnLocationChangeListener;
import com.ayan.architectureapp.models.location.CurrentLocation;

public class LocationRepository  {

    private MutableLiveData<CurrentLocation> currentLocationMutableLiveData ;
    private Application application;

    public LocationRepository(Application application) {
        this.application = application;
    }

    public MutableLiveData<CurrentLocation> getCurrentLocation(Context context){
        currentLocationMutableLiveData = new MutableLiveData<>();
        LocationHelper.getInstance(application.getApplicationContext()).getLocation(context,new OnLocationChangeListener() {
            @Override
            public void onLocationChange(CurrentLocation currentLocation) {
                currentLocationMutableLiveData.setValue(currentLocation);
            }
        });
        Log.d("LocationRepo", "Location changed");
        return currentLocationMutableLiveData;
    }

    public void removeLocationUpdates(){
        LocationHelper.getInstance(application.getApplicationContext()).removeLocationUpdates();
    }

    public void onLocationAvailable(){
        LocationHelper.getInstance(application.getApplicationContext()).onLocationAvailable();
    }


}
