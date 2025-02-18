package com.example.volkswagendemo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.dialog.GeneralDialog
import com.example.volkswagendemo.ui.composables.dialog.InventoryDialog
import com.example.volkswagendemo.ui.composables.home.ButtonSheetItems
import com.example.volkswagendemo.ui.composables.home.HomeColumn
import com.example.volkswagendemo.ui.composables.home.HomeTopBar
import com.example.volkswagendemo.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    navigateToInventory: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToBattery: () -> Unit,
    navigateToSettings: () -> Unit
) {

    val showBottomSheet by homeViewModel.bottomSheetStatus.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val showLocalizationDialog by homeViewModel.localizationDialogStatus.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(
                onClickListener = { homeViewModel.showBottomSheet() }
            )
        }) { innerPadding ->

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    homeViewModel.hideBottomSheet()
                },
                sheetState = sheetState
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        ButtonSheetItems(
                            title = "Informacion de Bateria",
                            icon = R.drawable.battery,
                            onClickListener = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        homeViewModel.hideBottomSheet()
                                        navigateToBattery()
                                    }
                                }
                            }
                        )
                        ButtonSheetItems(
                            title = "Configuraciones",
                            icon = R.drawable.settings,
                            onClickListener = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        homeViewModel.hideBottomSheet()
                                        navigateToSettings()
                                    }
                                }
                            }
                        )
                        Row(
                            Modifier
                                .height(32.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Version 1.0",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
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
            icon = R.drawable.location,
            dialogTitle = "Localizacion",
            dialogText = "Longitud: -99.1233243\n" +
                    "Latitud: 19.4031022\n" +
                    "Industria Zapatera 124, Zapopan Industrial Nte., 45130 Zapopan, Jal.",
            onConfirmation = {
                homeViewModel.hideLocalizationDialog()
            },
            hasDismissButton = false,
            onDismissRequest = {}
        )
    }

    if (homeViewModel.showWorkshopDialog) {
        InventoryDialog(
            homeViewModel,
            navigateToInventory
        )
    }

}