package com.pandemiagame.org.ui.screens.game.components.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.gson.JsonObject
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.models.game.MoveResponse
import com.pandemiagame.org.data.remote.models.game.Player
import kotlin.collections.forEach


@Composable
fun MovesDialog(
    moves: List<MoveResponse>?,
    players: List<Player>,
    onDismiss: () -> Unit
) {
    val playerNames = remember(players) {
        players.associate { it.id to it.name }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp) // Margen vertical para el diálogo
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.registro_movimientos),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Área scrollable con altura máxima definida
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        moves?.forEach { move ->
                            item {
                                MoveDialogItem(move, playerNames)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // Botón fijo en la parte inferior
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text(stringResource(R.string.atras))
                }
            }
        }
    }
}

@Composable
fun MoveDialogItem(move: MoveResponse, playerNames: Map<Int, String>) {
    when {
        move.action == "discard" -> RenderDiscardMove(move)
        move.card?.type == "organ" || move.card?.type == "cure" ->
            RenderOrganOrCureMove(move)
        move.card?.type == "virus" -> RenderVirusMove(move, playerNames)
        move.card?.type == "action" -> RenderActionMove(move, playerNames)
    }
}


@Composable
private fun MoveItem(
    iconRes: Int,
    text: String,
    iconTint: Color = Color(0xFF4CAF50),
    iconSize: Dp = 16.dp,
    textSize: TextUnit = 12.sp
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 3.dp)
                .size(iconSize),
            tint = iconTint
        )
        Text(
            text = text,
            modifier = Modifier.padding(bottom = 2.dp),
            fontSize = textSize
        )
    }
}


@Composable
private fun RenderDiscardMove(move: MoveResponse) {
    val jsonArray = move.data?.asJsonArray

    Column {

        if (jsonArray?.size() == 0) {
            MoveItem(
                iconRes = R.drawable.baseline_delete_24,
                text = stringResource(R.string.pasado_turno, move.player.name)
            )
        }
        else {
            MoveItem(
                iconRes = R.drawable.baseline_delete_24,
                text = stringResource(R.string.descartado_cartas, move.player.name, jsonArray?.size() ?: 0, if((jsonArray?.size() ?: 0) == 1 ) "carta" else "cartas")
            )
        }
    }

}

@Composable
private fun RenderOrganOrCureMove(move: MoveResponse) {
    MoveItem(
        iconRes = R.drawable.outline_playing_cards_24,
        text = stringResource(R.string.usuario_juega_carta, move.player.name, move.card?.name ?: "")
    )
}

@Composable
private fun RenderVirusMove(move: MoveResponse, playerNames: Map<Int, String>) {
    val jsonObject = move.data?.asJsonObject
    val player1 = jsonObject?.get("player1")?.toString()?.toIntOrNull()

    Column {
        MoveItem(
            iconRes = R.drawable.outline_playing_cards_24,
            text = stringResource(R.string.usuario_juega_carta, move.player.name, move.card?.name ?: "")
        )
        player1?.let {
            Text(
                text = stringResource(R.string.infectando, playerNames[it] ?: ""),
                modifier = Modifier.padding(bottom = 2.dp),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun RenderActionMove(move: MoveResponse, playerNames: Map<Int, String>) {
    val card = move.card ?: return
    var jsonObject: JsonObject? = null
    if (move.data != null && !move.data.isJsonNull) {
        jsonObject = move.data.asJsonObject
    }

    when (card.name) {
        "Steal Organ", "Change Body", "Exchange Card" -> {
            val player1 = jsonObject?.get("player1")?.toString()?.toIntOrNull()
            val actionText = when (card.name) {
                "Steal Organ" -> stringResource(R.string.robando)
                "Change Body" -> stringResource(R.string.cambiando_cuerpo)
                "Exchange Card" -> stringResource(R.string.intercambiando_organo)
                else -> ""
            }

            Column {
                MoveItem(
                    iconRes = R.drawable.outline_playing_cards_24,
                    text = stringResource(R.string.usuario_juega_carta, move.player.name, card.name)
                )
                player1?.let {
                    Text(
                        text = "$actionText ${playerNames[it]}",
                        modifier = Modifier.padding(bottom = 2.dp),
                        fontSize = 10.sp
                    )
                }
            }
        }
        "Infect Player" -> RenderInfectPlayerMove(move, playerNames, jsonObject)
    }
}

@Composable
private fun RenderInfectPlayerMove(
    move: MoveResponse,
    playerNames: Map<Int, String>,
    jsonObject: JsonObject?
) {
    val infectedPlayers = (1..5).mapNotNull { i ->
        jsonObject?.get("player$i")?.toString()?.toIntOrNull()
    }.filter { it != 0 }.mapNotNull { playerNames[it] }

    Column {
        MoveItem(
            iconRes = R.drawable.outline_playing_cards_24,
            text = stringResource(R.string.usuario_juega_carta, move.player.name, move.card?.name ?: "")
        )
        if (infectedPlayers.isNotEmpty()) {
            Text(
                text = stringResource(R.string.estornudando_jugadores, infectedPlayers.joinToString()),
                modifier = Modifier.padding(bottom = 2.dp),
                fontSize = 10.sp
            )
        }
    }
}