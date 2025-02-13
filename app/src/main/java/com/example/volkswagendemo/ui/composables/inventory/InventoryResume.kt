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
import com.example.volkswagendemo.viewmodel.InventoryViewModel

@Composable
fun InventoryResume(
    inventoryViewModel: InventoryViewModel,
    files: List<String>
) {
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
            items(files) { file ->
                InventoryResumeItem(
                    fileName = file,
                    onClickListener = {}
                )
            }
        }
        InventoryBottomBar(
            isDualMode = false,
            title = "Nueva Lectura",
            onClickListener = {inventoryViewModel.restartInventory()},
            title2 = "",
            onClickListener2 = {}
        )
    }
}