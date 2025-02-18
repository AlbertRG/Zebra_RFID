package com.example.volkswagendemo.ui.composables.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.volkswagendemo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onClickListener: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
        ),
        title = {
            Text(
                text = "Volkswagen",
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { }
            ) {
                Image(
                    painter = painterResource(R.drawable.volkswagen_logo),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Fit
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onClickListener() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.menu),
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun HomeTopBarPreview() {
    HomeTopBar {}
}