package com.example.volkswagendemo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.domain.usecase.settings.GetAntennaPowerUseCase
import com.example.volkswagendemo.domain.usecase.settings.GetBeeperVolumeUseCase
import com.example.volkswagendemo.domain.usecase.settings.SetAntennaPowerUseCase
import com.example.volkswagendemo.domain.usecase.settings.SetBeeperVolumeUseCase
import com.example.volkswagendemo.ui.states.MutableSettingUiState
import com.example.volkswagendemo.ui.states.SettingState
import com.example.volkswagendemo.ui.states.SettingUiState
import com.zebra.rfid.api3.InvalidUsageException
import com.zebra.rfid.api3.OperationFailureException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getAntennaPowerUseCase: GetAntennaPowerUseCase,
    private val setAntennaPowerUseCase: SetAntennaPowerUseCase,
    private val getBeeperVolumeUseCase: GetBeeperVolumeUseCase,
    private val setBeeperVolumeUseCase: SetBeeperVolumeUseCase,
) : ViewModel() {

    private val _settingUiState = MutableSettingUiState()
    val settingUiStates: SettingUiState = _settingUiState

    init {
        viewModelScope.launch {
            runCatching {
                val antennaPowerDeferred =  async { getAntennaPowerUseCase().first() }
                val beeperVolumeDeferred = async { getBeeperVolumeUseCase().first() }
                _settingUiState.antennaPower = antennaPowerDeferred.await()
                _settingUiState.beeperVolume = beeperVolumeDeferred.await()
            }.onSuccess {
                updateSettingsState(settingState = SettingState.Ready)
            }.onFailure { exception ->
                handleError("RFID_fetchSettings", exception)
            }
        }
    }

    fun updateAntennaPower(newPower: Float) {
        viewModelScope.launch {
            _settingUiState.antennaPower = newPower
        }
    }

    fun updateBeeperVolume(newVolume: Int) {
        viewModelScope.launch {
            _settingUiState.beeperVolume = newVolume
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            val currentState = _settingUiState
            Log.d(
                "RFID_saveSettings",
                "📌 Guardando valores: AntennaPower=${currentState.antennaPower}, BeeperVolume=${currentState.beeperVolume}"
            )
            runCatching {
                setAntennaPowerUseCase(power = currentState.antennaPower)
                setBeeperVolumeUseCase(volume = currentState.beeperVolume)
            }.onSuccess {
                Log.d("RFID_saveSettings", "✅ Configuración guardada exitosamente")
            }.onFailure { exception ->
                handleError("RFID_saveSettings", exception)
            }
        }
    }

    private fun updateSettingsState(settingState: SettingState) {
        viewModelScope.launch {
            _settingUiState.settingState = settingState
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