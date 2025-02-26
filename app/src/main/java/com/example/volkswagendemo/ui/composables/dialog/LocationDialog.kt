package com.example.volkswagendemo.ui.composables.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.volkswagendemo.R
import com.example.volkswagendemo.viewmodel.LocationViewModel

@Composable
fun LocationDialog(
    locationViewModel: LocationViewModel
) {

    val locationUiState = locationViewModel.locationUiState
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))

    when {

        locationUiState.isInternetError -> {
            GeneralDialog(
                icon = R.drawable.wifi_error,
                dialogTitle = "Sin conexión",
                dialogText = stringResource(R.string.internet_error),
                onConfirmation = { locationViewModel.setLocationShowing(false) },
                hasDismissButton = false,
                onDismissRequest = { }
            )
        }

        locationUiState.hasError -> {
            GeneralDialog(
                icon = R.drawable.error,
                dialogTitle = "Error",
                dialogText = locationUiState.message,
                onConfirmation = { locationViewModel.setLocationShowing(false) },
                hasDismissButton = false,
                onDismissRequest = { }
            )
        }

        else -> {
            Dialog(
                onDismissRequest = { locationViewModel.setLocationShowing(false) },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.location),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp),
                            tint = colorResource(R.color.primary_red)
                        )
                        Text(
                            text = "Localizacion",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        if (!locationUiState.isLoading) {
                            LocationInfo(
                                location = locationUiState.location,
                                address = locationUiState.address
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { locationViewModel.setLocationShowing(false) }
                                ) {
                                    Text(
                                        text = "Aceptar",
                                        color = Color.Black
                                    )
                                }
                            }
                        } else {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LottieAnimation(
                                    composition = composition,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(138.dp),
                                    iterations = LottieConstants.IterateForever
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}