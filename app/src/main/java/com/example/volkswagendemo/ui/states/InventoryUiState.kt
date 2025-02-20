package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
interface InventoryUiState {
    val tags: List<String>
    val files: List<String>
    val isConnecting: Boolean
    val isConnected: Boolean
    val isReading: Boolean
    val isStopped: Boolean
    val isOnResume: Boolean
    val hasError: Boolean
}

class MutableInventoryUiState() : InventoryUiState {
    override var tags: List<String> by mutableStateOf(emptyList())
    override var files: List<String> by mutableStateOf(emptyList())
    override var isConnecting: Boolean by mutableStateOf(false)
    override var isConnected: Boolean by mutableStateOf(false)
    override var isReading: Boolean by mutableStateOf(false)
    override var isStopped: Boolean by mutableStateOf(false)
    override var isOnResume: Boolean by mutableStateOf(false)
    override var hasError: Boolean by mutableStateOf(false)
}