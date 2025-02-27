package com.example.volkswagendemo.ui.states

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.volkswagendemo.data.TagData

@Stable
interface SearchUiState {
    val rfidSearchState: RfidSearchState
    val scannedTags: List<TagData>
    val filesList: List<String>
    val selectedFileName: String
    val fileData: List<TagData>
    val isDevelopMode: Boolean
    val isLocationSaved: Boolean
    val isFileDialogVisible: Boolean
}

class MutableSearchUiState() : SearchUiState {
    override var rfidSearchState: RfidSearchState by mutableStateOf(RfidSearchState.Files)
    override var scannedTags: List<TagData> by mutableStateOf(emptyList())
    override var filesList: List<String> by mutableStateOf(emptyList())
    override var selectedFileName: String by mutableStateOf("")
    override var fileData: List<TagData> by mutableStateOf(emptyList())
    override val isDevelopMode: Boolean by mutableStateOf(true)
    override val isLocationSaved: Boolean by mutableStateOf(false)
    override var isFileDialogVisible: Boolean by mutableStateOf(false)
}

sealed class RfidSearchState(val name: String) {
    data object Files : RfidSearchState("Getting files")
    data object SetupInfo : RfidSearchState("Setup info")
    data object Ready : RfidSearchState("Ready")
    data object Reading : RfidSearchState("Reading")
    data object Pause : RfidSearchState("Pause")
    data object Stop : RfidSearchState("Stop")
    data object Error : RfidSearchState("Error")
}