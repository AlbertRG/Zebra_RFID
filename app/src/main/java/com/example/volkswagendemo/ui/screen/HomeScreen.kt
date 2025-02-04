package com.example.volkswagendemo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.dialog.GeneralDialog
import com.example.volkswagendemo.ui.composables.dialog.InventoryDialog
import com.example.volkswagendemo.ui.composables.home.HomeColumn
import com.example.volkswagendemo.ui.composables.home.HomeTopBar
import com.example.volkswagendemo.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    navigateToInventory: () -> Unit,
    navigateToSearch: () -> Unit,
) {

    val showLocalizationDialog by homeViewModel::showLocalizationDialog

    Scaffold(
        topBar = {
            HomeTopBar()
        }) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(color = Color.White)
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                drawLine(
                    color = Color(0xFFF0F0F0),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
            }) {
            Image(
                painter = painterResource(
                    id = R.drawable.volkswagen_logo
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.08f),
                alignment = Alignment.Center,
                contentScale = ContentScale.None
            )
            HomeColumn(
                homeViewModel,
                navigateToSearch
            )
        }
    }

    if (showLocalizationDialog) {
        GeneralDialog(
            icon = R.drawable.outline_location_on_24,
            dialogTitle =   "Localizacion",
            dialogText =    "Longitud: -99.1233243\n" +
                            "Latitud: 19.4031022\n" +
                            "Industria Zapatera 124, Zapopan Industrial Nte., 45130 Zapopan, Jal.",
            onConfirmation = {
                println("Confirmation registered")
                homeViewModel.showLocalizationDialog = false
            },
            hasDismissButton = false,
            onDismissRequest = {}
        )
    }

    if (homeViewModel.showInventoryDialog) {
        InventoryDialog(
            homeViewModel,
            navigateToInventory
        )
    }

}