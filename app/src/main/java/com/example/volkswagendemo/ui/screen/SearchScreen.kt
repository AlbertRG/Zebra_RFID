package com.example.volkswagendemo.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.Background
import com.example.volkswagendemo.ui.composables.inventory.InventoryConnecting
import com.example.volkswagendemo.ui.composables.inventory.InventoryTopBar
import com.example.volkswagendemo.ui.composables.search.SearchFiles
import com.example.volkswagendemo.ui.composables.search.SearchPause
import com.example.volkswagendemo.ui.composables.search.SearchReading
import com.example.volkswagendemo.ui.composables.search.SearchReady
import com.example.volkswagendemo.ui.states.RfidSearchState
import com.example.volkswagendemo.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    navigateToHome: () -> Unit,
) {
    val searchUiState = searchViewModel.searchUiState
    Scaffold(
        topBar = {
            InventoryTopBar(
                title = "Busqueda",
                onNavigationBack = { navigateToHome() },
                iconAction = {
                    when (searchUiState.rfidSearchState) {
                        RfidSearchState.Files -> {}
                        RfidSearchState.SetupInfo -> {}
                        RfidSearchState.Ready -> {}
                        RfidSearchState.Reading -> {}
                        RfidSearchState.Pause -> {}
                        RfidSearchState.Stop -> {}
                        RfidSearchState.Error -> {}
                    }
                }
            )
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            HorizontalDivider(thickness = 1.dp, color = colorResource(R.color.tertiary_grey))
            Background()
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (searchUiState.rfidSearchState) {
                    RfidSearchState.Files -> SearchFiles(searchViewModel)
                    RfidSearchState.SetupInfo -> InventoryConnecting()
                    RfidSearchState.Ready -> SearchReady(searchViewModel)
                    RfidSearchState.Reading -> SearchReading(searchViewModel)
                    RfidSearchState.Pause -> SearchPause(searchViewModel)
                    RfidSearchState.Stop -> {}
                    RfidSearchState.Error -> {}
                }
            }
        }
    }
}