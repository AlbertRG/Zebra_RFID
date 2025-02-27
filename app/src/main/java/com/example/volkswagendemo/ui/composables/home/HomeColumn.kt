package com.example.volkswagendemo.ui.composables.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.example.volkswagendemo.R
import com.example.volkswagendemo.viewmodel.HomeViewModel
import com.example.volkswagendemo.viewmodel.LocationViewModel

@Composable
fun HomeColumn(
    homeViewModel: HomeViewModel,
    locationViewModel: LocationViewModel,
    navigateToSearch: () -> Unit
) {

    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {
                locationViewModel.initLocation()
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
            onClick = { homeViewModel.setWorkshopShowing(true) },
            iconColor = colorResource(R.color.tertiary_grey),
            fillColor = colorResource(R.color.primary_red)
        )
        HomeCard(
            title = "Busqueda",
            description = "Compara REPUVES con un archivo",
            icon = R.drawable.search,
            onClick = { navigateToSearch() },
            iconColor = colorResource(R.color.primary_red),
            fillColor = colorResource(R.color.tertiary_grey)
        )
        HomeCard(
            title = "Geiger",
            description = "Localiza REPUVES",
            icon = R.drawable.radar,
            onClick = { },
            iconColor = colorResource(R.color.tertiary_grey),
            fillColor = colorResource(R.color.primary_red)
        )
        HomeCard(
            title = "Localizacion",
            description = "Actualiza tu localizacion",
            icon = R.drawable.location,
            onClick = {
                if (locationViewModel.hasLocationPermission()) {
                    locationViewModel.setLocationShowing(true)
                    locationViewModel.initLocation()
                } else {
                    requestLocationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            },
            iconColor = colorResource(R.color.primary_red),
            fillColor = colorResource(R.color.tertiary_grey)
        )
    }

}