package com.pandemiagame.org.ui.screens.game.components.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pandemiagame.org.R

@Composable
fun PlayerActions(
    discarting: Boolean,
    selecting: Int,
    exchanging: Boolean,
    onDiscardToggle: () -> Unit,
    onConfirmDiscard: () -> Unit,
    onCancelAction: () -> Unit
) {
    Row(
        modifier = Modifier.padding(top = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {

        if (discarting) {
            ActionButton(
                iconResId = R.drawable.confirm,
                contentDescription = stringResource(R.string.confirmar),
                onClick = onConfirmDiscard
            )
        }
        if (!discarting && (selecting == 0) && !exchanging) {
            ActionButton(
                iconResId = R.drawable.discard,
                contentDescription = stringResource(R.string.descartar),
                onClick = onDiscardToggle
            )
        }
        else {
            ActionButton(
                iconResId = R.drawable.cancel,
                contentDescription = stringResource(R.string.cancelar),
                onClick = onCancelAction
            )
        }
    }
}

@Composable
fun ActionButton(
    iconResId: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription
        )
    }
}