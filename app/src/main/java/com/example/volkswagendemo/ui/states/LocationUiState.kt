package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.volkswagendemo.data.LocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Stable
interface LocationUiState {
    val location: LocationData
    val address: String
    val message: String
    val isShowing: Boolean
    val isLoading: Boolean
    val hasError: Boolean
    val isInternetError: Boolean
}

class MutableLocationUiState() : LocationUiState {
    override var location: LocationData by mutableStateOf(LocationData(0.0, 0.0))
    override var address: String by mutableStateOf("")
    override var message: String by mutableStateOf("")
    override var isShowing: Boolean by mutableStateOf(false)
    override var isLoading: Boolean by mutableStateOf(false)
    override var hasError: Boolean by mutableStateOf(false)
    override var isInternetError: Boolean by mutableStateOf(false)
}