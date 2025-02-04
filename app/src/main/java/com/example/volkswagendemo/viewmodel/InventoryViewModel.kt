package com.example.volkswagendemo.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.core.dataclass.TagDataInfo
import com.zebra.rfid.api3.ACCESS_OPERATION_CODE
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS
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
import com.zebra.rfid.api3.TagData
import com.zebra.rfid.api3.TriggerInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.withLock

@HiltViewModel
class InventoryViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    private val _inventoryStatus = MutableStateFlow("Connecting")
    val inventoryStatus: StateFlow<String> = _inventoryStatus.asStateFlow()

    private val _tagDataList = mutableListOf<TagDataInfo>()
    private val _tagsFlow = MutableStateFlow<List<TagDataInfo>>(emptyList())
    val tagsFlow: StateFlow<List<TagDataInfo>> = _tagsFlow.asStateFlow()

    private val readers = Readers(application.applicationContext, ENUM_TRANSPORT.SERVICE_USB)
    private var readerDevice: ReaderDevice? = null
    private var reader: RFIDReader? = null
    private val eventHandler = EventHandler()

    init {
        connectReader()
    }

    private fun updateInventoryStatus(status: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _inventoryStatus.value = status
        }
    }

    private fun connectReader() {

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val availableRFIDReaderList = readers.GetAvailableRFIDReaderList()
                Log.i(
                    "RFID_connectReader",
                    "Available readers: ${availableRFIDReaderList?.size ?: 0}"
                )
                if (!availableRFIDReaderList.isNullOrEmpty()) {
                    readerDevice = availableRFIDReaderList[0]
                    reader = readerDevice?.rfidReader?.apply {
                        connect()
                        configureReader(this)
                    }
                }
            }.onFailure {
                handleException(it)
            }
        }
    }

    private fun configureReader(reader: RFIDReader) {
        runCatching {
            setupAntennaConfig(reader)
            setupTriggerConfig(reader)
            setupEventConfig(reader)
        }.onSuccess {
            updateInventoryStatus("Ready")
            Log.i("RFID_configureReader", "RFID reader configured and ready!")
        }.onFailure {
            handleException(it)
        }
    }

    private fun setupAntennaConfig(reader: RFIDReader) {
        val antennaConfig = reader.Config.Antennas.getAntennaRfConfig(1)
        antennaConfig.transmitPowerIndex = 300
        antennaConfig.setrfModeTableIndex(0)
        antennaConfig.tari = 0
        reader.Config.Antennas.setAntennaRfConfig(1, antennaConfig)
    }

    private fun setupTriggerConfig(reader: RFIDReader) {
        val triggerInfo = TriggerInfo().apply {
            StartTrigger.triggerType = START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE
            StopTrigger.triggerType = STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE
        }
        reader.Config.apply {
            setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true)
            setUniqueTagReport(false)
            setLedBlinkEnable(true)
            startTrigger = triggerInfo.StartTrigger
            stopTrigger = triggerInfo.StopTrigger
            beeperVolume = BEEPER_VOLUME.LOW_BEEP
        }
    }

    private fun setupEventConfig(reader: RFIDReader) {
        reader.Events.apply {
            addEventsListener(eventHandler)
            setHandheldEvent(true)
            setTagReadEvent(true)
            setAttachTagDataWithReadEvent(false)
        }
    }

    fun readOp(tagId: String): String {

        val tagAccess = TagAccess()
        val readAccessParams = tagAccess.ReadAccessParams().apply {
            accessPassword = 0
            count = 9
            memoryBank = MEMORY_BANK.MEMORY_BANK_USER
            offset = 4
        }

        return runCatching {
            val tagData = reader?.Actions?.TagAccess?.readWait(tagId, readAccessParams, null)

            tagData?.takeIf {
                it.opStatus == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS &&
                        it.opCode == ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ
            }?.also {
                Log.i("RFID_readOp", "Successful read operation")
            }?.memoryBankData.orEmpty()
        }.getOrElse {
            handleException(it)
            Log.e("RFID_readOp", "Error reading tag: ${it.message}")
            ""
        }

    }

    private fun handleException(exception: Throwable) {
        val message = when (exception) {
            is InvalidUsageException -> "Invalid usage: ${exception.message}"
            is OperationFailureException -> "Operation failed: ${exception.vendorMessage}"
            else -> "Unexpected error: ${exception.message}"
        }
        updateInventoryStatus("Error")
        Log.e("RFID_handleException", message)
    }

    override fun onCleared() {
        super.onCleared()
        reader?.let {
            try {
                it.disconnect()
                Log.i("RFID_onCleared", "Reader disconnected")
            } catch (e: Exception) {
                Log.e("RFID_onCleared", "Error disconnecting reader: ${e.message}")
            }
        }
    }

    inner class EventHandler : RfidEventsListener {

        override fun eventReadNotify(e: RfidReadEvents?) {
            reader?.Actions?.getReadTags(100)?.forEach { tag ->
                val hexTagID = tag.tagID
                val asciiTagID = hexToAscii(hexTagID)
                Log.d(TAG, "Tag ID (Hex): $hexTagID -> (ASCII): $asciiTagID")
                val repuve = asciiTagID.take(8)
                if (repuve.all { it.isDigit() } && _tagDataList.none { it.asciiTag == repuve }) {
                    val userMemoryBank =
                        hexToAscii(readOp(hexTagID).dropLast(2)) // Leer memoria del tag
                    val tagDataInfo = TagDataInfo(hexTagID, repuve, userMemoryBank)
                    Log.d(
                        TAG,
                        "Tag Data Info: ${tagDataInfo.hexTag} - ${tagDataInfo.asciiTag} - ${tagDataInfo.memoryData}"
                    )
                    _tagDataList.add(tagDataInfo) // Agregamos el nuevo tag a la lista
                    _tagsFlow.value =
                        _tagDataList.toMutableList().reversed() // Actualizamos el flujo con la nueva lista
                }
            }
        }

        /*override fun eventReadNotify(e: RfidReadEvents?) {
            reader?.Actions?.getReadTags(100)?.forEach { tag ->
                val hexTagID = tag.tagID
                val asciiTagID = hexToAscii(hexTagID)
                val repuve = asciiTagID.take(8)

                if (repuve.all { it.isDigit() } && _tagDataList.none { it.asciiTag == repuve }) {
                    val rawMemoryData = readOp(hexTagID).dropLast(2)
                    val userMemoryBank = hexToAscii(rawMemoryData)

                    if (userMemoryBank.isNotBlank()) {
                        val tagDataInfo = TagDataInfo(hexTagID, repuve, userMemoryBank)
                        Log.d(
                            "RFID_eventReadNotify",
                            "Tag Data Info: ${tagDataInfo.hexTag} - ${tagDataInfo.asciiTag} - ${tagDataInfo.memoryData}"
                        )

                        _tagDataList.add(tagDataInfo)
                        _tagsFlow.value = _tagDataList.toMutableList().reversed()
                    }
                }
            }
        }*/

        /*override fun eventStatusNotify(rfidStatusEvents: RfidStatusEvents?) {
            rfidStatusEvents?.let { events ->
                when (events.StatusEventData.statusEventType) {
                    STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT -> {
                        when (events.StatusEventData.HandheldTriggerEventData.handheldEvent) {
                            HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED -> {
                                inventoryStatus = "Reading"
                                reader?.Actions?.Inventory?.perform()
                            }

                            HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED -> {
                                inventoryStatus = "Stopped"
                                reader?.Actions?.Inventory?.stop()
                            }

                            else -> {}
                        }
                    }

                    else -> {}
                }
            }
        }*/

        override fun eventStatusNotify(rfidStatusEvents: RfidStatusEvents?) {
            rfidStatusEvents?.let { events ->
                val statusEventType = events.StatusEventData.statusEventType
                Log.d(TAG, "Status Notification: $statusEventType")

                if (statusEventType == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {

                    val handheldEvent =
                        events.StatusEventData.HandheldTriggerEventData.handheldEvent

                    when (handheldEvent) {
                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED -> {
                            try {
                                updateInventoryStatus("Reading")
                                reader?.Actions?.Inventory?.perform()
                            } catch (e: Exception) {
                                Log.e(TAG, "Error performing inventory: ${e.message}")
                            }
                        }

                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED -> {
                            try {
                                updateInventoryStatus("Stopped")
                                reader?.Actions?.Inventory?.stop()
                            } catch (e: Exception) {
                                Log.e(TAG, "Error stopping inventory: ${e.message}")
                            }
                        }

                        else -> {
                            Log.d(TAG, handheldEvent.toString())
                        }
                    }
                }
            }
        }

    }

    private fun hexToAscii(hex: String): String {
        return hex.chunked(2).mapNotNull { it.toIntOrNull(16)?.toChar() }.joinToString("")
    }

}