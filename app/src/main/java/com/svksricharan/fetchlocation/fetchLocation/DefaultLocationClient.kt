package com.svksricharan.fetchlocation.fetchLocation

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient,
    private val callback: LocationUpdateCallback,
) : LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            val request =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
                    .build()

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(request)

            val task =
                LocationServices.getSettingsClient(context).checkLocationSettings(builder.build())
            var locationCallback: LocationCallback? = null
            task.addOnSuccessListener {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        super.onLocationResult(result)
                        result.locations.lastOrNull()?.let { location ->
                            launch { send(location) }
                        }
                    }
                }
                client.lastLocation.addOnSuccessListener {
                    launch { send(it) }
                }

                client.requestLocationUpdates(
                    request,
                    locationCallback!!,
                    Looper.getMainLooper()
                )
            }

            task.addOnFailureListener {
                callback.onLocationUpdateError(it)
            }

            awaitClose {
                locationCallback?.let { client.removeLocationUpdates(it) }
            }

        }
    }
}

fun interface LocationUpdateCallback {
    fun onLocationUpdateError(exception: Exception)
}
