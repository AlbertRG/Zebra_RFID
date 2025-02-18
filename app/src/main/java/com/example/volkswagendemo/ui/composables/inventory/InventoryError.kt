package com.example.volkswagendemo.ui.composables.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.volkswagendemo.R
import com.example.volkswagendemo.viewmodel.InventoryViewModel

@Composable
fun InventoryError(
    inventoryViewModel: InventoryViewModel
) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            modifier = Modifier
                .size(240.dp)
        )
        Spacer(
            modifier = Modifier
                .height(16.dp)
        )
        Text(
            text = "Opss! Ocurrio un error",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Verifica la conexion con la handheld",
            modifier = Modifier
                .padding(top = 8.dp),
            color = Color.Gray,
            fontSize = 14.sp,
        )
        Button(
            onClick = { inventoryViewModel.retryConnection() },
            modifier = Modifier
                .width(240.dp)
                .padding(vertical = 24.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF05A6E1))
        ) {
            Text(
                text = "Reintentar"
            )
        }
    }
}