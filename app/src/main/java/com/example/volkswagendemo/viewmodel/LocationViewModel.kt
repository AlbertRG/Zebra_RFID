package com.example.volkswagendemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.data.LocationData
import com.example.volkswagendemo.ui.states.LocationUiState
import com.example.volkswagendemo.ui.states.MutableLocationUiState
import com.example.volkswagendemo.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationUtils: LocationUtils
) : ViewModel() {

    private val _locationUiState = MutableLocationUiState()
    val locationUiState: LocationUiState = _locationUiState

    fun hasLocationPermission(): Boolean {
        return locationUtils.hasLocationPermission()
    }

    fun setLocationShowing(status: Boolean) {
        viewModelScope.launch {
            _locationUiState.isShowing = status
            Log.d("isLocationShowing", "🔵 Status: $status")
        }
    }

    fun initLocation() {
        _locationUiState.isLoading = true
        _locationUiState.hasError = false
        if (locationUtils.isInternetAvailable()) {
            viewModelScope.launch {
                _locationUiState.isInternetError = false
                requestLocationUpdate()
            }
        } else {
            _locationUiState.isInternetError = true
        }
    }

    private fun requestLocationUpdate() {
        viewModelScope.launch {
            locationUtils.requestLocation()
                .collect { newLocation: Pair<LocationData, String> ->
                    if (newLocation.first.latitude == 0.0 || newLocation.first.longitude == 0.0) {
                        _locationUiState.hasError = true
                        _locationUiState.message = newLocation.second
                    } else {
                        _locationUiState.location = newLocation.first
                        reverseGeocodeLocation(newLocation.first)
                    }
                }
        }
    }

    private fun reverseGeocodeLocation(location: LocationData) {
        locationUtils.reverseGeocodeLocation(location) { result ->
            _locationUiState.address = result
            _locationUiState.isLoading = false
        }
    }

}