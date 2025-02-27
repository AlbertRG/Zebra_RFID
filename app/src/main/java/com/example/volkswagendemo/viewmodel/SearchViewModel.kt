package com.example.volkswagendemo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.data.TagData
import com.example.volkswagendemo.ui.states.MutableSearchUiState
import com.example.volkswagendemo.ui.states.RfidSearchState
import com.example.volkswagendemo.ui.states.RfidState
import com.example.volkswagendemo.utils.ConversionUtils
import com.example.volkswagendemo.utils.ExcelUtils
import com.zebra.rfid.api3.BEEPER_VOLUME
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE
import com.zebra.rfid.api3.InvalidUsageException
import com.zebra.rfid.api3.MEMORY_BANK
import com.zebra.rfid.api3.OperationFailureException
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.RfidReadEvents
import com.zebra.rfid.api3.RfidStatusEvents
import com.zebra.rfid.api3.START_TRIGGER_TYPE
import com.zebra.rfid.api3.STATUS_EVENT_TYPE
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE
import com.zebra.rfid.api3.TagAccess
import com.zebra.rfid.api3.TriggerInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application,
    private val conversionUtils: ConversionUtils,
    private val excelUtils: ExcelUtils
) : ViewModel() {

    private val _searchUiState = MutableSearchUiState()
    val searchUiState: MutableSearchUiState = _searchUiState

    private var readers = Readers(application.applicationContext, ENUM_TRANSPORT.SERVICE_USB)
    private var readerDevice: ReaderDevice? = null
    private var rfidReader: RFIDReader? = null
    private val eventHandler = EventHandler()
    private val _scannedTagsList = mutableListOf<TagData>()

    init {
        getFilesList()
    }

    private fun getFilesList() {
        _searchUiState.filesList = excelUtils.indexExcelFiles("Demo")
    }

    fun selectFile(file: String) {
        _searchUiState.selectedFileName = file
    }

    fun setupSearch() {
        if (_searchUiState.rfidSearchState != RfidSearchState.SetupInfo) {
            runCatching {
                getSelectedFileData()
                connectReader()
            }.onFailure { exception ->
                handleError("setupSearch", exception)
            }
        }
    }

    private fun getSelectedFileData() {
        _searchUiState.fileData = excelUtils.readSpecificExcelFile(_searchUiState.selectedFileName)
        updateSearchState(rfidSearchState = RfidSearchState.SetupInfo)
    }

    private fun connectReader() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                var attempts = 0
                var availableRFIDReaderList: List<ReaderDevice>? = null

                while (attempts < 10) {
                    availableRFIDReaderList = readers.GetAvailableRFIDReaderList()

                    if (!availableRFIDReaderList.isNullOrEmpty()) {
                        Log.i("RFID_connectReader", "✅ Reader found in intent $attempts")
                        break
                    }

                    Log.i(
                        "RFID_connectReader",
                        "⏱️ No readers available, retrying... ($attempts/10)"
                    )
                    attempts++
                    delay(1000)
                }

                if (availableRFIDReaderList.isNullOrEmpty()) {
                    throw IllegalStateException("No available readers found")
                }

                readerDevice = availableRFIDReaderList[0]
                rfidReader = readerDevice?.rfidReader?.apply {
                    connect()
                    configureReader(this)
                }
            }.onFailure { exception ->
                handleError("RFID_connectReader", exception)
            }
        }
    }

    private fun configureReader(rfidReader: RFIDReader) {
        runCatching {
            //configureAntenna(rfidReader)
            configureTrigger(rfidReader)
            configureEvents(rfidReader)
        }.onSuccess {
            updateSearchState(rfidSearchState = RfidSearchState.Ready)
            Log.i("RFID_configureReader", "✅ RFID reader configured and ready!")
        }.onFailure { exception ->
            handleError("RFID_configureReader", exception)
        }
    }

    private fun configureAntenna(rfidReader: RFIDReader) {
        val antennaConfig = rfidReader.Config.Antennas.getAntennaRfConfig(1)
        antennaConfig.transmitPowerIndex = 300
        antennaConfig.setrfModeTableIndex(0)
        antennaConfig.tari = 0
        rfidReader.Config.Antennas.setAntennaRfConfig(1, antennaConfig)
    }

    private fun configureTrigger(rfidReader: RFIDReader) {
        val triggerInfo = TriggerInfo().apply {
            StartTrigger.triggerType = START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
            StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE
        }
        rfidReader.Config.apply {
            setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true)
            setUniqueTagReport(true)
            setLedBlinkEnable(true)
            startTrigger = triggerInfo.StartTrigger
            stopTrigger = triggerInfo.StopTrigger
            beeperVolume = BEEPER_VOLUME.LOW_BEEP
        }
    }

    private fun configureEvents(rfidReader: RFIDReader) {
        rfidReader.Events.apply {
            addEventsListener(eventHandler)
            setHandheldEvent(true)
            setTagReadEvent(true)
            setAttachTagDataWithReadEvent(true)
        }
    }

    fun performInventory() {
        if (_searchUiState.rfidSearchState == RfidSearchState.Ready ||
            _searchUiState.rfidSearchState == RfidSearchState.Pause
        ) {
            runCatching {
                performInventoryRead()
            }.onFailure { exception ->
                handleError("RFID_performInventory", exception)
            }
        }
    }

    private fun performInventoryRead() {
        val tagAccess = TagAccess()
        val readAccessParams = tagAccess.ReadAccessParams().apply {
            accessPassword = 0
            memoryBank = MEMORY_BANK.MEMORY_BANK_USER
            if (_searchUiState.isDevelopMode) {
                /* In developer mode (DevelopMode), we use count = 8 because:
                8 nibbles correspond to 32 hexadecimal characters.
                32 hexadecimal characters convert to 16 alphanumeric VIN characters.
                Test tags only support 128-bit, which means a maximum of 32 hexadecimal characters.
                If we try to read more characters from a 128-bit tag, the read operation will fail.*/
                count = 8
                offset = 0
            } else {
                /* In normal mode, we use count = 9 because:
                9 nibbles correspond to 36 hexadecimal characters.
                36 hexadecimal characters convert to 18 alphanumeric VIN characters.
                Real tags are 256-bit, allowing up to 64 hexadecimal characters.*/
                count = 9
                offset = 4
            }
        }
        updateSearchState(rfidSearchState = RfidSearchState.Reading)
        rfidReader?.Actions?.TagAccess?.readEvent(readAccessParams, null, null)
    }

    fun pauseInventory() {
        if (_searchUiState.rfidSearchState == RfidSearchState.Reading) {
            runCatching {
                pauseInventoryRead()
            }.onFailure { exception ->
                handleError("RFID_pauseInventory", exception)
            }
        }
    }

    private fun pauseInventoryRead() {
        updateSearchState(rfidSearchState = RfidSearchState.Pause)
        rfidReader?.Actions?.TagAccess?.stopAccess()
    }

    fun retrySetupReader() {
        setupSearch()
    }

    private fun updateSearchState(rfidSearchState: RfidSearchState) {
        viewModelScope.launch {
            _searchUiState.rfidSearchState = rfidSearchState
            Log.d("RFID_updateSearchState", "🔵 Status: ${rfidSearchState.name}")
        }
    }

    private fun handleError(title: String, exception: Throwable) {
        val message = when (exception) {
            is InvalidUsageException -> "⚠️ InvalidUsage: ${exception.message}"
            is IllegalStateException -> "⚠️ IllegalState: ${exception.message}"
            is OperationFailureException -> "⚠️ OperationFailure: ${exception.vendorMessage}"
            else -> "⚠️ Unexpected error: ${exception.message}"
        }
        updateSearchState(rfidSearchState = RfidSearchState.Error)
        Log.e(title, message)
    }

    override fun onCleared() {
        super.onCleared()
        rfidReader?.let {
            try {
                it.disconnect()
                rfidReader = null
                readers.Dispose()
                Log.i("RFID_onCleared", "Reader disconnected")
            } catch (e: Exception) {
                Log.e("RFID_onCleared", "Error disconnecting reader: ${e.message}")
            }
        }
    }

    inner class EventHandler : RfidEventsListener {

        override fun eventReadNotify(e: RfidReadEvents?) {
            rfidReader?.Actions?.getReadTags(100)?.forEach { tag ->

                if (_scannedTagsList.none { it.controlData == tag.memoryBankData }) {

                    val repuve = conversionUtils.convert(tag.tagID.take(16))
                    val vin = conversionUtils.convert(tag.memoryBankData.take(34))
                    val rawMemoryData = tag.memoryBankData

                    if (repuve == null || vin == null) {
                        Log.e(
                            "RFID_eventReadNotify",
                            "⚠️ Conversion failed (repuve=$repuve, vin=$vin)"
                        )
                        return@forEach
                    }

                    if (repuve.all { it.isDigit() } && vin.all { it.isLetterOrDigit() }) {
                        val tagData =
                            TagData(
                                repuve = repuve,
                                vin = vin,
                                controlData = rawMemoryData
                            )
                        Log.d(
                            "RFID_eventReadNotify",
                            "Tag Data Info: ${tagData.repuve} - ${tagData.vin}"
                        )
                        _searchUiState.fileData = _searchUiState.fileData.filterNot {
                            it.repuve == tagData.repuve
                        }
                        _scannedTagsList.add(tagData)
                        _searchUiState.scannedTags =
                            _scannedTagsList.toList().distinctBy { it.repuve }.reversed()
                    } else {
                        Log.e(
                            "RFID_eventReadNotify",
                            "⚠️ Invalid tag ignored (repuve=$repuve, vin=$vin)"
                        )
                    }
                }
            }
        }

        override fun eventStatusNotify(rfidStatusEvents: RfidStatusEvents?) {
            rfidStatusEvents?.let { events ->
                val statusEventType = events.StatusEventData.statusEventType
                Log.d("RFID_eventStatusNotify", "Status Notification: $statusEventType")

                if (statusEventType == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {

                    val handheldEvent =
                        events.StatusEventData.HandheldTriggerEventData.handheldEvent

                    when (handheldEvent) {
                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED -> {
                            try {
                                performInventory()
                            } catch (e: Exception) {
                                Log.e(
                                    "RFID_eventStatusNotify",
                                    "Error performing inventory: ${e.message}"
                                )
                            }
                        }

                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED -> {
                            try {
                                pauseInventory()
                            } catch (e: Exception) {
                                Log.e(
                                    "RFID_eventStatusNotify",
                                    "Error pausing inventory: ${e.message}"
                                )
                            }
                        }

                        else -> {
                            Log.d("RFID_eventStatusNotify", handheldEvent.toString())
                        }
                    }
                }
            }
        }

    }

}