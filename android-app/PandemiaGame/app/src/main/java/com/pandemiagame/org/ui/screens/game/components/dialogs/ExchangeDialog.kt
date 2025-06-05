package com.pandemiagame.org.ui.screens.game.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.models.game.GameResponse
import com.pandemiagame.org.data.remote.models.game.Organ

@Composable
fun ExchangeDialog(
    game: GameResponse,
    currentPlayerIndex: Int,
    otherPlayerIndex: Int,
    selectedOrgan: String,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(stringResource(R.string.cambiar_organo_aviso, selectedOrgan))

                val index = game.players[otherPlayerIndex].organs.indexOfFirst { it.tipo == selectedOrgan }
                val targetOrgans = if (index != -1) {
                    listOf(game.players[otherPlayerIndex].organs[index])
                } else {
                    game.players[otherPlayerIndex].organs.filter {
                        (it.cure != 3) && puedoCambiarlo(
                            selectedOrgan,
                            it,
                            game.players[currentPlayerIndex].organs
                        )
                    }
                }

                var otherOrganSelected by remember { mutableStateOf<String?>(null) }
                var expanded by remember { mutableStateOf(false) }

                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(otherOrganSelected ?: stringResource(R.string.seleccionar_organo))
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    targetOrgans.forEach { organ ->
                        DropdownMenuItem(
                            text = { Text(organ.tipo) },
                            onClick = {
                                otherOrganSelected = organ.tipo
                                expanded = false
                            }
                        )
                        HorizontalDivider()
                    }
                }

                // Botones de acción
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(R.string.cancelar))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            otherOrganSelected?.let { onConfirm(it) }
                        },
                        enabled = otherOrganSelected != null
                    ) {
                        Text(stringResource(R.string.confirmar))
                    }
                }
            }
        }
    }
}


private fun puedoCambiarlo (myOrganSelected: String?, theirOrgan: Organ, myOrganList: List<Organ>): Boolean{
    return if (theirOrgan.tipo == myOrganSelected){
        true
    } else {
        !myOrganList.contains(theirOrgan)
    }
}