package com.example.volkswagendemo.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.volkswagendemo.ui.composables.Background
import com.example.volkswagendemo.ui.composables.dialog.WorkshopDialog
import com.example.volkswagendemo.ui.composables.dialog.LocationDialog
import com.example.volkswagendemo.ui.composables.home.BottomSheet
import com.example.volkswagendemo.ui.composables.home.HomeColumn
import com.example.volkswagendemo.ui.composables.home.HomeTopBar
import com.example.volkswagendemo.viewmodel.HomeViewModel
import com.example.volkswagendemo.viewmodel.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    locationViewModel: LocationViewModel,
    navigateToInventory: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToBattery: () -> Unit,
    navigateToSettings: () -> Unit
) {

    val homeUiState = homeViewModel.homeUiStates
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            HomeTopBar(
                onClickListener = { homeViewModel.setMenuShowing(true) }
            )
        }) { innerPadding ->

        if (homeUiState.isMenuShowing) {
            BottomSheet(
                homeViewModel,
                sheetState,
                scope,
                navigateToBattery,
                navigateToSettings
            )
        }

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
            Background()
            HomeColumn(
                homeViewModel,
                locationViewModel,
                navigateToSearch
            )
        }

    }

    if (locationViewModel.locationUiState.isShowing) {
        LocationDialog(
            locationViewModel
        )
    }

    if (homeUiState.isWorkshopShowing) {
        WorkshopDialog(
            homeViewModel,
            navigateToInventory
        )
    }

}