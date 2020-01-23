package com.ayan.architectureapp.restaurant;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.ayan.architectureapp.models.restaurant.Restaurant;
import com.ayan.architectureapp.models.restaurant.RestaurantResponse;
import com.ayan.architectureapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository {

    public static final String LOG ="RestaurantRepository";
    private Application application;
    private MutableLiveData<RestaurantResponse> restaurantResponseMutableLiveData;

    public RestaurantRepository(Application application) {
        this.application = application;
        restaurantResponseMutableLiveData = new MutableLiveData<>();
    }


    public MutableLiveData<RestaurantResponse> getRestaurantList(Double lat, Double lng, String nextPageToken) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?type=restaurant&key=");
        url.append(Constants.GOOGLE_API_KEY);
        url.append("&rankby=distance&location=");
        url.append(lat);
        url.append(",");
        url.append(lng);
        url.append("&next_page_token=");
        url.append(nextPageToken);

        ApiHelper.getInstance().get(String.valueOf(url), new ApiListener() {
            @Override
            public void onSuccess(String response) {
                Log.d(LOG, response);
                RestaurantResponse restaurantResponse = new RestaurantResponse();
                List<Restaurant> restaurantList = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String nextPageToken = jsonObject.optString("next_page_token", "");
                    JSONArray jsonArray = jsonObject.optJSONArray("results");
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Restaurant restaurant = new Restaurant();
                            JSONObject jsonObjectRestaurant = jsonArray.optJSONObject(i);
                            restaurant.setRestaurantName(jsonObjectRestaurant.optString("name"));
                            restaurant.setImage(jsonObjectRestaurant.optString("icon"));
                            JSONArray photosArr = jsonObjectRestaurant.optJSONArray("photos");
                            if (photosArr != null && photosArr.length() > 0) {
                                restaurant.setPhotoReference(photosArr.optJSONObject(0).optString("photo_reference"));
                            }
                            restaurant.setVicinity(jsonObjectRestaurant.optString("vicinity"));
                            restaurant.setLatitude(jsonObjectRestaurant.optJSONObject("geometry").optJSONObject("location").optDouble("lat"));
                            restaurant.setLongitude(jsonObjectRestaurant.optJSONObject("geometry").optJSONObject("location").optDouble("lng"));
                            restaurantList.add(restaurant);
                        }
                    }
                    restaurantResponse.setNextPageToken(nextPageToken);
                    restaurantResponse.setRestaurantList(restaurantList);
                    restaurantResponseMutableLiveData.setValue(restaurantResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String message) {
                Log.d(LOG, message);
                restaurantResponseMutableLiveData.setValue(new RestaurantResponse(message));
            }
        });

        return restaurantResponseMutableLiveData;
    }


}
