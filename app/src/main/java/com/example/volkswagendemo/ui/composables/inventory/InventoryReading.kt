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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volkswagendemo.core.dataclass.TagDataInfo
import com.example.volkswagendemo.viewmodel.InventoryViewModel

@Composable
fun InventoryReading(
    inventoryViewModel: InventoryViewModel,
    tags: List<TagDataInfo> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = tags.size.toString(),
            modifier = Modifier
                .padding(top = 8.dp),
            color = Color(0xFF05A6E1),
            fontSize = 45.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Lecturas",
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
            items(tags) { tag ->
                InventoryCard(
                    repuve = tag.repuve,
                    vin = tag.vin
                )
            }
        }
        InventoryBottomBar(
            isDualMode = false,
            title = "Parar",
            onClickListener = {inventoryViewModel.stopInventory()},
            title2 = "",
            onClickListener2 = {}
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun InventoryReadingPreview() {
    InventoryReading()
}*/
