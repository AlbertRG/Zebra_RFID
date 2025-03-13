package com.example.volkswagendemo.ui.composables.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.general.RfidBottomBar
import com.example.volkswagendemo.ui.states.SettingUiState
import com.example.volkswagendemo.viewmodel.SettingsViewModel

@Composable
fun Settings(
    settingsViewModel: SettingsViewModel
) {
    val settingUiState = settingsViewModel.settingUiStates.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        /*SettingItem(
            icon = R.drawable.antenna,
            title = "Poder de la Antena"
        ) {
            PowerSlider(settingUiState.value) { newPower ->
                settingsViewModel.updateAntennaPower(newPower)
            }
        }*/
        SettingItem(
            icon = R.drawable.volume_high,
            title = "Volumen"
        ) {
            VolumeSelector(settingUiState.value) { newVolume ->
                settingsViewModel.updateBeeperVolume(newVolume)
            }
        }
        /*SettingItem(
            icon = R.drawable.settings,
            title = "Link Profile"
        ) { LinkProfileOptions() }*/
        RfidBottomBar(
            isDualMode = false,
            title = "Guardar",
            onClickListener = {
                settingsViewModel.saveSettings()
            },
            title2 = "",
            onClickListener2 = {}
        )
    }
}

@Composable
fun SettingItem(
    icon: Int,
    title: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = title,

                    tint = colorResource(R.color.primary_red)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    modifier = Modifier
                        .padding(vertical = 4.dp),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun PowerSlider(settingUiState: SettingUiState, onPowerChange: (Float) -> Unit) {
    Slider(
        value = settingUiState.settings.antennaPower / 100f,
        onValueChange = { power -> onPowerChange(power * 100f) },
        modifier = Modifier
            .padding(vertical = 4.dp),
        colors = SliderDefaults.colors(
            thumbColor = colorResource(R.color.primary_red),
            activeTrackColor = colorResource(R.color.primary_red),
            inactiveTrackColor = colorResource(R.color.secondary_red)
        )
    )
}

@Composable
fun VolumeSelector(settingUiState: SettingUiState, onVolumeChange: (Int) -> Unit) {
    val options = listOf("Silencio", "Bajo", "Medio", "Alto")
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { onVolumeChange(index) },
                selected = index == settingUiState.settings.beeperVolume,
                label = { Text(label) },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = colorResource(R.color.primary_red),
                    activeContentColor = colorResource(R.color.white),
                    inactiveContainerColor = colorResource(R.color.secondary_red),
                    inactiveContentColor = colorResource(R.color.primary_red)
                ),
                border = SegmentedButtonDefaults.borderStroke(color = Color.Transparent)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkProfileOptions() {
    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Opción 1", "Opción 2", "Opción 3")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            label = { Text("Selecciona una opción") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expandir",
                    tint = colorResource(R.color.primary_red)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = colorResource(R.color.primary_red),
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = colorResource(R.color.primary_red)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        text = option
                        expanded = false
                    }
                )
            }
        }
    }
}