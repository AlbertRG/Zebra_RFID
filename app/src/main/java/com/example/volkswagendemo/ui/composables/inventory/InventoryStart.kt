package com.example.volkswagendemo.ui.composables.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.general.RfidBottomBar
import com.example.volkswagendemo.viewmodel.InventoryViewModel

@Composable
fun InventoryStart(inventoryViewModel: InventoryViewModel) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_list))
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
                .fillMaxWidth()
                .height(240.dp),
            iterations = LottieConstants.IterateForever
        )
        Spacer(
            modifier = Modifier
                .height(24.dp)
        )
        Text(
            text = stringResource(R.string.inventory_empty_message),
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = stringResource(R.string.inventory_instruction_part1),
            modifier = Modifier
                .padding(top = 8.dp),
            color = Color.Gray,
            fontSize = 14.sp,
        )
        Text(
            text = stringResource(R.string.inventory_instruction_part2),
            color = Color.Gray,
            fontSize = 14.sp,
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        RfidBottomBar(
            isDualMode = false,
            title = stringResource(R.string.inventory_button_start),
            onClickListener = { inventoryViewModel.performInventory() },
            title2 = "",
            onClickListener2 = {}
        )
    }
}