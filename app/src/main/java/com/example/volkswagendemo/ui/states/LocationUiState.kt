package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.volkswagendemo.data.models.LocationData

@Stable
interface LocationUiState {
    val locationState: LocationState
    val location: LocationData
    val address: String
    val message: String
    val isShowing: Boolean
}

class MutableLocationUiState() : LocationUiState {
    override var locationState: LocationState by mutableStateOf(LocationState.Loading)
    override var location: LocationData by mutableStateOf(LocationData(0.0, 0.0))
    override var address: String by mutableStateOf("")
    override var message: String by mutableStateOf("")
    override var isShowing: Boolean by mutableStateOf(false)
}

sealed class LocationState(val name: String) {
    data object Loading : LocationState("Loading")
    data object Success : LocationState("Success")
    data object Error : LocationState("Error")
    data object InternetError : LocationState("InternetError")
}