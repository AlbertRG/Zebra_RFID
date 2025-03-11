package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import com.example.volkswagendemo.data.models.SettingsData

@Stable
interface SettingUiState {
    val settingState: SettingState
    val settings: SettingsData
}

data class MutableSettingUiState(
    override val settingState: SettingState = SettingState.Loading,
    override val settings: SettingsData = SettingsData(0f, 0)
) : SettingUiState

sealed class SettingState(val name: String) {
    data object Loading : SettingState("Loading")
    data object Ready : SettingState("Ready")
    data object Error : SettingState("Error")
}