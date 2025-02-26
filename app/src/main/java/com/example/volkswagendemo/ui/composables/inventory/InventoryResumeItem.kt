package com.example.volkswagendemo.ui.composables.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.volkswagendemo.R

@Composable
fun InventoryResumeItem(
    fileName: String,
    onClickListener: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClickListener() },
    ) {
        Row(
            modifier = Modifier
                .height(72.dp),
            verticalAlignment = Alignment.CenterVertically
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
                    .weight(1f)
                    .padding(start = 16.dp),
                fontSize = 16.sp
            )
            IconButton(
                onClick = {
                    //TODO: Share info via email or Whatsapp
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.share),
                    contentDescription = null,
                    tint = colorResource(R.color.primary_red)
                )
            }
        }
        HorizontalDivider(thickness = 1.dp, color = colorResource(R.color.tertiary_grey))
    }

}

@Preview(showBackground = true)
@Composable
fun InventoryResumeItemPreview() {
    InventoryResumeItem(
        fileName = "Taller123 2024-12-18 14:02:00 catalogoln.xls",
        onClickListener = {}
    )
}