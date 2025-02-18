package com.example.volkswagendemo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.data.LocationData
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

    private val _localizationDialogStatus = MutableStateFlow(false)
    var localizationDialogStatus: StateFlow<Boolean> = _localizationDialogStatus.asStateFlow()

    private val _location = MutableStateFlow<LocationData?>(null)
    val location: StateFlow<LocationData?> = _location.asStateFlow()

    private val _address = MutableStateFlow("Ubicación no disponible")
    val address: StateFlow<String> = _address.asStateFlow()

    fun showBottomSheet() {
        _bottomSheetStatus.value = true
    }

    fun hideBottomSheet() {
        _bottomSheetStatus.value = false
    }

    fun showLocalizationDialog() {
        requestLocationUpdate()
        _localizationDialogStatus.value = true
    }

    fun hideLocalizationDialog() {
        _localizationDialogStatus.value = false
    }

    fun hasLocationPermission(): Boolean {
        return locationUtils.hasLocationPermission()
    }

    private fun requestLocationUpdate() {
        viewModelScope.launch {
            locationUtils.getLocationOnce()
                .collect { newLocation ->
                    _location.value = newLocation
                    reverseGeocodeLocation(newLocation)
                }
        }
    }

    private fun reverseGeocodeLocation(location: LocationData) {
        locationUtils.reverseGeocodeLocation(location) { result ->
            _address.value = result
        }
    }

}