package com.example.volkswagendemo.ui.composables.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volkswagendemo.data.LocationData

@Composable
fun LocationInfo(
    location: LocationData,
    address: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                    append("Latitud: ")
                }
                append(location.latitude.toString())
            },
            modifier = Modifier
                .padding(vertical = 4.dp),
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                    append("Longitud: ")
                }
                append(location.longitude.toString())
            },
            modifier = Modifier
                .padding(vertical = 4.dp),
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = address,
            modifier = Modifier
                .padding(vertical = 4.dp),
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}