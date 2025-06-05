package com.pandemiagame.org.ui.screens.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.models.game.GameResponse
import java.text.SimpleDateFormat
import java.util.Locale


// Composable para mostrar cada item del juego
@Composable
fun GameItem(game: GameResponse, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(8.dp), onClick = {
        onClick()
    }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio igual entre todos los hijos
            ) {
                Text(stringResource(R.string.fecha, formatDateTimeCompat(game.date, stringResource(R.string.fecha_invalida))))
                Text(stringResource(R.string.estado, game.status))
            }
            if(game.winner > 0){
                Text(stringResource(R.string.ganador_nombre, game.winner))
            }
            Row (
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio igual entre todos los hijos
            ) {
                if(game.multiplayer){
                    Icon(
                        painter = painterResource((R.drawable.baseline_people_24)),
                        contentDescription = stringResource(R.string.turno),
                        tint = Color(0xFFFFA500)
                    )
                }
                Text(stringResource(R.string.turnos, game.numTurns.toString()))
                Text(stringResource(R.string.jugadores, game.turns.size))
            }
        }
    }
}

fun formatDateTimeCompat(isoDateTime: String, error: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return try {
        val parsedDate = parser.parse(isoDateTime) ?: throw IllegalArgumentException(error)
        formatter.format(parsedDate)
    } catch (_: Exception) {
        isoDateTime // Fallback
    }
}
