package com.example.volkswagendemo.ui.composables.battery

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BatteryInfoItem(
    title: String,
    value: String
) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                append("${title}: ")
            }
            append(value)
        },
        modifier = Modifier
            .padding(vertical = 4.dp),
        color = Color.Black,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    )
}