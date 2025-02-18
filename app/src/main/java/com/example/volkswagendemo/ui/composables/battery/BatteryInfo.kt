package com.example.volkswagendemo.ui.composables.battery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.volkswagendemo.R
import com.example.volkswagendemo.viewmodel.BatteryViewModel

@Composable
fun BatteryInfo(
    batteryViewModel: BatteryViewModel
) {

    val batteryInfo by batteryViewModel.batteryInfo.collectAsState()
    val batteryPercentage = batteryInfo[5].replace("%", "").toInt()

    val batteryIcon = when {
        batteryPercentage in 91..100 -> R.drawable.battery_100
        batteryPercentage in 60..90 -> R.drawable.battery_075
        batteryPercentage in 41..59 -> R.drawable.battery_050
        batteryPercentage in 10..40 -> R.drawable.battery_025
        else -> R.drawable.battery_000
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(batteryIcon),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp),
                tint = Color(0xFF05A6E1)
            )
        }
        BatteryInfoTitle(
            "Estado de la batería"
        )
        BatteryInfoItem(
            "Porcentaje de carga",
            batteryInfo[5]
        )
        BatteryInfoTitle(
            "Información sobre la batería"
        )
        BatteryInfoItem(
            "Manufacture Date",
            batteryInfo[0]
        )
        BatteryInfoItem(
            "Fecha fabricación",
            batteryInfo[1]
        )
        BatteryInfoItem(
            "Batería ID",
            batteryInfo[2]
        )
        BatteryInfoTitle(
            "Estadísticas de duración de la batería"
        )
        BatteryInfoItem(
            "Estado de salud",
            batteryInfo[3]
        )
        BatteryInfoItem(
            "Ciclos de carga consumidos",
            batteryInfo[4]
        )
        BatteryInfoTitle(
            "Temperatura de la batería"
        )
        BatteryInfoItem(
            "Actual",
            batteryInfo[6]
        )

    }
}