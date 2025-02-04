package com.example.volkswagendemo.ui.composables.home

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
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        HomeCard(
            title = "Inventario",
            description = "Genera archivos de REPUVES",
            icon = R.drawable.baseline_list_alt_24,
            onClick = { homeViewModel.showInventoryDialog = true }
        )
        HomeCard(
            title = "Busqueda",
            description = "Compara REPUVES con un archivo",
            icon = R.drawable.baseline_manage_search_24,
            onClick = { navigateToSearch() }
        )
        HomeCard(
            title = "Localizacion",
            description = "Actualiza tu localizacion",
            icon = R.drawable.outline_location_on_24,
            onClick = { homeViewModel.showLocalizationDialog = true }
        )
    }
}