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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.geiger.GeigerFiles
import com.example.volkswagendemo.ui.composables.geiger.GeigerReady
import com.example.volkswagendemo.ui.composables.general.Background
import com.example.volkswagendemo.ui.composables.general.RfidLoading
import com.example.volkswagendemo.ui.composables.general.RfidTopBar
import com.example.volkswagendemo.ui.states.RfidGeigerState
import com.example.volkswagendemo.viewmodel.GeigerViewModel

@Composable
fun GeigerScreen(
    geigerViewModel: GeigerViewModel,
    navigateToHome: () -> Unit,
) {
    val geigerUiState = geigerViewModel.geigerUiState
    Scaffold(
        topBar = {
            RfidTopBar(
                title = "Geiger",
                onNavigationBack = { navigateToHome() },
                iconAction = {
                    when (geigerUiState.rfidGeigerState) {
                        RfidGeigerState.Files -> {}
                        RfidGeigerState.Setup -> {}
                        RfidGeigerState.Ready -> {}
                        RfidGeigerState.Reading -> {}
                        RfidGeigerState.Pause -> {}
                        RfidGeigerState.Stop -> {}
                        RfidGeigerState.Error -> {}
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
                when (geigerUiState.rfidGeigerState) {
                    RfidGeigerState.Files -> GeigerFiles(geigerViewModel)
                    RfidGeigerState.Setup -> RfidLoading()
                    RfidGeigerState.Ready -> GeigerReady(geigerViewModel)
                    RfidGeigerState.Reading -> GeigerReady(geigerViewModel)
                    RfidGeigerState.Pause -> {}
                    RfidGeigerState.Stop -> {}
                    RfidGeigerState.Error -> {}
                }
            }
        }
    }
}