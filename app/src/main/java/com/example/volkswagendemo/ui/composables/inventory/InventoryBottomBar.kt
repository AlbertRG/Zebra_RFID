package com.example.volkswagendemo.ui.composables.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.volkswagendemo.R

@Composable
fun InventoryBottomBar(
    isDualMode: Boolean,
    title: String,
    onClickListener: (() -> Unit),
    title2: String,
    onClickListener2: (() -> Unit),
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        HorizontalDivider(thickness = 1.dp, color = colorResource(R.color.tertiary_grey))
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isDualMode) {
                Button(
                    onClick = { onClickListener2() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            vertical = 16.dp,
                            horizontal = 8.dp
                        ),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.secondary_red))
                ) {
                    Text(
                        text = title2,
                        color = colorResource(R.color.primary_red)
                    )
                }
            }
            Button(
                onClick = { onClickListener() },
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        vertical = 16.dp,
                        horizontal = 8.dp
                    ),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_red))
            ) {
                Text(
                    text = title
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InventoryBottomBarPreview() {
    InventoryBottomBar(
        isDualMode = false,
        title = "Start",
        onClickListener = {},
        title2 = "",
        onClickListener2 = {}
    )
}

@Preview(showBackground = true)
@Composable
fun InventoryBottomBarDualModePreview() {
    InventoryBottomBar(
        isDualMode = true,
        title = "Continue",
        onClickListener = {},
        title2 = "Resume",
        onClickListener2 = {}
    )
}