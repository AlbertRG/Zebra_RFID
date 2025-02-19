package com.example.volkswagendemo.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.data.LocationData
import com.example.volkswagendemo.ui.states.LocationStates
import com.example.volkswagendemo.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationUtils: LocationUtils
) : ViewModel() {

    var showWorkshopDialog by mutableStateOf(false)

    private val _bottomSheetStatus = MutableStateFlow(false)
    var bottomSheetStatus: StateFlow<Boolean> = _bottomSheetStatus.asStateFlow()

    private val _locationStatus = MutableStateFlow<LocationStates>(LocationStates.Hide)
    val locationStatus: StateFlow<LocationStates> = _locationStatus.asStateFlow()

    private val _location = MutableStateFlow(LocationData(0.0,0.0))
    val location: StateFlow<LocationData> = _location.asStateFlow()
    private val _address = MutableStateFlow("Ubicación no disponible")
    val address: StateFlow<String> = _address.asStateFlow()
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    fun showBottomSheet() {
        updateBottomSheetStatus(true)
    }

    fun hideBottomSheet() {
        updateBottomSheetStatus(false)
    }

    private fun updateBottomSheetStatus(status: Boolean) {
        viewModelScope.launch {
            _bottomSheetStatus.value = status
            Log.d("updateBottomSheetStatus", "🔵 Status: $status")
        }
    }

    fun showLocalizationDialog() {
        updateLocationStatus(LocationStates.Loading)
        if (locationUtils.isInternetAvailable()) {
            viewModelScope.launch {
                requestLocationUpdate()
            }
        } else {
            _message.value = "Por favor verifica tu conexion a internet"
            updateLocationStatus(LocationStates.InternetError)
        }
    }

    fun hideLocalizationDialog() {
        updateLocationStatus(LocationStates.Hide)
    }

    fun hasLocationPermission(): Boolean {
        return locationUtils.hasLocationPermission()
    }

    private fun updateLocationStatus(status: LocationStates) {
        viewModelScope.launch {
            _locationStatus.value = status
            Log.d("updateLocationStatus", "🔵 Status: $status")
        }
    }

    private fun requestLocationUpdate() {
        viewModelScope.launch {
            locationUtils.requestLocation()
                .collect { newLocation: Pair<LocationData, String> ->
                    if (newLocation.first.latitude == 0.0 || newLocation.first.longitude == 0.0) {
                        updateLocationStatus(LocationStates.Error)
                        _message.value = newLocation.second
                    } else {
                        _location.value = newLocation.first
                        reverseGeocodeLocation(newLocation.first)
                    }
                }
        }
    }

    private fun reverseGeocodeLocation(location: LocationData) {
        locationUtils.reverseGeocodeLocation(location) { result ->
            _address.value = result
            updateLocationStatus(LocationStates.Show)
        }
    }

}