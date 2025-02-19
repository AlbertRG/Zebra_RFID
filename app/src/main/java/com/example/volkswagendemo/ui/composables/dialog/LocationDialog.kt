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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.example.volkswagendemo.ui.states.LocationStates
import com.example.volkswagendemo.viewmodel.HomeViewModel

@Composable
fun LocationDialog(
    homeViewModel: HomeViewModel
) {

    val locationStatus by homeViewModel.locationStatus.collectAsState()
    val location by homeViewModel.location.collectAsState()
    val address by homeViewModel.address.collectAsState()
    val message by homeViewModel.message.collectAsState()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.connecting))

    when (locationStatus) {

        in listOf(LocationStates.Show, LocationStates.Loading) -> {
            Dialog(
                onDismissRequest = { homeViewModel.hideLocalizationDialog() },
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
                            tint = Color(0xFF05A6E1)
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
                        if (locationStatus != LocationStates.Loading) {

                            LocationInfo(
                                location = location,
                                address = address
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { homeViewModel.hideLocalizationDialog() }
                                ) {
                                    Text(
                                        text = "Aceptar",
                                        color = Color(0xFF05A6E1)
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

        is LocationStates.Error -> {
            GeneralDialog(
                icon = R.drawable.error,
                dialogTitle = "Error",
                dialogText = message,
                onConfirmation = { homeViewModel.hideLocalizationDialog() },
                hasDismissButton = false,
                onDismissRequest = { }
            )
        }

        is LocationStates.InternetError -> {
            GeneralDialog(
                icon = R.drawable.wifi_error,
                dialogTitle = "Sin conexión",
                dialogText = message,
                onConfirmation = { homeViewModel.hideLocalizationDialog() },
                hasDismissButton = false,
                onDismissRequest = { }
            )
        }

        else -> { }

    }
}