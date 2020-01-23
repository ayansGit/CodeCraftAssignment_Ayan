package com.ayan.architectureapp.models.restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantResponse {

   private List<Restaurant> restaurantList = new ArrayList<>();
   private String nextPageToken = "";
   private String message = "";

    public RestaurantResponse(String message) {
        this.message = message;
    }

    public RestaurantResponse() {
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
