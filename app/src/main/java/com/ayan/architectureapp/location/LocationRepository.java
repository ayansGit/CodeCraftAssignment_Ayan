package com.ayan.architectureapp.location;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.ayan.architectureapp.models.location.CurrentLocation;

public class LocationRepository  {

    private MutableLiveData<CurrentLocation> currentLocationMutableLiveData ;
    private Application application;

    public LocationRepository(Application application) {
        this.application = application;
        currentLocationMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<CurrentLocation> getCurrentLocation(Context context){
        LocationHelper.getInstance(application.getApplicationContext()).getLocation(context,new OnLocationChangeListener() {
            @Override
            public void onLocationChange(CurrentLocation currentLocation) {
                currentLocationMutableLiveData.setValue(currentLocation);
            }
        });
        return currentLocationMutableLiveData;
    }

    public void removeLocationUpdates(){
        LocationHelper.getInstance(application.getApplicationContext()).removeLocationUpdates();
    }

    public void onLocationAvailable(){
        LocationHelper.getInstance(application.getApplicationContext()).onLocationAvailable();
    }


}
