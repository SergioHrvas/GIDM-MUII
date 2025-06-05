package com.pandemiagame.org.ui.screens.game.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pandemiagame.org.R


@Composable
fun ChangeBodyDialog(
    otherPlayerName: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(text = "${stringResource(R.string.cambiar_cuerpo_aviso)} $otherPlayerName")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.aceptar))
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(stringResource(R.string.cancelar))
            }
        }
    )
}