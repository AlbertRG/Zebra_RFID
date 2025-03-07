package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.volkswagendemo.data.models.RfidData

@Stable
interface InventoryUiState {
    val rfidState: RfidInventoryState
    val workshop: String
    val scannedTags: List<RfidData>
    val filesList: List<String>
    val selectedFileName: String
    val fileData: List<RfidData>
    val isDevelopMode: Boolean
    val isLocationSaved: Boolean
    val isFileDialogVisible: Boolean
}

class MutableInventoryUiState() : InventoryUiState {
    override var rfidState: RfidInventoryState by mutableStateOf(RfidInventoryState.Connecting)
    override var workshop: String by mutableStateOf("")
    override var scannedTags: List<RfidData> by mutableStateOf(emptyList())
    override var filesList: List<String> by mutableStateOf(emptyList())
    override var selectedFileName: String by mutableStateOf("")
    override var fileData: List<RfidData> by mutableStateOf(emptyList())
    override val isDevelopMode: Boolean by mutableStateOf(true)
    override val isLocationSaved: Boolean by mutableStateOf(false)
    override var isFileDialogVisible: Boolean by mutableStateOf(false)
}

sealed class RfidInventoryState(val name: String) {
    data object Connecting : RfidInventoryState("Connecting")
    data object Ready : RfidInventoryState("Ready")
    data object Reading : RfidInventoryState("Reading")
    data object Pause : RfidInventoryState("Pause")
    data object Stop : RfidInventoryState("Stop")
    data object Error: RfidInventoryState("Error")
}