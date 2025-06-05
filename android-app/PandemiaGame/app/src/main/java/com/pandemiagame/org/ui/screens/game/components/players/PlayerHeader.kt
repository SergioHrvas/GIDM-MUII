package com.pandemiagame.org.ui.screens.game.components.players

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.models.game.Player
import com.pandemiagame.org.ui.screens.game.components.utils.GameState
import com.pandemiagame.org.ui.viewmodels.GameViewModel

@Composable
fun PlayerHeader(
    gameState: GameState,
    viewModel: GameViewModel,
    player: Player,
    showChangeButton: Boolean,
    onPlayerChange: () -> Unit,
    current: Boolean,
    multiplayer: Boolean,
    winner: Boolean
) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (!current) Box(modifier = Modifier
            .padding(start = 10.dp)
            .weight(1F)) {
            MenuButton(gameState = gameState, viewModel = viewModel)
        }

        if (showChangeButton) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_right_24),
                contentDescription = stringResource(R.string.cambiar_jugador),
                modifier = Modifier.clickable(onClick = onPlayerChange)
            )
        }
        if(multiplayer)
            Icon(
                painter = painterResource((R.drawable.baseline_star_24)),
                contentDescription = stringResource(R.string.turno),
                tint = Color(0xFFFFA500)
            )
        if(winner)
            Icon(
                painter = painterResource((R.drawable.sharp_crown_24)),
                contentDescription = stringResource(R.string.ganador),
                tint = Color(0xFFFFA500)
            )
        Text(
            text = player.name,
            modifier = Modifier.padding(end = 20.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.user),
            contentDescription = stringResource(R.string.imagen_perfil),
            modifier = Modifier
                .padding(end = 10.dp)
                .size(40.dp)
                .border(width = 1.dp, color = Color.Black, shape = CircleShape)
        )
    }
}