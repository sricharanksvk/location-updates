package com.svksricharan.fetchlocation

import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.svksricharan.fetchlocation.fetchLocation.DefaultLocationClient
import com.svksricharan.fetchlocation.fetchLocation.LocationClient
import com.svksricharan.fetchlocation.fetchLocation.LocationUpdateCallback
import com.svksricharan.fetchlocation.ui.theme.FetchLocationTheme
import kotlinx.coroutines.Job
import java.util.Locale

class MainActivity : ComponentActivity(), LocationUpdateCallback {
    private lateinit var resultLauncher: ActivityResultLauncher<IntentSenderRequest>
    internal lateinit var locationClient: LocationClient
    internal lateinit var locationJob: Job

    val viewModel: LocationViewModel by viewModels<LocationViewModel>()

    companion object {
        internal const val REQUEST_LOCATION_PERMISSION = 123
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                hasLocationPermission()
            } else {
                this@MainActivity.finish()
            }
        }
        locationClient = DefaultLocationClient(
            this@MainActivity,
            LocationServices.getFusedLocationProviderClient(this),
            this@MainActivity
        )
        setContent {
            FetchLocationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.onPrimary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GenericButton(stringResource(R.string.fetch_location_updates)) {
                                checkLocationPermissions()
                            }

                            GenericButton(stringResource(R.string.stop_location_updates)) {
                                removeLocationUpdates()
                            }
                        }
                    }
                }
            }
        }
        viewModel.mobileCurrentLocation.observe(this@MainActivity){
            showToast(it.longitude.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        updateLocation()
    }

    override fun onPause() {
        super.onPause()
        removeLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
    }

    override fun onLocationUpdateError(exception: Exception) {
        if (exception is ResolvableApiException) {
            try {
                val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                resultLauncher.launch(intentSenderRequest)
            } catch (exception: IntentSender.SendIntentException) {
                Log.e("TAG", "startGps: ${exception.message}")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermissions()
            } else {
                showToast(getString(R.string.please_allow_location_permission))
            }
        }
    }

    private fun removeLocationUpdates() {
        if (::locationJob.isInitialized) {
            locationJob.cancel()
        }
    }
}

@Composable
fun GenericButton(buttonText: String, action: () -> Unit) {
    Button(
        onClick = {
            action()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 9.dp, end = 9.dp, bottom = 13.dp)
            .background(
                MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(size = 5.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
        ),
        enabled = true
    ) {
        Text(
            text = buttonText.uppercase(Locale.ROOT),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FetchLocationTheme {
        GenericButton("Open DashBoard") {
        }
    }
}