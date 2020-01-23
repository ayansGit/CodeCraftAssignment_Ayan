package com.ayan.architectureapp.restaurantList;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ayan.architectureapp.models.location.CurrentLocation;
import com.ayan.architectureapp.location.LocationRepository;
import com.ayan.architectureapp.models.restaurant.RestaurantResponse;
import com.ayan.architectureapp.restaurant.RestaurantRepository;

public class RestaurantListViewModel extends AndroidViewModel {

    private LocationRepository locationRepository;
    private RestaurantRepository restaurantRepository;

    public RestaurantListViewModel(@NonNull Application application) {
        super(application);
        locationRepository = new LocationRepository(application);
        restaurantRepository = new RestaurantRepository(application);
    }

    public LiveData<CurrentLocation> getCurrentLocation(Context context) {
        return locationRepository.getCurrentLocation(context);
    }

    public void removeLocationUpdates(){
        locationRepository.removeLocationUpdates();
    }

    public void onLocationAvailable(){
        locationRepository.onLocationAvailable();
    }

    public LiveData<RestaurantResponse> getRestaurantList(Double lat, Double lng, String nextPageToken){
        return restaurantRepository.getRestaurantList(lat,lng,nextPageToken);
    }


}
