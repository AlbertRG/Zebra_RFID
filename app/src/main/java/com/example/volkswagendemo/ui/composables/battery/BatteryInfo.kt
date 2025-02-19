package com.example.volkswagendemo.ui.composables.battery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volkswagendemo.R
import com.example.volkswagendemo.viewmodel.BatteryViewModel

@Composable
fun BatteryInfo(
    batteryViewModel: BatteryViewModel
) {

    val batteryInfo by batteryViewModel.batteryInfo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp)
    ) {
        BatteryPercentage(batteryInfo[5].toInt())
        Spacer(modifier = Modifier.height(16.dp))
        BatteryInfoCard(
            icon = R.drawable.battery_profile,
            title = "Detalles Generales",
            infoList = listOf(
                "Fecha de manufactura" to batteryInfo[0],
                "Número de modelo" to batteryInfo[1],
                "ID de batería" to batteryInfo[2]
            )
        )
        BatteryInfoCard(
            icon = R.drawable.health_metrics,
            title = "Indicadores de Salud",
            infoList = listOf(
                "Estado de salud" to batteryInfo[3],
                "Ciclos de carga consumidos" to batteryInfo[4]
            )
        )
        BatteryInfoCard(
            icon = R.drawable.thermostat,
            title = "Temperatura de Funcionamiento",
            infoList = listOf(
                "Actual" to batteryInfo[6]
            )
        )
    }
}

@Composable
fun BatteryPercentage(
    percentage: Int
) {
    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = percentage.toString(),
            color = Color(0xFF05A6E1),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "%",
            modifier = Modifier
                .padding(bottom = 4.dp),
            color = Color(0xFF05A6E1),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
    LinearProgressIndicator(
        progress = { percentage / 100f },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        color = Color(0xFF05A6E1),
        trackColor = Color.LightGray,
        gapSize = (-5).dp
    )
}