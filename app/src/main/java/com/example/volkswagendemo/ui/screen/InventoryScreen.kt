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
import androidx.compose.ui.unit.dp
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.dialog.FileDialog
import com.example.volkswagendemo.ui.composables.general.Background
import com.example.volkswagendemo.ui.composables.general.RfidLoading
import com.example.volkswagendemo.ui.composables.general.RfidTopBar
import com.example.volkswagendemo.ui.composables.inventory.InventoryError
import com.example.volkswagendemo.ui.composables.inventory.InventoryPause
import com.example.volkswagendemo.ui.composables.inventory.InventoryReading
import com.example.volkswagendemo.ui.composables.inventory.InventoryStart
import com.example.volkswagendemo.ui.composables.inventory.InventoryStop
import com.example.volkswagendemo.ui.states.RfidInventoryState
import com.example.volkswagendemo.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel,
    navigateToHome: () -> Unit,
) {
    val inventoryUiState = inventoryViewModel.inventoryUiState
    Scaffold(
        topBar = {
            RfidTopBar(
                title = inventoryUiState.workshop,
                onNavigationBack = { navigateToHome() },
                iconAction = {
                    when (inventoryUiState.rfidState) {
                        RfidInventoryState.Connecting -> {}
                        RfidInventoryState.Ready -> {}
                        RfidInventoryState.Reading -> {}
                        RfidInventoryState.Pause -> {}
                        RfidInventoryState.Stop -> {}
                        RfidInventoryState.Error -> {}
                    }
                }
            )
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            HorizontalDivider(thickness = 1.dp, color = colorResource(R.color.tertiary_grey))
            Background()
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (inventoryUiState.rfidState) {
                    RfidInventoryState.Connecting -> RfidLoading()
                    RfidInventoryState.Ready -> InventoryStart(inventoryViewModel)
                    RfidInventoryState.Reading -> InventoryReading(inventoryViewModel)
                    RfidInventoryState.Pause -> InventoryPause(inventoryViewModel)
                    RfidInventoryState.Stop -> InventoryStop(inventoryViewModel)
                    RfidInventoryState.Error -> InventoryError(inventoryViewModel)
                }
            }
        }
    }
    if (inventoryUiState.isFileDialogVisible) {
        FileDialog(inventoryViewModel)
    }
}