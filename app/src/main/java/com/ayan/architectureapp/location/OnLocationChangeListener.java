package com.ayan.architectureapp.location;

import com.ayan.architectureapp.models.location.CurrentLocation;

public interface OnLocationChangeListener {
    void onLocationChange(CurrentLocation currentLocation);
}
