package com.example.volkswagendemo.ui.composables.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
fun SelectFileItem(
    fileName: String,
    selected: Boolean = false,
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
            Icon(
                (if (selected) painterResource(R.drawable.radio_button_checked)
                else painterResource(R.drawable.radio_button_unchecked)),
                contentDescription = null,
                tint = colorResource(R.color.primary_red)
            )
        }
        HorizontalDivider(thickness = 1.dp, color = colorResource(R.color.tertiary_grey))
    }
}

@Preview(showBackground = true)
@Composable
fun SearchFileItemSelectedPreview() {
    SelectFileItem(
        fileName = "Taller123 2024-12-18 14:02:00.xls",
        selected = true,
        onClickListener = {}
    )
}

@Preview(showBackground = true)
@Composable
fun SearchFileItemPreview() {
    SelectFileItem(
        fileName = "Taller123 2024-12-18 14:02:00.xls",
        selected = false,
        onClickListener = {}
    )
}