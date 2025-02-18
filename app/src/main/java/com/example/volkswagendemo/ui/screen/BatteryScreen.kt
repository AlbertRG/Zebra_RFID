package com.example.volkswagendemo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.volkswagendemo.R
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

    val batteryStatus by batteryViewModel.batteryStatus.collectAsState()

    Scaffold(
        topBar = {
            InventoryTopBar(
                title = "Informacion de Bateria",
                onNavigationBack = { navigateToHome() },
            )
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
            Image(
                painter = painterResource(
                    id = R.drawable.volkswagen_logo
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.08f),
                alignment = Alignment.Center,
                contentScale = ContentScale.None
            )
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (batteryStatus) {
                    "Connecting" -> BatteryConnecting()
                    "Ready" -> BatteryInfo(batteryViewModel)
                    "Error" -> BatteryError(batteryViewModel)
                }
            }
        }
    }
}