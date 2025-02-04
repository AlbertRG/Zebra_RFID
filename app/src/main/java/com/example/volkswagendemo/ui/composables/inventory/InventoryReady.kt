package com.example.volkswagendemo.ui.composables.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volkswagendemo.R

@Composable
fun InventoryReady() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.startscreen_logo
            ),
            contentDescription = null,
            modifier = Modifier
                .size(160.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(
            modifier = Modifier
                .height(24.dp)
        )
        Text(
            text = "Tu inventario esta vacio",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Utiliza la terminal para",
            modifier = Modifier
                .padding(top = 8.dp),
            color = Color.Gray,
            fontSize = 14.sp,
        )
        Text(
            text = "buscar REPUVES disponibles",
            color = Color.Gray,
            fontSize = 14.sp,
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        InventoryBottomBar(
            isDualMode = false,
            title = "Comenzar",
            onClickListener = {},
            title2 = "",
            onClickListener2 = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InventoryReadyPreview() {
    InventoryReady()
}