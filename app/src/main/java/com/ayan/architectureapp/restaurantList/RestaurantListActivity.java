package com.ayan.architectureapp.restaurantList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ayan.architectureapp.R;
import com.ayan.architectureapp.adapters.RestaurantListAdapter;
import com.ayan.architectureapp.models.location.CurrentLocation;
import com.ayan.architectureapp.location.LocationHelper;
import com.ayan.architectureapp.models.restaurant.Restaurant;
import com.ayan.architectureapp.models.restaurant.RestaurantResponse;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    public static final String TAG = "RestaurantListActivity";
    private RestaurantListViewModel restaurantListViewModel;
    private RecyclerView rvRestaurantList;
    private SwipeRefreshLayout swipeToRefresh;
    private RestaurantListAdapter adapter = new RestaurantListAdapter();
    private Double latitude;
    private Double longitude;
    private String nextPageToken = "";
    private List<Restaurant> restaurantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restaurantListViewModel = ViewModelProviders.of(this).get(RestaurantListViewModel.class);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        rvRestaurantList = findViewById(R.id.rvRestaurantList);
        rvRestaurantList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        swipeToRefresh.setOnRefreshListener(this);
        rvRestaurantList.setAdapter(adapter);
        if (restaurantList.isEmpty())
            swipeToRefresh.setRefreshing(true);
        getLocation();
    }

    private void getLocation() {

        restaurantListViewModel.getCurrentLocation(this).observe(this, new Observer<CurrentLocation>() {
            @Override
            public void onChanged(CurrentLocation currentLocation) {
                latitude = currentLocation.getLatitude();
                longitude = currentLocation.getLongitude();
                getRestaurantList();


            }
        });
    }

    private void getRestaurantList() {
        restaurantListViewModel.getRestaurantList(latitude, longitude, nextPageToken).observe(this, new Observer<RestaurantResponse>() {
            @Override
            public void onChanged(RestaurantResponse restaurantResponse) {
                Log.d(TAG, "on restaurant list changed: " + restaurantResponse.getRestaurantList().size());
                swipeToRefresh.setRefreshing(false);
                nextPageToken = restaurantResponse.getNextPageToken();
                if (restaurantResponse.getRestaurantList().size() > 0)
                    restaurantList.addAll(restaurantResponse.getRestaurantList());
                adapter.setRestaurant(restaurantList);
            }
        });
    }


    private void onLocationPermissionRequest(@NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // request location
            getLocation();

        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LocationHelper.LOCATION_PERMISSION_REQUEST_CODE:
                onLocationPermissionRequest(grantResults);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LocationHelper.LOCATION_REQUEST_CODE:
                    restaurantListViewModel.onLocationAvailable();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {

        restaurantListViewModel.removeLocationUpdates();
        super.onDestroy();
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        restaurantList = new ArrayList<>();
        getLocation();
    }
}
