package com.ayan.architectureapp.api;

public interface ApiListener {

    void onSuccess(String response);
    void onFailure(String message);
}
