package com.example.volkswagendemo.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.dialog.FileDialog
import com.example.volkswagendemo.ui.composables.general.Background
import com.example.volkswagendemo.ui.composables.general.RfidLoading
import com.example.volkswagendemo.ui.composables.general.RfidTopBar
import com.example.volkswagendemo.ui.composables.inventory.InventoryError
import com.example.volkswagendemo.ui.composables.inventory.InventoryPause
import com.example.volkswagendemo.ui.composables.inventory.InventoryReading
import com.example.volkswagendemo.ui.composables.inventory.InventoryReport
import com.example.volkswagendemo.ui.composables.inventory.InventoryStart
import com.example.volkswagendemo.ui.composables.inventory.InventoryStop
import com.example.volkswagendemo.ui.states.RfidInventoryState
import com.example.volkswagendemo.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel,
    navigateToHome: () -> Unit,
) {
    val inventoryUiState = inventoryViewModel.inventoryUiState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                inventoryViewModel.reportInventory()
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Permiso de cámara requerido")
                }
            }
        }
    )
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RfidTopBar(
                title = if (inventoryUiState.rfidState == RfidInventoryState.Report) {
                    "Reporte de Incidencia"
                } else {
                    inventoryUiState.workshop
                },
                onNavigationBack = { navigateToHome() },
                iconAction = {
                    when (inventoryUiState.rfidState) {
                        RfidInventoryState.Connecting -> {}
                        RfidInventoryState.Ready -> {}
                        RfidInventoryState.Reading -> {}
                        RfidInventoryState.Pause -> {
                            IconButton(
                                onClick = {
                                    if (inventoryViewModel.hasCamaraPermission()) {
                                        inventoryViewModel.reportInventory()
                                    } else {
                                        requestCameraPermissionLauncher.launch(
                                            Manifest.permission.CAMERA
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.car_crash),
                                    contentDescription = null
                                )
                            }
                        }

                        RfidInventoryState.Report -> {}
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
                    RfidInventoryState.Report -> InventoryReport(inventoryViewModel)
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