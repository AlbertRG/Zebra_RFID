package com.example.volkswagendemo.ui.composables.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volkswagendemo.R

@Composable
fun HomeCard(
    title: String,
    description: String,
    icon: Int,
    onClick: () -> Unit = {},
    iconColor: Color,
    fillColor: Color
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = fillColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    color = iconColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = iconColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeCardRedPreview() {
    HomeCard(
        title = "Inventario",
        description = "Escanea y registra chasises con RFID",
        icon = R.drawable.list,
        iconColor = colorResource(R.color.tertiary_grey),
        fillColor = colorResource(R.color.primary_red)
    )
}

@Preview(showBackground = true)
@Composable
fun HomeCardGreyPreview() {
    HomeCard(
        title = "Inventario",
        description = "Escanea y registra chasises con RFID",
        icon = R.drawable.list,
        iconColor = colorResource(R.color.primary_red),
        fillColor = colorResource(R.color.tertiary_grey)
    )
}