package com.example.volkswagendemo.ui.composables.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.volkswagendemo.R
import com.example.volkswagendemo.data.LocationData
import com.example.volkswagendemo.viewmodel.InventoryViewModel

@Composable
fun FileDialog(
    inventoryViewModel: InventoryViewModel
) {

    var openDialog by remember { mutableStateOf(true) }
    val isLocationSave by remember { mutableStateOf(true) }

    val fileName by inventoryViewModel.fileName.collectAsState()
    val vins by inventoryViewModel.vinFlow.collectAsState()

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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.google_sheets_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = fileName,
                    modifier = Modifier
                        .padding(vertical = 16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isLocationSave) {
                    LocationInfo(
                        location = LocationData(0.0, 0.0),
                        address = "Industria Zapatera 124, Zapopan Industrial Nte., 45130 Zapopan, Jal."
                    )
                    HorizontalDivider(thickness = 1.dp, color = colorResource(R.color.tertiary_grey))
                }
                LazyColumn(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(vins) { vin ->
                        Text(
                            text = vin,
                            modifier = Modifier
                                .padding(top = 8.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, end = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            //TODO: Share info and close dialog
                        }
                    ) {
                        Text(
                            text = "Compartir",
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(
                        onClick = { inventoryViewModel.closeFileDialog() }
                    ) {
                        Text(
                            text = "Aceptar",
                            color = colorResource(R.color.primary_red)
                        )
                    }
                }
            }
        }
    }
}