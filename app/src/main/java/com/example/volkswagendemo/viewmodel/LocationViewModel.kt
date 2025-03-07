package com.example.volkswagendemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.data.models.LocationData
import com.example.volkswagendemo.domain.usecase.location.SetAddressUseCase
import com.example.volkswagendemo.domain.usecase.location.SetLocationSavedUseCase
import com.example.volkswagendemo.domain.usecase.location.SetLocationUseCase
import com.example.volkswagendemo.ui.states.LocationState
import com.example.volkswagendemo.ui.states.LocationUiState
import com.example.volkswagendemo.ui.states.MutableLocationUiState
import com.example.volkswagendemo.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationUtils: LocationUtils,
    private val setLocationSavedUseCase: SetLocationSavedUseCase,
    private val setLocationUseCase: SetLocationUseCase,
    private val setAddressUseCase: SetAddressUseCase
) : ViewModel() {

    private val _locationUiState = MutableLocationUiState()
    val locationUiState: LocationUiState = _locationUiState

    fun setLocationShowing(status: Boolean) {
        viewModelScope.launch {
            _locationUiState.isShowing = status
            Log.d("isLocationShowing", "🔵 Status: $status")
        }
    }

    fun hasLocationPermission(): Boolean {
        return locationUtils.hasLocationPermission()
    }

    fun initLocation() {
        if (locationUtils.isInternetAvailable()) {
            viewModelScope.launch {
                _locationUiState.locationState = LocationState.Loading
                requestLocationUpdate()
            }
        } else {
            _locationUiState.locationState = LocationState.InternetError
        }
    }

    private fun requestLocationUpdate() {
        viewModelScope.launch {
            locationUtils.requestLocation()
                .collect { newLocation: Pair<LocationData, String> ->
                    if (newLocation.first.latitude == 0.0 || newLocation.first.longitude == 0.0) {
                        _locationUiState.message = newLocation.second
                        _locationUiState.locationState = LocationState.Error
                    } else {
                        _locationUiState.location = newLocation.first
                        setLocationSavedUseCase(true)
                        setLocationUseCase(_locationUiState.location)
                        reverseGeocodeLocation(newLocation.first)
                    }
                }
        }
    }

    private fun reverseGeocodeLocation(location: LocationData) {
        viewModelScope.launch {
            runCatching {
                _locationUiState.address = locationUtils.reverseGeocodeLocation(location)
                setAddressUseCase(_locationUiState.address)
                _locationUiState.locationState = LocationState.Success
            }.onFailure {
                _locationUiState.locationState = LocationState.Error
            }
        }
    }

}