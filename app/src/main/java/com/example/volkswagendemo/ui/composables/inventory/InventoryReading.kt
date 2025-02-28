package com.example.volkswagendemo.ui.composables.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.volkswagendemo.ui.composables.general.RfidBottomBar
import com.example.volkswagendemo.ui.composables.general.RfidCard
import com.example.volkswagendemo.viewmodel.InventoryViewModel

@Composable
fun InventoryReading(
    inventoryViewModel: InventoryViewModel
) {
    val inventoryUiState = inventoryViewModel.inventoryUiState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = inventoryUiState.scannedTags.size.toString(),
            modifier = Modifier
                .padding(top = 8.dp),
            color = colorResource(R.color.primary_red),
            fontSize = 45.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = stringResource(R.string.inventory_readings),
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
        Spacer(
            modifier = Modifier
                .height(8.dp)
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(inventoryUiState.scannedTags) { tag ->
                RfidCard(
                    repuve = tag.repuve,
                    vin = tag.vin
                )
            }
        }
        RfidBottomBar(
            isDualMode = false,
            title = stringResource(R.string.inventory_button_pause),
            onClickListener = { inventoryViewModel.pauseInventory() },
            title2 = "",
            onClickListener2 = {}
        )
    }
}