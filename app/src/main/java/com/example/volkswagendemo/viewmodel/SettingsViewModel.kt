package com.example.volkswagendemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.data.models.SettingsData
import com.example.volkswagendemo.domain.usecase.settings.GetSettingsUseCase
import com.example.volkswagendemo.domain.usecase.settings.SetSettingsUseCase
import com.example.volkswagendemo.ui.states.MutableSettingUiState
import com.example.volkswagendemo.ui.states.SettingState
import com.example.volkswagendemo.ui.states.SettingUiState
import com.zebra.rfid.api3.InvalidUsageException
import com.zebra.rfid.api3.OperationFailureException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val setSettingsUseCase: SetSettingsUseCase
) : ViewModel() {

    private val _settingUiState = MutableStateFlow(MutableSettingUiState())
    val settingUiStates: StateFlow<SettingUiState> = _settingUiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching {
                getSettingsUseCase().first()
            }.onSuccess { settings ->
                settings?.let {
                    _settingUiState.update { currentState: MutableSettingUiState ->
                        currentState.copy(settings = settings)
                    }
                }
                updateSettingsState(SettingState.Ready)
            }.onFailure { exception ->
                handleError("RFID_fetchSettings", exception)
            }
        }
    }

    fun updateAntennaPower(newPower: Float) {
        viewModelScope.launch {
            _settingUiState.update { currentState: MutableSettingUiState ->
                currentState.copy(settings = currentState.settings.copy(antennaPower = newPower))
            }
        }
    }

    fun updateBeeperVolume(newVolume: Int) {
        viewModelScope.launch {
            _settingUiState.update { currentState: MutableSettingUiState ->
                currentState.copy(settings = currentState.settings.copy(beeperVolume = newVolume))
            }
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            runCatching {
                setSettingsUseCase(
                    SettingsData(
                        antennaPower = _settingUiState.value.settings.antennaPower,
                        beeperVolume = _settingUiState.value.settings.beeperVolume
                    )
                )
            }.onSuccess {
                Log.d("RFID_saveSettings", "✅ Configuration saved successfully")
            }.onFailure { exception ->
                handleError("RFID_saveSettings", exception)
            }
        }
    }

    private fun updateSettingsState(settingState: SettingState) {
        viewModelScope.launch {
            _settingUiState.update { currentState: MutableSettingUiState ->
                currentState.copy(settingState = settingState)
            }
            Log.d("RFID_updateSettingsState", "🔵 Status: ${settingState.name}")
        }
    }

    private fun handleError(title: String, exception: Throwable) {
        val message = when (exception) {
            is InvalidUsageException -> "⚠️ InvalidUsage: ${exception.message}"
            is IllegalStateException -> "⚠️ IllegalState: ${exception.message}"
            is OperationFailureException -> "⚠️ OperationFailure: ${exception.vendorMessage}"
            else -> "⚠️ Unexpected error: ${exception.message}"
        }
        updateSettingsState(settingState = SettingState.Error)
        Log.e(title, message)
    }

}