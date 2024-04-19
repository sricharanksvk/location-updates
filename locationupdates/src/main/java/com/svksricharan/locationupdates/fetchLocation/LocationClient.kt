package com.svksricharan.locationupdates.fetchLocation

import android.location.Location
import kotlinx.coroutines.flow.Flow

fun interface LocationClient {
    fun getLocationUpdates(interval: Long): Flow<Location>
}