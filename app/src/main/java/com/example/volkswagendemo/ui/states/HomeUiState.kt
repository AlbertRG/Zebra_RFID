package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
interface HomeUiState {
    val isMenuShowing: Boolean
    val isWorkshopShowing: Boolean
}

class MutableHomeUiState : HomeUiState {
    override var isMenuShowing: Boolean by mutableStateOf(false)
    override var isWorkshopShowing: Boolean by mutableStateOf(false)
}