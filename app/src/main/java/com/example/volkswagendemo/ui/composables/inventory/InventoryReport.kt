package com.example.volkswagendemo.ui.composables.inventory

import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.general.RfidBottomBar
import com.example.volkswagendemo.viewmodel.InventoryViewModel

@Composable
fun InventoryReport(inventoryViewModel: InventoryViewModel) {
    var repuve by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(0.dp))
                .background(colorResource(R.color.tertiary_grey))
                .clickable {
                    inventoryViewModel.capturePhoto()
                },
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = {
                    previewView
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        OutlinedTextField(
            value = repuve.trim(),
            onValueChange = { newText ->
                val filteredText = newText
                    .uppercase()
                    .filter { it.isDigit() }
                    .take(8)
                repuve = filteredText
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            label = { Text("REPUVE") },
            trailingIcon = {
                Text(
                    text = "${repuve.length}/8",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = colorResource(R.color.primary_red),
                unfocusedIndicatorColor = Color(0xFF6D7679),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = colorResource(R.color.primary_red)
            )
        )
        OutlinedTextField(
            value = vin.trim(),
            onValueChange = { newText ->
                val filteredText = newText
                    .uppercase()
                    .filter { it.isLetterOrDigit() }
                    .take(17)
                vin = filteredText
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            label = { Text("Numero de Serie") },
            trailingIcon = {
                Text(
                    text = "${vin.length}/17",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                )
            },
            supportingText = {
                Text(
                    text = "Campo Obligatorio",
                    style = TextStyle(
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = colorResource(R.color.primary_red),
                unfocusedIndicatorColor = Color(0xFF6D7679),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = colorResource(R.color.primary_red)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        RfidBottomBar(
            isDualMode = false,
            title = "Guardar",
            onClickListener = { },
            title2 = "",
            onClickListener2 = {}
        )
    }

    LaunchedEffect(Unit) {
        inventoryViewModel.initCamera(previewView, lifecycleOwner)
    }

}