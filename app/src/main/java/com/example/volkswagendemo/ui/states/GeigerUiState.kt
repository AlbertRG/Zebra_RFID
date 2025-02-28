package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.volkswagendemo.data.RfidData

@Stable
interface GeigerUiState {
    val rfidGeigerState: RfidGeigerState
    val scannedTags: List<RfidData>
    val filesList: List<String>
    val selectedFileName: String
    val fileData: List<RfidData>
    val isDevelopMode: Boolean
    val isLocationSaved: Boolean
    val isFileDialogVisible: Boolean
}

class MutableGeigerUiState() : GeigerUiState {
    override var rfidGeigerState: RfidGeigerState by mutableStateOf(RfidGeigerState.Files)
    override var scannedTags: List<RfidData> by mutableStateOf(emptyList())
    override var filesList: List<String> by mutableStateOf(emptyList())
    override var selectedFileName: String by mutableStateOf("")
    override var fileData: List<RfidData> by mutableStateOf(emptyList())
    override val isDevelopMode: Boolean by mutableStateOf(false)
    override val isLocationSaved: Boolean by mutableStateOf(false)
    override var isFileDialogVisible: Boolean by mutableStateOf(false)
}

sealed class RfidGeigerState(val name: String) {
    data object Files : RfidGeigerState("Getting files")
    data object Setup : RfidGeigerState("Setup")
    data object Ready : RfidGeigerState("Ready")
    data object Reading : RfidGeigerState("Reading")
    data object Pause : RfidGeigerState("Pause")
    data object Stop : RfidGeigerState("Stop")
    data object Error : RfidGeigerState("Error")
}