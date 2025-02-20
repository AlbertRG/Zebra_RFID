package com.example.volkswagendemo.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.volkswagendemo.data.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import javax.inject.Inject

class LocationUtils @Inject constructor(private val context: Context) {

    private val _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetwork != null
    }

    @SuppressLint("MissingPermission")
    fun requestLocation(): Flow<Pair<LocationData, String>> = callbackFlow {
        val timeoutHandler = Handler(Looper.getMainLooper())
        val timeoutRunnable = Runnable {
            Log.e("LocationUtils", "⚠️ Timeout: No location received")
            trySend(Pair(LocationData(0.0, 0.0), "Tiempo agotado: Localización no recibida"))
            close()
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    val locationData = LocationData(it.latitude, it.longitude)
                    runCatching {
                        trySend(
                            Pair(locationData, "")
                        )
                        Log.d(
                            "LocationUtils",
                            "✅ Location received: Lat=${it.latitude}, Lng=${it.longitude}"
                        )
                    }.onFailure { e ->
                        Log.e("LocationUtils", "⚠️ Error sending location: ${e.message}")
                        trySend(Pair(LocationData(0.0, 0.0), "Error sending location"))
                    }
                    timeoutHandler.removeCallbacks(timeoutRunnable)
                    _fusedLocationClient.removeLocationUpdates(this)
                    close()
                }
            }
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).setMaxUpdates(1)
            .build()

        runCatching {
            _fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            timeoutHandler.postDelayed(timeoutRunnable, 10000)
        }.onFailure { e ->
            Log.e("LocationUtils", "⚠️ Error requesting location updates: ${e.message}")
            trySend(Pair(LocationData(0.0, 0.0), "Error requesting location updates"))
            close(e)
        }

        awaitClose {
            _fusedLocationClient.removeLocationUpdates(locationCallback)
            timeoutHandler.removeCallbacks(timeoutRunnable)
        }
    }

    fun reverseGeocodeLocation(location: LocationData, callback: (String) -> Unit) {
        if (location.latitude == 0.0 && location.longitude == 0.0) {
            val errorMsg = "⚠️ Invalid location: lat=0.0, lng=0.0"
            Log.e("LocationUtils", errorMsg)
            callback(errorMsg)
            return
        }

        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    val address =
                        addresses.firstOrNull()?.getAddressLine(0) ?: "Dirección no encontrada"
                    Log.d("LocationUtils", "✅ Successful geocoding: $address")
                    callback(address)
                }

                override fun onError(errorMessage: String?) {
                    val errorMsg = "⚠️ Error getting the address: $errorMessage"
                    Log.e("LocationUtils", errorMsg)
                    callback(errorMsg)
                }
            })
    }

}