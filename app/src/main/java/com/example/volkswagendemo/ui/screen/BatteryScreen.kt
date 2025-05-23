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
import com.example.volkswagendemo.ui.composables.battery.BatteryError
import com.example.volkswagendemo.ui.composables.battery.BatteryInfo
import com.example.volkswagendemo.ui.composables.general.Background
import com.example.volkswagendemo.ui.composables.general.RfidLoading
import com.example.volkswagendemo.ui.composables.general.RfidTopBar
import com.example.volkswagendemo.ui.states.RfidBatteryState
import com.example.volkswagendemo.viewmodel.BatteryViewModel

@Composable
fun BatteryScreen(
    batteryViewModel: BatteryViewModel,
    navigateToHome: () -> Unit,
) {
    val batteryUiState = batteryViewModel.batteryUiState
    Scaffold(
        topBar = {
            RfidTopBar(
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
                when(batteryUiState.batteryState) {
                    RfidBatteryState.Connecting -> RfidLoading()
                    RfidBatteryState.Ready -> BatteryInfo(batteryViewModel)
                    RfidBatteryState.Error -> BatteryError(batteryViewModel)
                }
            }
        }
    }
}