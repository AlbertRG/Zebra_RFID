package com.example.volkswagendemo.ui.composables.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.volkswagendemo.R
import com.example.volkswagendemo.viewmodel.HomeViewModel

@Composable
fun HomeColumn(
    homeViewModel: HomeViewModel,
    navigateToSearch: () -> Unit
) {

    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {
                homeViewModel.showLocalizationDialog()
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        HomeCard(
            title = "Inventario",
            description = "Genera archivos de REPUVES",
            icon = R.drawable.list,
            onClick = { homeViewModel.showWorkshopDialog = true }
        )
        HomeCard(
            title = "Busqueda",
            description = "Compara REPUVES con un archivo",
            icon = R.drawable.search,
            onClick = { navigateToSearch() }
        )
        HomeCard(
            title = "Localizacion",
            description = "Actualiza tu localizacion",
            icon = R.drawable.location,
            onClick = {
                if (homeViewModel.hasLocationPermission()) {
                    homeViewModel.showLocalizationDialog()
                } else {
                    requestLocationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            }
        )
    }

}