package com.example.volkswagendemo.ui.composables.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.volkswagendemo.data.LocationData
import com.example.volkswagendemo.viewmodel.HomeViewModel

@Composable
fun WorkshopDialog(
    homeViewModel: HomeViewModel,
    navigateToInventory: () -> Unit
) {

    var openDialog by remember { mutableStateOf(true) }
    var workshop by remember { mutableStateOf("") }
    val isLocationSave by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { openDialog = false },
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = Color(0xFF05A6E1)
                )
                Text(
                    text = "Localizacion del taller",
                    modifier = Modifier.padding(8.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Identifica el taller para optimizar el control de inventarios.",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
                OutlinedTextField(
                    value = workshop,
                    onValueChange = { newText -> workshop = newText },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text("Nombre del taller") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF05A6E1),
                        unfocusedIndicatorColor = Color(0xFF6D7679),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedLabelColor = Color(0xFF05A6E1)
                    )
                )
                if (isLocationSave) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Localizacion",
                            modifier = Modifier
                                .padding(bottom = 8.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        LocationInfo(
                            location = LocationData(0.0, 0.0),
                            address = "Industria Zapatera 124, Zapopan Industrial Nte., 45130 Zapopan, Jal."
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            homeViewModel.setWorkshopShowing(false)
                        }
                    ) {
                        Text(
                            text = "Cancelar",
                            color = Color.Red
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(
                        onClick = {
                            homeViewModel.setWorkshopShowing(false)
                            navigateToInventory()
                        }) {
                        Text(
                            text = "Aceptar",
                            color = if (workshop.none()) {
                                Color.Gray
                            } else {
                                Color(0xFF05A6E1)
                            }
                        )
                    }
                }
            }
        }
    }
}