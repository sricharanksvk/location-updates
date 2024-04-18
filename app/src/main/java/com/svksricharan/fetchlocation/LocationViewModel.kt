package com.svksricharan.fetchlocation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class LocationViewModel : ViewModel() {

    var mobileCurrentLocation =  MutableLiveData<LatLng>()
}