package com.example.volkswagendemo.ui.composables.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volkswagendemo.R
import com.example.volkswagendemo.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    homeViewModel: HomeViewModel,
    sheetState: SheetState,
    scope: CoroutineScope,
    navigateToBattery: () -> Unit,
    navigateToSettings: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = {
            homeViewModel.setMenuShowing(false)
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
                                homeViewModel.setMenuShowing(false)
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
                                homeViewModel.setMenuShowing(false)
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