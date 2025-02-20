package com.example.volkswagendemo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.volkswagendemo.ui.composables.Background
import com.example.volkswagendemo.ui.composables.dialog.FileDialog
import com.example.volkswagendemo.ui.composables.inventory.InventoryConnecting
import com.example.volkswagendemo.ui.composables.inventory.InventoryError
import com.example.volkswagendemo.ui.composables.inventory.InventoryReading
import com.example.volkswagendemo.ui.composables.inventory.InventoryReady
import com.example.volkswagendemo.ui.composables.inventory.InventoryResume
import com.example.volkswagendemo.ui.composables.inventory.InventoryStopped
import com.example.volkswagendemo.ui.composables.inventory.InventoryTopBar
import com.example.volkswagendemo.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel,
    navigateToHome: () -> Unit,
) {

    val rfidStatus by inventoryViewModel.inventoryStatus.collectAsState()
    val showFileDialog by inventoryViewModel.showFileDialog.collectAsState()
    val tags by inventoryViewModel.tagsFlow.collectAsState()

    Scaffold(
        topBar = {
            InventoryTopBar(
                title = "Inventario",
                onNavigationBack = { navigateToHome() },
                iconAction = {
                    when (rfidStatus) {

                        "Stopped" ->
                            IconButton(
                                onClick = { }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.camera),
                                    contentDescription = null,

                                    )
                            }

                        "Resume" ->
                            IconButton(
                                onClick = { }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null
                                )
                            }
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
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
            Background()
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (rfidStatus) {
                    "Connecting" -> InventoryConnecting()
                    "Ready" -> InventoryReady(inventoryViewModel)
                    "Reading" -> InventoryReading(inventoryViewModel, tags)
                    "Stopped" -> InventoryStopped(inventoryViewModel, tags)
                    "Resume" -> InventoryResume(inventoryViewModel)
                    "Error" -> InventoryError(inventoryViewModel)
                }
            }
        }
    }

    if (showFileDialog) {
        FileDialog(inventoryViewModel)
    }

}