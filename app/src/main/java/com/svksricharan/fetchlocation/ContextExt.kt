package com.svksricharan.fetchlocation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.coroutineScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun requestLocationPermission(activity: Activity, requestCode: Int) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ),
        requestCode
    )
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

fun Context.isLocationEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun MainActivity.updateLocation() {
    if (hasLocationPermission()) {
        locationJob = locationClient
            .getLocationUpdates(10000)
            .catch { e ->
                e.printStackTrace()
            }
            .onEach { location ->
                /*
                * Get location update here
                * */
                viewModel.mobileCurrentLocation.value =
                    LatLng(location.latitude, location.longitude)
            }.launchIn(lifecycle.coroutineScope)

    }
}

fun MainActivity.checkLocationPermissions() {
    if (hasLocationPermission()) {
        updateLocation()
    }

    if (!hasLocationPermission()) {
        requestLocationPermission(
            this@checkLocationPermissions,
            MainActivity.REQUEST_LOCATION_PERMISSION
        )
    }
}

fun Activity.showToast(message: String) {
    Toast.makeText(this, message.trim(), Toast.LENGTH_SHORT).show()
}