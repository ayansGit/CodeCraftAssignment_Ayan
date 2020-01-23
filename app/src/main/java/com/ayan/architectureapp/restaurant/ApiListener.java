package com.ayan.architectureapp.restaurant;

public interface ApiListener {

    void onSuccess(String response);
    void onFailure(String message);
}
