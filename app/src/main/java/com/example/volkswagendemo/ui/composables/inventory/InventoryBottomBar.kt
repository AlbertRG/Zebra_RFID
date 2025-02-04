package com.example.volkswagendemo.ui.composables.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun InventoryBottomBar(
    isDualMode: Boolean,
    title: String,
    onClickListener: (() -> Unit),
    title2: String,
    onClickListener2: (() -> Unit),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(Color.White)
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                drawLine(
                    color = Color(0xFFF0F0F0),
                    start = androidx.compose.ui.geometry.Offset(-80f, 0f),
                    end = androidx.compose.ui.geometry.Offset(size.width + 80f, 0f),
                    strokeWidth = strokeWidth
                )
            },
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
                    colors = ButtonDefaults.buttonColors(Color(0x2505A6E1))
                ) {
                    Text(
                        text = title2,
                        color = Color(0xFF05A6E1)
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
            colors = ButtonDefaults.buttonColors(Color(0xFF05A6E1))
        ) {
            Text(
                text = title
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InventoryBottomBarPreview() {
    InventoryBottomBar(
        isDualMode = false,
        title = "Reanudar lectura",
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
        title = "Reanudar lectura",
        onClickListener = {},
        title2 = "Finalizar lectura",
        onClickListener2 = {}
    )
}