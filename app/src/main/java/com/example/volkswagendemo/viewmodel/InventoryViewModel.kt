package com.example.volkswagendemo.viewmodel

import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.data.TagData
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    private val context = application.applicationContext
    private val _inventoryStatus = MutableStateFlow("Connecting")
    val inventoryStatus: StateFlow<String> = _inventoryStatus.asStateFlow()

    private var readers = Readers(application.applicationContext, ENUM_TRANSPORT.SERVICE_USB)
    private var readerDevice: ReaderDevice? = null
    private var reader: RFIDReader? = null
    private val eventHandler = EventHandler()

    private val _tagDataList = mutableListOf<TagData>()
    private val _tagsFlow = MutableStateFlow<List<TagData>>(emptyList())
    val tagsFlow: StateFlow<List<TagData>> = _tagsFlow.asStateFlow()

    private val _filesFlow = MutableStateFlow<List<String>>(emptyList())
    val filesFlow: StateFlow<List<String>> = _filesFlow.asStateFlow()

    private val _showFileDialog = MutableStateFlow(false)
    var showFileDialog: StateFlow<Boolean> = _showFileDialog.asStateFlow()

    private val _vinFlow = MutableStateFlow<List<String>>(emptyList())
    val vinFlow: StateFlow<List<String>> = _vinFlow.asStateFlow()

    private val _fileName = MutableStateFlow("")
    val fileName: StateFlow<String> = _fileName.asStateFlow()

    init {
        connectReader()
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

    private fun inventoryStop() {
        reader?.Actions?.TagAccess?.stopAccess()
        updateInventoryStatus("Stopped")
    }

    fun finishInventory() {
        if (_inventoryStatus.value != "Resume") {
            try {
                inventoryResume()
            } catch (e: Exception) {
                Log.e("RFID_finishInventory", "Error finishing inventory: ${e.message}")
                updateInventoryStatus("Error")
            }
        }
    }

    private fun inventoryResume() {
        writeExcelFile(context, tagsFlow.value, "taller123")
        indexExcelFiles(context, "taller123")
        updateInventoryStatus("Resume")
    }

    fun restartInventory() {
        if (_inventoryStatus.value != "Ready") {
            try {
                inventoryRestart()
            } catch (e: Exception) {
                Log.e("RFID_restartInventory", "⚠️ Error restarting inventory: ${e.message}")
                updateInventoryStatus("Error")
            }
        }
    }

    private fun inventoryRestart() {
        _tagDataList.clear()
        _tagsFlow.value = emptyList()
        _filesFlow.value = emptyList()
        updateInventoryStatus("Ready")
    }

    fun retryConnection() {
        updateInventoryStatus("Connecting")
        connectReader()
    }

    private fun updateInventoryStatus(status: String) {
        viewModelScope.launch {
            _inventoryStatus.value = status
            Log.d("RFID_updateInventoryStatus", "🔵 Status: $status")
        }
    }

    private fun connectReader() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                var attempts = 0
                var availableRFIDReaderList: List<ReaderDevice>? = null

                while (attempts < 15) {
                    availableRFIDReaderList = readers.GetAvailableRFIDReaderList()

                    if (!availableRFIDReaderList.isNullOrEmpty()) {
                        Log.i("RFID_connectReader", "✅ Lector encontrado en intento $attempts")
                        break
                    }

                    Log.i(
                        "RFID_connectReader",
                        "⏱️ No hay lectores disponibles, reintentando... ($attempts/15)"
                    )
                    attempts++
                    delay(1000)
                }

                if (availableRFIDReaderList.isNullOrEmpty()) {
                    throw IllegalStateException("No se encontraron lectores disponibles")
                }

                readerDevice = availableRFIDReaderList[0]
                reader = readerDevice?.rfidReader?.apply {
                    connect()
                    configureReader(this)
                }

            }.onFailure { exception ->
                Log.e("RFID_connectReader", "⚠️ Error al conectar el lector RFID")
                handleException(exception)
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
            Log.i("RFID_configureReader", "✅ RFID reader configured and ready!")
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
            is InvalidUsageException -> "⚠️ Invalid usage: ${exception.message}"
            is OperationFailureException -> "⚠️ Operation failed: ${exception.vendorMessage}"
            is IllegalStateException -> "⚠️ Illegal state: ${exception.message}"
            else -> "⚠️ Unexpected error: ${exception.message}"
        }
        updateInventoryStatus("Error")
        Log.e("RFID_handleException", message)
    }

    override fun onCleared() {
        super.onCleared()
        reader?.let {
            try {
                it.disconnect()
                reader = null
                readers.Dispose()
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
                        _tagDataList.add(tagData)
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

    private fun hexToAscii(hex: String): String {
        return hex.chunked(2).mapNotNull { it.toIntOrNull(16)?.toChar() }.joinToString("")
    }

    private fun getActualDate(): String =
        SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault()).format(Date())

    private fun writeExcelFile(context: Context, tagsFlow: List<TagData>, workshop: String) {
        val timestamp = getActualDate()
        val fileName = "$workshop $timestamp catalogoln.xls"
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("Hoja de datos")

        listOf("Localization", timestamp, "Chasises").forEachIndexed { index, text ->
            sheet.createRow(index + 1).createCell(0).setCellValue(text)
        }

        tagsFlow.forEachIndexed { index, tag ->
            sheet.createRow(index + 4).createCell(0).setCellValue(tag.vin)
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/Catalogos/$workshop")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
            contentValues
        )

        uri?.let {
            runCatching {
                resolver.openOutputStream(it)?.use { fos ->
                    workbook.use { wb -> wb.write(fos) }
                }
                Log.d("Excel", "📁 Archivo guardado en: $fileName")
            }.onFailure { e ->
                Log.e("Excel", "⚠️ Error al guardar el archivo: ${e.message}")
            }
        } ?: run {
            Log.e("Excel", "⚠️ Error al obtener URI para el archivo")
        }

    }

    private fun indexExcelFiles(context: Context, workshop: String) {
        val folderPath = "Documents/Catalogos/$workshop/"
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_MODIFIED
        )
        val selection =
            "${MediaStore.Files.FileColumns.RELATIVE_PATH} = ? AND ${MediaStore.Files.FileColumns.MIME_TYPE} = ?"
        val selectionArgs = arrayOf(folderPath, "application/vnd.ms-excel")
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"
        val excelFiles = mutableListOf<String>()

        context.contentResolver.query(
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
            projection, selection, selectionArgs, sortOrder
        )?.use { cursor ->
            val nameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val fileName = cursor.getString(nameColumn)
                Log.d("Excel", "📂 Archivo encontrado: $fileName")

                if (fileName.endsWith(".xls", ignoreCase = true)) {
                    //Log.d("Excel", "📁 Archivo detectado: $fileName")
                    excelFiles.add(fileName)
                }
            }
        }
        _filesFlow.value = excelFiles
        Log.d("Excel", "📂 Archivos XLS encontrados: ${_filesFlow.value.size}")
    }

    fun readSpecificExcelFile(fileName: String) {
        _vinFlow.value = readExcelFile(context, fileName)
        _fileName.value = fileName
        _showFileDialog.value = true
    }

    fun closeFileDialog() {
        _showFileDialog.value = false
    }

    private fun readExcelFile(context: Context, fileName: String): List<String> {
        val vinsList = mutableListOf<String>()
        val projection = arrayOf(MediaStore.Downloads._ID)
        val selection = "${MediaStore.Downloads.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        context.contentResolver.query(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            projection, selection, selectionArgs, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndex(MediaStore.Downloads._ID)
                val fileId = cursor.getLong(idColumn)
                val fileUri =
                    ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, fileId)

                runCatching {
                    context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                        HSSFWorkbook(inputStream).use { workbook ->
                            val sheet = workbook.getSheetAt(0)

                            for (rowIndex in 4 until sheet.physicalNumberOfRows + 1) {
                                val row = sheet.getRow(rowIndex)
                                row?.getCell(0)?.toString()?.trim()?.let { vin ->
                                    if (vin.isNotEmpty()) {
                                        vinsList.add(vin)
                                    }
                                }
                            }
                        }
                    }
                }.onFailure { e ->
                    Log.e("Excel", "⚠️ Error al leer el archivo: ${e.message}")
                }
            }
        }
        return vinsList
    }

}