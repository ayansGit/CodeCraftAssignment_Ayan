package com.ayan.architectureapp.views;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ayan.architectureapp.R;
import com.ayan.architectureapp.adapters.RestaurantListAdapter;
import com.ayan.architectureapp.models.location.CurrentLocation;
import com.ayan.architectureapp.location.LocationHelper;
import com.ayan.architectureapp.models.restaurant.Restaurant;
import com.ayan.architectureapp.utils.Constants;
import com.ayan.architectureapp.viewModels.RestaurantListViewModel;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        RestaurantListAdapter.OnRestaurantItemClickListener {


    public static final String TAG = "RestaurantListActivity";
    public static final String RESTAURANT_LIST = "restaurantList";
    private RestaurantListViewModel restaurantListViewModel;
    private RecyclerView rvRestaurantList;
    private SwipeRefreshLayout swipeToRefresh;
    private RestaurantListAdapter adapter;
    private Double latitude;
    private Double longitude;
    private Boolean onRefreshed = true;
    private String nextPageToken = "";
    private ArrayList<Restaurant> restaurantList;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Near by restaurants");
        restaurantListViewModel = ViewModelProviders.of(this).get(RestaurantListViewModel.class);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        rvRestaurantList = findViewById(R.id.rvRestaurantList);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvRestaurantList.setLayoutManager(linearLayoutManager);
        swipeToRefresh.setOnRefreshListener(this);
        restaurantList = new ArrayList<>();
        adapter = new RestaurantListAdapter(this);
        rvRestaurantList.setAdapter(adapter);
        rvRestaurantList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 19) {
                    getRestaurantList();
                }
            }
        });

        if (restaurantList.isEmpty())
            swipeToRefresh.setRefreshing(true);
        getLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.map:
                if(this.restaurantList.size()>0){
                    Intent intent = new Intent(this,MapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(RESTAURANT_LIST, this.restaurantList);
                    intent.putExtra("BUNDLE",bundle);
                    startActivity(intent);
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLocation() {

        restaurantListViewModel.getCurrentLocation(this).observe(this, new Observer<CurrentLocation>() {
            @Override
            public void onChanged(CurrentLocation currentLocation) {
                latitude = currentLocation.getLatitude();
                longitude = currentLocation.getLongitude();
                onRefreshed = true;
                Log.d(TAG,"Location changed get called: "+currentLocation.getLatitude()+":"+currentLocation.getLongitude());
                getRestaurantList();


            }
        });
    }

    private void getRestaurantList() {
        restaurantListViewModel.getRestaurantList(latitude, longitude, onRefreshed).observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurantList) {
                Log.d(TAG, "on restaurant list changed: " + restaurantList.size());
                swipeToRefresh.setRefreshing(false);
                if (restaurantList.size() > 0){
                    onRefreshed = false;
                    RestaurantListActivity.this.restaurantList.addAll(restaurantList);
                }
                adapter.setRestaurant(RestaurantListActivity.this.restaurantList);
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
        onRefreshed = true;
        restaurantList = new ArrayList<>();
        getLocation();
    }

    @Override
    public void setOnRestaurantItemClickListener(Restaurant restaurant) {

        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=");
        url.append(restaurant.getPhotoReference());
        url.append("&key=");
        url.append(Constants.GOOGLE_API_KEY);

        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra("URL",String.valueOf(url));
        startActivity(intent);
    }
}
