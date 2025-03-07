package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.volkswagendemo.data.models.LocationData

@Stable
interface HomeUiState {
    val isMenuShowing: Boolean
    val isLocationSaved: Boolean
    val location: LocationData
    val address: String
    val isWorkshopShowing: Boolean
    var workshop: String
}

class MutableHomeUiState : HomeUiState {
    override var isMenuShowing: Boolean by mutableStateOf(false)
    override var isLocationSaved: Boolean by mutableStateOf(false)
    override var location: LocationData by mutableStateOf(LocationData(0.0, 0.0))
    override var address: String by mutableStateOf("")
    override var isWorkshopShowing: Boolean by mutableStateOf(false)
    override var workshop: String by mutableStateOf("")
}