package com.example.volkswagendemo.ui.composables.geiger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.general.RfidBottomBar
import com.example.volkswagendemo.ui.states.RfidGeigerState
import com.example.volkswagendemo.viewmodel.GeigerViewModel

@Composable
fun GeigerReady(
    geigerViewModel: GeigerViewModel
) {
    val geigerUiState = geigerViewModel.geigerUiState
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(geigerUiState.fileData) { tag ->
                GeigerCard(
                    repuve = tag.repuve,
                    vin = tag.vin,
                    distance = 50
                )
            }
        }

        when (geigerUiState.rfidGeigerState) {

            RfidGeigerState.Ready ->
                RfidBottomBar(
                    isDualMode = false,
                    title = stringResource(R.string.search_button_startSearch),
                    onClickListener = { geigerViewModel.performGeiger() },
                    title2 = "",
                    onClickListener2 = {}
                )

            RfidGeigerState.Reading ->
                RfidBottomBar(
                    isDualMode = false,
                    title = "Pausar",
                    onClickListener = { geigerViewModel.pauseGeiger() },
                    title2 = "",
                    onClickListener2 = {}
                )

            RfidGeigerState.Pause ->
                RfidBottomBar(
                    isDualMode = false,
                    title = "Continuar",
                    onClickListener = { geigerViewModel.performGeiger() },
                    title2 = "Detener",
                    onClickListener2 = {}
                )

            else -> {}
        }
    }
}