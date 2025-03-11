package com.example.volkswagendemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.domain.usecase.location.GetAddressUseCase
import com.example.volkswagendemo.domain.usecase.location.GetLocationSavedUseCase
import com.example.volkswagendemo.domain.usecase.location.GetLocationUseCase
import com.example.volkswagendemo.domain.usecase.workshop.SetWorkshopUseCase
import com.example.volkswagendemo.ui.states.HomeUiState
import com.example.volkswagendemo.ui.states.MutableHomeUiState
import com.example.volkswagendemo.utils.ExcelUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLocationSavedUseCase: GetLocationSavedUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val getAddressUseCase: GetAddressUseCase,
    private val setWorkshopUseCase: SetWorkshopUseCase,
    excelUtils: ExcelUtils
) : ViewModel() {

    init {
        excelUtils.createAppFolder()
    }

    private val _homeUiState = MutableHomeUiState()
    val homeUiStates: HomeUiState = _homeUiState

    fun setMenuShowing(status: Boolean) {
        viewModelScope.launch {
            _homeUiState.isMenuShowing = status
            Log.d("isMenuShowing", "🔵 Status: $status")
        }
    }

    fun openWorkshopDialog() {
        runCatching {
            getLocationInfo()
            _homeUiState.isWorkshopShowing = true
            Log.d("isWorkshopShowing", "🔵 Status: Open")
            Log.e("addressA", "🔵 Address: ${_homeUiState.address}")
        }.onFailure { e ->
            Log.e("setWorkshopShowing", "🔴 Error: ${e.message}")
        }
    }

    fun closeWorkshopDialog(navigateToInventory: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                setWorkshopUseCase(_homeUiState.workshop)
            }.onFailure { e ->
                Log.e("closeWorkshopDialog", "🔴 Error setting workshop: ${e.message}")
            }
            _homeUiState.isWorkshopShowing = false
            navigateToInventory()
            Log.d("isWorkshopShowing", "🔵 Status: Close")
        }
    }

    private fun getLocationInfo() {
        viewModelScope.launch {
            runCatching {
                _homeUiState.isLocationSaved = getLocationSavedUseCase().first()
                if (_homeUiState.isLocationSaved) {
                    val locationDeferred = async { getLocationUseCase().firstOrNull() }
                    val addressDeferred = async { getAddressUseCase().firstOrNull() }
                    _homeUiState.location = locationDeferred.await() ?: _homeUiState.location
                    _homeUiState.address = addressDeferred.await() ?: _homeUiState.address
                    Log.e("locationB", "🔵 Location: ${_homeUiState.location.latitude}, ${_homeUiState.location.longitude}")
                    Log.e("addressB", "🔵 Address: ${_homeUiState.address}")
                }
            }.onFailure { e ->
                Log.e("getLocationInfo", "🔴 Error: ${e.message}")
            }
        }
    }

}