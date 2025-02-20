package com.example.volkswagendemo.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.volkswagendemo.R

@Composable
fun Background() {
    Image(
        painter = painterResource(
            id = R.drawable.volkswagen_logo
        ),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.08f),
        alignment = Alignment.Center,
        contentScale = ContentScale.None
    )
}