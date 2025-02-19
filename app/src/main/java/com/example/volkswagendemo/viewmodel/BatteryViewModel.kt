package com.example.volkswagendemo.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(
    application: Application
) : ViewModel() {

    private val _batteryStatus = MutableStateFlow("Connecting")
    val batteryStatus: StateFlow<String> = _batteryStatus.asStateFlow()

    private var readers = Readers(application.applicationContext, ENUM_TRANSPORT.SERVICE_USB)
    private var readerDevice: ReaderDevice? = null
    private var reader: RFIDReader? = null
    private var batteryStats = BatteryStatistics()

    private val _batteryInfo = MutableStateFlow(List(7) { "0" })
    val batteryInfo = _batteryInfo.asStateFlow()

    init {
        connectReader()
    }

    fun retryConnection() {
        updateBatteryStatus("Connecting")
        connectReader()
    }

    private fun updateBatteryStatus(status: String) {
        viewModelScope.launch {
            _batteryStatus.value = status
            Log.d("RFID_updateBatteryStatus", "🔵 Status: $status")
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
                    fetchBatteryStats(this)

                }

            }.onFailure { exception ->
                Log.e("RFID_connectReader", "⚠️ Error al conectar el lector RFID")
                handleException(exception)
            }
        }
    }

    private fun fetchBatteryStats(reader: RFIDReader) {

        runCatching {
            batteryStats = reader.Config.batteryStats
            updateBatteryStatus("Ready")
        }.onFailure { e ->
            handleException(e)
        }

        _batteryInfo.value = listOf(
            batteryStats.manufactureDate,
            batteryStats.modelNumber,
            batteryStats.batteryId,
            "${batteryStats.health}%",
            "${batteryStats.cycleCount}",
            "${batteryStats.percentage}",
            "${batteryStats.temperature}°C"
        )
    }

    private fun handleException(exception: Throwable) {
        val message = when (exception) {
            is InvalidUsageException -> "⚠️ Invalid usage: ${exception.message}"
            is OperationFailureException -> "⚠️ Operation failed: ${exception.vendorMessage}"
            is IllegalStateException -> "⚠️ Illegal state: ${exception.message}"
            is NullPointerException -> "⚠️ Null pointer exception: ${exception.message}"
            else -> "⚠️ Unexpected error: ${exception.message}"
        }
        updateBatteryStatus("Error")
        Log.e("RFID_handleException", message)
    }

}