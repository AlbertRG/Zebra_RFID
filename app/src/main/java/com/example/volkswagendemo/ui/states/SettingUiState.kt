package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
interface SettingUiState {
    val settingState: SettingState
    val antennaPower: Float
    val beeperVolume: Int
}

class MutableSettingUiState() : SettingUiState {
    override var settingState: SettingState by mutableStateOf(SettingState.Loading)
    override var antennaPower: Float by mutableFloatStateOf(0f)
    override var beeperVolume: Int by mutableIntStateOf(0)
}

sealed class SettingState(val name: String) {
    data object Loading : SettingState("Loading")
    data object Ready : SettingState("Ready")
    data object Error : SettingState("Error")
}