package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
interface BatteryUiState {
    val manufactureDate: String
    val modelNumber: String
    val batteryId: String
    val health: Int
    val cycleCount: Int
    val percentage: Int
    val temperature: Int
    val isConnecting: Boolean
    val isConnected: Boolean
    val hasError: Boolean
}

class MutableBatteryUiState() : BatteryUiState {
    override var manufactureDate: String by mutableStateOf("")
    override var modelNumber: String by mutableStateOf("")
    override var batteryId: String by mutableStateOf("")
    override var health: Int by mutableIntStateOf(0)
    override var cycleCount: Int by mutableIntStateOf(0)
    override var percentage: Int by mutableIntStateOf(0)
    override var temperature: Int by mutableIntStateOf(0)
    override var isConnecting: Boolean by mutableStateOf(true)
    override var isConnected: Boolean by mutableStateOf(false)
    override var hasError: Boolean by mutableStateOf(false)
}