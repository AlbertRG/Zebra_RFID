package com.example.volkswagendemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.ui.states.HomeUiState
import com.example.volkswagendemo.ui.states.MutableHomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _homeUiState = MutableHomeUiState()
    val homeUiStates: HomeUiState = _homeUiState

    fun setMenuShowing(status: Boolean) {
        viewModelScope.launch {
            _homeUiState.isMenuShowing = status
            Log.d("isMenuShowing", "🔵 Status: $status")
        }
    }

    fun setWorkshopShowing(status: Boolean) {
        viewModelScope.launch {
            _homeUiState.isWorkshopShowing = status
            Log.d("isWorkshopShowing", "🔵 Status: $status")
        }
    }

}