package com.example.volkswagendemo.viewmodel

import android.app.Application
import android.util.ArrayMap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.data.RfidData
import com.example.volkswagendemo.ui.states.MutableGeigerUiState
import com.example.volkswagendemo.ui.states.RfidGeigerState
import com.example.volkswagendemo.utils.ExcelUtils
import com.zebra.rfid.api3.BEEPER_VOLUME
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE
import com.zebra.rfid.api3.InvalidUsageException
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
import com.zebra.rfid.api3.TagData
import com.zebra.rfid.api3.TriggerInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeigerViewModel @Inject constructor(
    application: Application,
    private val excelUtils: ExcelUtils
) : ViewModel() {
    private val _geigerUiState = MutableGeigerUiState()
    val geigerUiState: MutableGeigerUiState = _geigerUiState

    private var readers = Readers(application.applicationContext, ENUM_TRANSPORT.SERVICE_USB)
    private var readerDevice: ReaderDevice? = null
    private var rfidReader: RFIDReader? = null
    private val eventHandler = EventHandler()
    private val _scannedTagsList = mutableListOf<RfidData>()

    init {
        getFilesList()
    }

    private fun getFilesList() {
        _geigerUiState.filesList = excelUtils.indexExcelFiles("Demo")
    }

    fun selectFile(file: String) {
        _geigerUiState.selectedFileName = file
    }

    fun setupGeiger() {
        if (_geigerUiState.rfidGeigerState != RfidGeigerState.Setup) {
            runCatching {
                getSelectedFileData()
                connectReader()
            }.onFailure { exception ->
                handleError("setupSearch", exception)
            }
        }
    }

    private fun getSelectedFileData() {
        _geigerUiState.fileData = excelUtils.readSpecificExcelFile(_geigerUiState.selectedFileName)
        updateGeigerState(rfidGeigerState = RfidGeigerState.Setup)
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
            updateGeigerState(rfidGeigerState = RfidGeigerState.Ready)
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

    fun performGeiger() {
        if (_geigerUiState.rfidGeigerState != RfidGeigerState.Reading &&
            _geigerUiState.rfidGeigerState != RfidGeigerState.Stop
        ) {
            runCatching {
                performMultiTagLocate()
            }.onFailure { exception ->
                handleError("RFID_performGeiger", exception)
            }
        }
    }

    private fun performMultiTagLocate() {
        multiTagSetup()
        updateGeigerState(rfidGeigerState = RfidGeigerState.Reading)
        rfidReader?.Actions?.MultiTagLocate?.perform()
    }

    private fun multiTagSetup() {
        val multiTagLocateTagMap = ArrayMap<String, String>().apply {
            _geigerUiState.fileData.forEach { tag ->
                put(tag.tagID, "-50")
            }
        }
        runCatching {
            rfidReader?.Actions?.MultiTagLocate?.purgeItemList()
            rfidReader?.Actions?.MultiTagLocate?.importItemList(multiTagLocateTagMap)
        }.onFailure { exception ->
            handleError("RFID_multiTagSetup", exception)
        }
    }

    fun pauseGeiger() {
        if (_geigerUiState.rfidGeigerState != RfidGeigerState.Pause) {
            runCatching {
                pauseMultiTagLocate()
            }.onFailure { exception ->
                handleError("RFID_pauseGeiger", exception)
            }
        }
    }

    private fun pauseMultiTagLocate() {
        updateGeigerState(rfidGeigerState = RfidGeigerState.Pause)
        rfidReader?.Actions?.MultiTagLocate?.stop()
    }

    fun retrySetupReader() {
        setupGeiger()
    }

    private fun updateGeigerState(rfidGeigerState: RfidGeigerState) {
        viewModelScope.launch {
            _geigerUiState.rfidGeigerState = rfidGeigerState
            Log.d("RFID_updateGeigerState", "🔵 Status: ${rfidGeigerState.name}")
        }
    }

    private fun handleError(title: String, exception: Throwable) {
        val message = when (exception) {
            is InvalidUsageException -> "⚠️ InvalidUsage: ${exception.message}"
            is IllegalStateException -> "⚠️ IllegalState: ${exception.message}"
            is OperationFailureException -> "⚠️ OperationFailure: ${exception.vendorMessage}"
            else -> "⚠️ Unexpected error: ${exception.message}"
        }
        updateGeigerState(rfidGeigerState = RfidGeigerState.Error)
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
            val myTags: Array<TagData>? = rfidReader?.Actions?.getMultiTagLocateTagInfo(100)
            myTags?.forEach { tagData ->
                if (tagData.isContainsMultiTagLocateInfo) {
                    Log.d("RFID_eventReadNotify", "${tagData.tagID} ${tagData.MultiTagLocateInfo.relativeDistance}")
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
                                //performInventory()
                            } catch (e: Exception) {
                                Log.e(
                                    "RFID_eventStatusNotify",
                                    "Error performing inventory: ${e.message}"
                                )
                            }
                        }

                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED -> {
                            try {
                                //pauseInventory()
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