package com.example.volkswagendemo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.volkswagendemo.ui.states.BatteryUiState
import com.example.volkswagendemo.ui.states.MutableBatteryUiState
import com.example.volkswagendemo.ui.states.RfidBatteryState
import com.zebra.rfid.api3.BatteryStatistics
import com.zebra.rfid.api3.ENUM_TRANSPORT
import com.zebra.rfid.api3.InvalidUsageException
import com.zebra.rfid.api3.OperationFailureException
import com.zebra.rfid.api3.RFIDReader
import com.zebra.rfid.api3.ReaderDevice
import com.zebra.rfid.api3.Readers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    private val _batteryUiState = MutableBatteryUiState()
    val batteryUiState: BatteryUiState = _batteryUiState

    private var readers = Readers(application.applicationContext, ENUM_TRANSPORT.SERVICE_USB)
    private var readerDevice: ReaderDevice? = null
    private var reader: RFIDReader? = null
    private var batteryStats = BatteryStatistics()

    init {
        connectReader()
    }

    fun retryConnection() {
        updateBatteryState(rfidBatteryState = RfidBatteryState.Connecting)
        connectReader()
    }

    private fun connectReader() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                var attempts = 0
                var availableRFIDReaderList: List<ReaderDevice>? = null
                while (attempts < 15) {
                    availableRFIDReaderList = readers.GetAvailableRFIDReaderList()

                    if (!availableRFIDReaderList.isNullOrEmpty()) {
                        Log.i("RFID_connectReader", "✅ Reader found in intent: $attempts")
                        break
                    }

                    Log.i(
                        "RFID_connectReader",
                        "⏱️ No readers available, retrying... ($attempts/15)"
                    )
                    attempts++
                    delay(1000)
                }

                if (availableRFIDReaderList.isNullOrEmpty()) {
                    throw IllegalStateException("No available readers found")
                }

                readerDevice = availableRFIDReaderList[0]
                reader = readerDevice?.rfidReader?.apply {
                    connect()
                    fetchBatteryStats(this)
                }

            }.onFailure { exception ->
                Log.e("RFID_connectReader", "⚠️ Error when connecting the RFID reader")
                handleException(exception)
            }
        }
    }

    private fun fetchBatteryStats(reader: RFIDReader) {
        runCatching {
            batteryStats = reader.Config.batteryStats
            _batteryUiState.manufactureDate = batteryStats.manufactureDate
            _batteryUiState.modelNumber = batteryStats.modelNumber
            _batteryUiState.batteryId = batteryStats.batteryId
            _batteryUiState.health = batteryStats.health
            _batteryUiState.cycleCount = batteryStats.cycleCount
            _batteryUiState.percentage = batteryStats.percentage
            _batteryUiState.temperature = batteryStats.temperature
            updateBatteryState(rfidBatteryState = RfidBatteryState.Ready)
        }.onFailure { exception ->
            Log.e("RFID_fetchBatteryStats", "⚠️ Error in obtaining battery information")
            handleException(exception)
        }
    }

    private fun updateBatteryState(rfidBatteryState: RfidBatteryState) {
        viewModelScope.launch {
            _batteryUiState.batteryState = rfidBatteryState
            Log.d("RFID_updateBatteryState", "🔵 Status: ${rfidBatteryState.name}")
        }
    }

    private fun handleException(exception: Throwable) {
        val message = when (exception) {
            is InvalidUsageException -> "⚠️ Invalid usage: ${exception.message}"
            is OperationFailureException -> "⚠️ Operation failed: ${exception.vendorMessage}"
            is IllegalStateException -> "⚠️ Illegal state: ${exception.message}"
            is NullPointerException -> "⚠️ Null pointer exception: ${exception.message}"
            else -> "⚠️ Unexpected error: ${exception.message}"
        }
        updateBatteryState(rfidBatteryState = RfidBatteryState.Error)
        Log.e("RFID_handleException", message)
    }

}