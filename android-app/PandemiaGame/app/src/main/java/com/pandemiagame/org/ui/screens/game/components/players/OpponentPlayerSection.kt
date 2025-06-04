package com.pandemiagame.org.ui.screens.game.components.players

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pandemiagame.org.data.remote.models.game.GameResponse
import com.pandemiagame.org.ui.screens.game.components.Body
import com.pandemiagame.org.ui.screens.game.components.utils.GameState
import com.pandemiagame.org.ui.viewmodels.GameViewModel


@Composable
fun OpponentPlayerSection(
    game: GameResponse,
    gameState: GameState,
    otherPlayerIndex: Int,
    onPlayerChange: () -> Unit,
    onOrganSelected: (String) -> Unit,
    viewModel: GameViewModel
) {
    Column(modifier = Modifier.padding(bottom = 2.dp)) {
        PlayerHeader(
            gameState = gameState,
            viewModel = viewModel,
            player = game.players[otherPlayerIndex],
            showChangeButton = game.players.size > 2,
            onPlayerChange = onPlayerChange,
            current = false,
            multiplayer = (game.multiplayer && game.players[otherPlayerIndex].id == game.turn),
            winner = (game.winner != 0 && game.winner == game.players[otherPlayerIndex].id)
        )

        Body(
            myBody = false,
            organs = game.players[otherPlayerIndex].organs,
            onOrganSelected = onOrganSelected
        )
    }
}
