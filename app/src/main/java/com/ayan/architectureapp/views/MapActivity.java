package com.ayan.architectureapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.ayan.architectureapp.R;
import com.ayan.architectureapp.models.restaurant.Restaurant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ArrayList<Restaurant> restaurants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setTitle("Map View");
        if (getIntent().getBundleExtra("BUNDLE").getParcelableArrayList(RestaurantListActivity.RESTAURANT_LIST) != null) {
            ArrayList<Restaurant> restaurantArrayList = getIntent().getBundleExtra("BUNDLE").getParcelableArrayList(RestaurantListActivity.RESTAURANT_LIST);
            restaurants.addAll(restaurantArrayList);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        for (Restaurant restaurant : restaurants) {
            LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            map.addMarker(new MarkerOptions().position(latLng)
                    .title(restaurant.getRestaurantName()));

        }

        if(restaurants.size()> 0){
            LatLng sp = new LatLng(restaurants.get(0).getLatitude(), restaurants.get(0).getLongitude());
            LatLng ep = new LatLng(restaurants.get(restaurants.size()-1).getLatitude(), restaurants.get(restaurants.size()-1).getLongitude());
            LatLngBounds bounds = new LatLngBounds(sp,ep);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 14));
        }



    }
}
