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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.volkswagendemo.R
import com.example.volkswagendemo.ui.composables.general.Background
import com.example.volkswagendemo.ui.composables.general.RfidTopBar
import com.example.volkswagendemo.ui.composables.settings.Settings

@Composable
fun SettingsScreen(
    navigateToHome: () -> Unit,
) {
    Scaffold(
        topBar = {
            RfidTopBar(
                title = "Configuraciones",
                onNavigationBack = { navigateToHome() },
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
                Settings()
            }
        }
    }
}
@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navigateToHome = {})
}