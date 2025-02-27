package com.example.volkswagendemo.ui.composables.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.volkswagendemo.R
import com.example.volkswagendemo.viewmodel.InventoryViewModel

@Composable
fun InventoryStop(
    inventoryViewModel: InventoryViewModel
) {
    val inventoryUiState = inventoryViewModel.inventoryUiState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(inventoryUiState.filesList) { file ->
                InventoryResumeItem(
                    fileName = file,
                    onClickListener = { inventoryViewModel.openFileDialog(file) }
                )
            }
        }
        InventoryBottomBar(
            isDualMode = false,
            title = stringResource(R.string.inventory_button_new_reading),
            onClickListener = { inventoryViewModel.resetInventoryState() },
            title2 = "",
            onClickListener2 = { }
        )
    }
}