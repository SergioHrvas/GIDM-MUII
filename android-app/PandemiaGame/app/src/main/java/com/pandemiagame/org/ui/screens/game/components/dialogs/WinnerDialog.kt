package com.pandemiagame.org.ui.screens.game.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pandemiagame.org.R


@Composable
fun WinnerDialog(
    winnerName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.juego_terminado))
        },
        text = {
            Text(stringResource(R.string.ganador_nombre, winnerName))
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.aceptar))
            }
        }
    )
}