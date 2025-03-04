package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.volkswagendemo.data.RfidData

@Stable
interface SearchUiState {
    val rfidSearchState: RfidSearchState
    val scannedTags: List<RfidData>
    val filesList: List<String>
    val selectedFileName: String
    val fileData: List<RfidData>
    val selectedTag: RfidData
    val relativeDistance: Int
    val isGeigerWorking: Boolean
    val isDevelopMode: Boolean
    val isLocationSaved: Boolean
    val isFileDialogVisible: Boolean
}

class MutableSearchUiState() : SearchUiState {
    override var rfidSearchState: RfidSearchState by mutableStateOf(RfidSearchState.Files)
    override var scannedTags: List<RfidData> by mutableStateOf(emptyList())
    override var filesList: List<String> by mutableStateOf(emptyList())
    override var selectedFileName: String by mutableStateOf("")
    override var fileData: List<RfidData> by mutableStateOf(emptyList())
    override var selectedTag: RfidData by mutableStateOf(RfidData("","",""))
    override var relativeDistance: Int by mutableIntStateOf(0)
    override var isGeigerWorking: Boolean by mutableStateOf(false)
    override val isDevelopMode: Boolean by mutableStateOf(true)
    override val isLocationSaved: Boolean by mutableStateOf(false)
    override var isFileDialogVisible: Boolean by mutableStateOf(false)
}

sealed class RfidSearchState(val name: String) {
    data object Files : RfidSearchState("Getting files")
    data object Setup : RfidSearchState("Setup")
    data object Ready : RfidSearchState("Ready")
    data object Reading : RfidSearchState("Reading")
    data object Pause : RfidSearchState("Pause")
    data object Stop : RfidSearchState("Stop")
    data object Error : RfidSearchState("Error")
}