package com.example.volkswagendemo.ui.composables.battery

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BatteryInfoTitle(
    title: String
) {
    Text(
        text = title,
        modifier = Modifier
            .padding(vertical = 12.dp),
        color = Color.Black,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
    )
}