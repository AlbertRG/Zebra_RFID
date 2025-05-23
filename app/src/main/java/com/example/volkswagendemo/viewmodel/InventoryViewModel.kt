package com.example.volkswagendemo.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.data.models.RfidData
import com.example.volkswagendemo.domain.usecase.settings.GetSettingsUseCase
import com.example.volkswagendemo.domain.usecase.workshop.GetWorkshopUseCase
import com.example.volkswagendemo.ui.states.InventoryUiState
import com.example.volkswagendemo.ui.states.MutableInventoryUiState
import com.example.volkswagendemo.ui.states.RfidInventoryState
import com.example.volkswagendemo.utils.ConversionUtils
import com.example.volkswagendemo.utils.ExcelUtils
import com.google.common.util.concurrent.ListenableFuture
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    application: Application,
    private val getWorkshopUseCase: GetWorkshopUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val conversionUtils: ConversionUtils,
    private val excelUtils: ExcelUtils
) : ViewModel() {

    private val context = application.applicationContext

    private val _inventoryUiState = MutableInventoryUiState()
    val inventoryUiState: InventoryUiState = _inventoryUiState

    private var readers = Readers(application.applicationContext, ENUM_TRANSPORT.SERVICE_USB)
    private var readerDevice: ReaderDevice? = null
    private var rfidReader: RFIDReader? = null
    private val eventHandler = EventHandler()
    private val _scannedTagsList = mutableListOf<RfidData>()

    private val _imageCapture = MutableLiveData<ImageCapture?>()
    val imageCapture: LiveData<ImageCapture?> = _imageCapture

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(application)

    init {
        getWorkshop()
        getRfidSettings()
    }

    private fun getWorkshop() {
        viewModelScope.launch {
            runCatching {
                getWorkshopUseCase().first()
            }.onSuccess { workshop ->
                workshop.let {
                    _inventoryUiState.workshop = workshop
                }
            }.onFailure {
                handleError("RFID_getWorkshop", it)
            }
        }
    }

    private fun getRfidSettings() {
        viewModelScope.launch {
            runCatching {
                getSettingsUseCase().first()
            }.onSuccess { settings ->
                settings?.let {
                    _inventoryUiState.settings.antennaPower = settings.antennaPower
                    _inventoryUiState.settings.beeperVolume = settings.beeperVolume
                }
                connectReader()
            }.onFailure { exception ->
                handleError("RFID_getRfidSettings", exception)
            }
        }
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
            updateInventoryState(RfidInventoryState.Ready)
            Log.i("RFID_configureReader", "✅ RFID reader configured and ready!")
        }.onFailure { exception ->
            handleError("RFID_configureReader", exception)
        }
    }

    private fun configureAntenna(rfidReader: RFIDReader) {
        val antennaConfig = rfidReader.Config.Antennas.getAntennaRfConfig(1)
        antennaConfig.transmitPowerIndex = _inventoryUiState.settings.antennaPower.toInt()
        //antennaConfig.setrfModeTableIndex(0)
        //antennaConfig.tari = 0
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
            beeperVolume = when (_inventoryUiState.settings.beeperVolume) {
                0 -> BEEPER_VOLUME.QUIET_BEEP
                1 -> BEEPER_VOLUME.LOW_BEEP
                2 -> BEEPER_VOLUME.MEDIUM_BEEP
                else -> BEEPER_VOLUME.HIGH_BEEP
            }
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
        if (_inventoryUiState.rfidState == RfidInventoryState.Ready ||
            _inventoryUiState.rfidState == RfidInventoryState.Pause
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
            if (_inventoryUiState.isDevelopMode) {
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
        updateInventoryState(RfidInventoryState.Reading)
        rfidReader?.Actions?.TagAccess?.readEvent(readAccessParams, null, null)
    }

    fun pauseInventory() {
        if (_inventoryUiState.rfidState == RfidInventoryState.Reading) {
            runCatching {
                pauseInventoryRead()
            }.onFailure { exception ->
                handleError("RFID_pauseInventory", exception)
            }
        }
    }

    private fun pauseInventoryRead() {
        updateInventoryState(RfidInventoryState.Pause)
        rfidReader?.Actions?.TagAccess?.stopAccess()
    }

    fun reportInventory() {
        if (_inventoryUiState.rfidState == RfidInventoryState.Pause) {
            runCatching {
                updateInventoryState(RfidInventoryState.Report)
            }.onFailure { exception ->
                handleError("RFID_reportInventory", exception)
            }
        }
    }

    fun hasCamaraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun initCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val executor = ContextCompat.getMainExecutor(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val imageCapture = ImageCapture.Builder().build()
            _imageCapture.value = imageCapture

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("CameraViewModel", "Error al inicializar la cámara", e)
            }
        }, executor)
    }

    fun capturePhoto() {
        val imageCapture = _imageCapture.value ?: return
        val photoFile = File(context.externalCacheDir, "photo.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("CameraViewModel", "Foto guardada en: ${photoFile.absolutePath}")
                }
                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraViewModel", "Error al tomar foto", exception)
                }
            }
        )
    }

    fun stopInventory() {
        if (_inventoryUiState.rfidState == RfidInventoryState.Pause) {
            runCatching {
                stopInventorySession()
            }.onFailure { exception ->
                handleError("RFID_stopInventory", exception)
            }
        }
    }

    private fun stopInventorySession() {
        excelUtils.writeExcelFile(_inventoryUiState.scannedTags, _inventoryUiState.workshop)
        _inventoryUiState.filesList = excelUtils.indexExcelFiles(_inventoryUiState.workshop)
        updateInventoryState(RfidInventoryState.Stop)
    }

    fun openFileDialog(file: String) {
        _inventoryUiState.selectedFileName = file
        _inventoryUiState.fileData = excelUtils.readSpecificExcelFile(file)
        _inventoryUiState.isFileDialogVisible = true
    }

    fun closeFileDialog() {
        _inventoryUiState.isFileDialogVisible = false
    }

    fun resetInventoryState() {
        if (_inventoryUiState.rfidState == RfidInventoryState.Stop) {
            runCatching {
                resetInventory()
            }.onFailure { exception ->
                handleError("RFID_resetInventory", exception)
            }
        }
    }

    private fun resetInventory() {
        _scannedTagsList.clear()
        _inventoryUiState.scannedTags = emptyList()
        _inventoryUiState.filesList = emptyList()
        updateInventoryState(RfidInventoryState.Ready)
    }

    fun retryReaderConnection() {
        updateInventoryState(RfidInventoryState.Connecting)
        connectReader()
    }

    private fun updateInventoryState(rfidState: RfidInventoryState) {
        viewModelScope.launch {
            _inventoryUiState.rfidState = rfidState
            Log.d("RFID_updateInventoryState", "🔵 Status: ${rfidState.name}")
        }
    }

    private fun handleError(title: String, exception: Throwable) {
        val message = when (exception) {
            is InvalidUsageException -> "⚠️ InvalidUsage: ${exception.message}"
            is IllegalStateException -> "⚠️ IllegalState: ${exception.message}"
            is OperationFailureException -> "⚠️ OperationFailure: ${exception.vendorMessage}"
            else -> "⚠️ Unexpected error: ${exception.message}"
        }
        updateInventoryState(RfidInventoryState.Error)
        Log.e(title, message)
    }

    override fun onCleared() {
        super.onCleared()
        rfidReader?.let {
            runCatching {
                it.disconnect()
                rfidReader = null
                readers.Dispose()
                Log.i("RFID_onCleared", "Reader disconnected")
            }.onFailure { e ->
                Log.e("RFID_onCleared", "Error disconnecting reader: ${e.message}")
            }
        }
    }

    inner class EventHandler : RfidEventsListener {

        override fun eventReadNotify(e: RfidReadEvents?) {
            rfidReader?.Actions?.getReadTags(100)?.forEach { tag ->

                if (_scannedTagsList.none { it.tagID == tag.tagID }) {

                    val tagID = tag.tagID
                    val repuve = conversionUtils.hexToAscii(tag.tagID.take(16))
                    val vin = conversionUtils.hexToAscii(tag.memoryBankData.take(34))

                    if (repuve == null || vin == null) {
                        Log.e(
                            "RFID_eventReadNotify",
                            "⚠️ Conversion failed (repuve=$repuve, vin=$vin)"
                        )
                        return@forEach
                    }

                    if (repuve.all { it.isDigit() } && vin.all { it.isLetterOrDigit() }) {
                        val tagData =
                            RfidData(
                                tagID = tagID,
                                repuve = repuve,
                                vin = vin
                            )
                        Log.d(
                            "RFID_eventReadNotify",
                            "Tag Data Info: ${tagData.repuve} - ${tagData.vin}"
                        )
                        _scannedTagsList.add(tagData)
                        _inventoryUiState.scannedTags =
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