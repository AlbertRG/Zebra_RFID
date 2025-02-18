package com.example.volkswagendemo.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
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

    @SuppressLint("MissingPermission")
    fun getLocationOnce(): Flow<LocationData> = callbackFlow {
        runCatching {
            _fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val locationData = LocationData(latitude = it.latitude, longitude = it.longitude)
                    trySend(locationData).isSuccess
                    Log.d("LocationUtils", "✅ Location received: Lat=${it.latitude}, Lng=${it.longitude}")
                } ?: run {
                    close(Exception("Location is null"))
                    Log.e("LocationUtils", "⚠️ Location is null")
                }
            }
        }.onFailure { e ->
            Log.e("LocationUtils", "⚠️ Error requesting location: ${e.message}")
            close(e)
        }

        awaitClose {}
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(): Flow<LocationData> = callbackFlow {

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    val locationData = LocationData(latitude = it.latitude, longitude = it.longitude)
                    runCatching {
                        trySend(locationData).isSuccess
                        Log.d("LocationUtils", "✅ Location received: Lat=${it.latitude}, Lng=${it.longitude}")
                    }.onFailure { e ->
                        Log.e("LocationUtils", "⚠️ Error sending location: ${e.message}")
                    }
                }
            }
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).build()

        runCatching {
            _fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }.onFailure { e ->
            Log.e("LocationUtils", "⚠️ Error requesting location updates: ${e.message}")
            close(e)
        }

        awaitClose { _fusedLocationClient.removeLocationUpdates(locationCallback) }
    }

    fun reverseGeocodeLocation(location: LocationData, callback: (String) -> Unit) {
        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    val address = addresses.firstOrNull()?.getAddressLine(0) ?: "Dirección no encontrada"
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