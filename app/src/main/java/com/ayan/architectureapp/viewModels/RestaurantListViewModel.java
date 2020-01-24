package com.ayan.architectureapp.viewModels;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ayan.architectureapp.models.location.CurrentLocation;
import com.ayan.architectureapp.repositories.LocationRepository;
import com.ayan.architectureapp.models.restaurant.Restaurant;
import com.ayan.architectureapp.repositories.RestaurantRepository;

import java.util.List;

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

    public LiveData<List<Restaurant>> getRestaurantList(Double lat, Double lng, Boolean onRefreshed){
        return restaurantRepository.getRestaurantList(lat,lng,onRefreshed);
    }


}
