package com.example.volkswagendemo.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.Background
import com.example.volkswagendemo.ui.composables.battery.BatteryConnecting
import com.example.volkswagendemo.ui.composables.battery.BatteryError
import com.example.volkswagendemo.ui.composables.battery.BatteryInfo
import com.example.volkswagendemo.ui.composables.inventory.InventoryTopBar
import com.example.volkswagendemo.viewmodel.BatteryViewModel

@Composable
fun BatteryScreen(
    batteryViewModel: BatteryViewModel,
    navigateToHome: () -> Unit,
) {

    val batteryUiState = batteryViewModel.batteryUiState

    Scaffold(
        topBar = {
            InventoryTopBar(
                title = stringResource(R.string.battery_title),
                onNavigationBack = { navigateToHome() },
            )
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            HorizontalDivider(
                thickness = 1.dp,
                color = colorResource(R.color.tertiary_grey)
            )
            Background()
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    batteryUiState.isConnecting -> BatteryConnecting()
                    batteryUiState.isConnected -> BatteryInfo(batteryViewModel)
                    batteryUiState.hasError -> BatteryError(batteryViewModel)
                }
            }
        }
    }
}