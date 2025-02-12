package com.example.volkswagendemo.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.core.dataclass.TagDataInfo
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    val context = application.applicationContext
    private val _inventoryStatus = MutableStateFlow("Connecting")
    val inventoryStatus: StateFlow<String> = _inventoryStatus.asStateFlow()

    private val _tagDataList = mutableListOf<TagDataInfo>()
    private val _tagsFlow = MutableStateFlow<List<TagDataInfo>>(emptyList())
    val tagsFlow: StateFlow<List<TagDataInfo>> = _tagsFlow.asStateFlow()

    private var readers = Readers(application.applicationContext, ENUM_TRANSPORT.SERVICE_USB)
    private var readerDevice: ReaderDevice? = null
    private var reader: RFIDReader? = null
    private val eventHandler = EventHandler()

    init {
        connectReader()
    }

    private fun updateInventoryStatus(status: String) {
        viewModelScope.launch {
            _inventoryStatus.value = status
            Log.d("RFID_updateInventoryStatus", "Status: $status")
        }
    }

    fun startInventory() {
        if (_inventoryStatus.value != "Reading") {
            try {
                inventoryPerform()
            } catch (e: Exception) {
                Log.e("RFID_startInventory", "Error starting inventory: ${e.message}")
                updateInventoryStatus("Error")
            }
        }
    }

    fun stopInventory() {
        if (_inventoryStatus.value != "Stopped") {
            try {
                inventoryStop()
            } catch (e: Exception) {
                Log.e("RFID_stopInventory", "Error stopping inventory: ${e.message}")
                updateInventoryStatus("Error")
            }
        }
    }

    fun finishInventory() {
        try {
            inventoryResume()
        } catch (e: Exception) {
            Log.e("RFID_finishInventory", "Error finishing inventory: ${e.message}")
            updateInventoryStatus("Error")
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
            setUniqueTagReport(true)
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
            setAttachTagDataWithReadEvent(true)
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
                inventoryStop()
                it.disconnect()
                reader = null;
                readers.Dispose();
                Log.i("RFID_onCleared", "Reader disconnected")
            } catch (e: Exception) {
                Log.e("RFID_onCleared", "Error disconnecting reader: ${e.message}")
            }
        }
    }

    inner class EventHandler : RfidEventsListener {

        override fun eventReadNotify(e: RfidReadEvents?) {
            reader?.Actions?.getReadTags(100)?.forEach { tag ->

                if (_tagDataList.none { it.controlData == tag.memoryBankData }) {

                    val rawMemoryData = tag.memoryBankData
                    val repuve = hexToAscii(tag.tagID).take(8)
                    val vin = hexToAscii(tag.memoryBankData).take(16)

                    if (repuve.all { it.isDigit() }) {
                        val tagDataInfo =
                            TagDataInfo(
                                repuve = repuve,
                                vin = vin,
                                controlData = rawMemoryData
                            )
                        Log.d(
                            "RFID_eventReadNotify",
                            "Tag Data Info: ${tagDataInfo.repuve} - ${tagDataInfo.vin}"
                        )
                        _tagDataList.add(tagDataInfo)
                        _tagsFlow.value = _tagDataList.toList().distinctBy { it.repuve }.reversed()
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
                                inventoryPerform()
                            } catch (e: Exception) {
                                Log.e(
                                    "RFID_eventStatusNotify",
                                    "Error performing inventory: ${e.message}"
                                )
                            }
                        }

                        HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED -> {
                            try {
                                inventoryStop()
                            } catch (e: Exception) {
                                Log.e(
                                    "RFID_eventStatusNotify",
                                    "Error stopping inventory: ${e.message}"
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

    private fun inventoryPerform() {

        val tagAccess = TagAccess()

        // 128 BITS 32 CHARS - 256 BITS 64 CHARS
        val readAccessParams = tagAccess.ReadAccessParams().apply {
            accessPassword = 0
            count = 8 //8 NIBBLES 32 CHARS - 9 NIBBLES 36 CHARS
            memoryBank = MEMORY_BANK.MEMORY_BANK_USER
            offset = 0 //0 NIBBLES - 4 NIBBLES 16 CHARS
        }

        updateInventoryStatus("Reading")
        reader?.Actions?.TagAccess?.readEvent(readAccessParams, null, null)

    }

    private fun inventoryStop() {
        updateInventoryStatus("Stopped")
        reader?.Actions?.Inventory?.stop()
    }

    private fun inventoryResume() {
        createExcelFile(context, tagsFlow.value)
        updateInventoryStatus("Resume")
        reader?.Actions?.Inventory?.stop()
    }

    private fun hexToAscii(hex: String): String {
        return hex.chunked(2).mapNotNull { it.toIntOrNull(16)?.toChar() }.joinToString("")
    }

    private fun createExcelFile(context: Context, tagsFlow: List<TagDataInfo>) {
        val workshop = "taller123"
        val timestamp = getActualDate()
        val fileName = "$workshop $timestamp.xls"

        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("Hoja de datos")

        var row = sheet.createRow(1)
        row.createCell(0).setCellValue("Localization")

        row = sheet.createRow(2)
        row.createCell(0).setCellValue(timestamp)

        row = sheet.createRow(3)
        row.createCell(0).setCellValue("Chasises")

        tagsFlow.forEachIndexed { index, tag ->
            row = sheet.createRow(index + 4)
            row.createCell(0).setCellValue(tag.vin)
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/vnd.ms-excel")
            put(MediaStore.Downloads.RELATIVE_PATH, "Download/Catalogos/${workshop}")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            try {
                resolver.openOutputStream(it)?.use { fos ->
                    workbook.write(fos)
                }
                Log.d("Excel", "📁 Archivo guardado en: $fileName")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Excel", "⚠️ Error al guardar el archivo: ${e.message}")
            } finally {
                workbook.close()
            }
        }

    }

    private fun getActualDate(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

}