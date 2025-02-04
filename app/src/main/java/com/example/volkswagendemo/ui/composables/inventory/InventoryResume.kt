package com.example.volkswagendemo.ui.composables.inventory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun InventoryResume() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(20) {
                InventoryResumeItem(
                    fileName = "Taller123 2024-12-18 14:02:00",
                    onClickListener = {}
                )
            }
        }
        InventoryBottomBar(
            isDualMode = false,
            title = "Nueva Lectura",
            onClickListener = {},
            title2 = "",
            onClickListener2 = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InventoryResumePreview() {
    InventoryResume()
}
