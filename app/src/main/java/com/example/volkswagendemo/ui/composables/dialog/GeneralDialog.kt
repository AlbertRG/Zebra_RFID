package com.example.volkswagendemo.ui.composables.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.volkswagendemo.R

@Composable
fun GeneralDialog(
    icon: Int,
    iconDescription: String? = null,
    dialogTitle: String,
    dialogText: String,
    confirmButtonText: String = "Confirmar",
    onConfirmation: () -> Unit,
    hasDismissButton: Boolean = true,
    dismissButtonText: String = "Cancelar",
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(
                    text = confirmButtonText,
                    color = Color(0xFF05A6E1)
                )
            }
        },
        dismissButton = {
            if (hasDismissButton) {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(
                        text = dismissButtonText,
                        color = Color.Red
                    )
                }
            }
        },
        icon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = iconDescription,
                tint = Color(0xFF05A6E1)
            )
        },
        title = {
            Text(
                text = dialogTitle,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Text(
                text = dialogText,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        },
        containerColor = Color.White
    )
}

@Preview(showBackground = true)
@Composable
fun GeneralDialogPreview() {
    GeneralDialog(
        icon = R.drawable.outline_location_on_24,
        dialogTitle = "Localizacion",
        dialogText =    "Longitud: -99.1233243\n" +
                        "Latitud: 19.4031022\n" +
                        "Industria Zapatera 124, Zapopan Industrial Nte., 45130 Zapopan, Jal.",
        onConfirmation = {
            println("Confirmation registered")
        },
        hasDismissButton = false,
        onDismissRequest = {},
    )
}