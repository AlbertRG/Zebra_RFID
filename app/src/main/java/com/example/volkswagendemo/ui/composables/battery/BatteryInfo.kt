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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volkswagendemo.R
import com.example.volkswagendemo.viewmodel.BatteryViewModel

@Composable
fun BatteryInfo(
    batteryViewModel: BatteryViewModel
) {
    val batteryUiState = batteryViewModel.batteryUiState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp)
    ) {
        BatteryPercentage(batteryUiState.percentage)
        Spacer(modifier = Modifier.height(16.dp))
        BatteryInfoCard(
            icon = R.drawable.battery_profile,
            title = stringResource(R.string.general_details),
            infoList = listOf(
                stringResource(R.string.manufacturer_date) to batteryUiState.manufactureDate,
                stringResource(R.string.model_number) to batteryUiState.modelNumber,
                stringResource(R.string.battery_id) to batteryUiState.batteryId
            )
        )
        BatteryInfoCard(
            icon = R.drawable.health_metrics,
            title = stringResource(R.string.health_metrics),
            infoList = listOf(
                stringResource(R.string.health_status) to batteryUiState.health.toString(),
                stringResource(R.string.cycle_count) to batteryUiState.cycleCount.toString()
            )
        )
        BatteryInfoCard(
            icon = R.drawable.thermostat,
            title = stringResource(R.string.temperature),
            infoList = listOf(
                stringResource(R.string.current) to batteryUiState.temperature.toString()
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
            color = colorResource(R.color.primary_red),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.percentage_symbol),
            modifier = Modifier
                .padding(bottom = 4.dp),
            color = colorResource(R.color.primary_red),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
    LinearProgressIndicator(
        progress = { percentage / 100f },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp),
        color = colorResource(R.color.primary_red),
        trackColor = Color.LightGray,
        gapSize = (-5).dp
    )
}