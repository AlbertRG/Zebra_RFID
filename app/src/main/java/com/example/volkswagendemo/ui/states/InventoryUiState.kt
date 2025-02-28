package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.volkswagendemo.data.RfidData

@Stable
interface InventoryUiState {
    val rfidState: RfidState
    val scannedTags: List<RfidData>
    val filesList: List<String>
    val selectedFileName: String
    val fileData: List<RfidData>
    val isDevelopMode: Boolean
    val isLocationSaved: Boolean
    val isFileDialogVisible: Boolean
}

class MutableInventoryUiState() : InventoryUiState {
    override var rfidState: RfidState by mutableStateOf(RfidState.Connecting)
    override var scannedTags: List<RfidData> by mutableStateOf(emptyList())
    override var filesList: List<String> by mutableStateOf(emptyList())
    override var selectedFileName: String by mutableStateOf("")
    override var fileData: List<RfidData> by mutableStateOf(emptyList())
    override val isDevelopMode: Boolean by mutableStateOf(false)
    override val isLocationSaved: Boolean by mutableStateOf(false)
    override var isFileDialogVisible: Boolean by mutableStateOf(false)
}

sealed class RfidState(val name: String) {
    data object Connecting : RfidState("Connecting")
    data object Start : RfidState("Start")
    data object Reading : RfidState("Reading")
    data object Pause : RfidState("Pause")
    data object Stop : RfidState("Stop")
    data object Error: RfidState("Error")
}