package com.pandemiagame.org.ui.screens.game.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import com.pandemiagame.org.data.remote.models.game.GameResponse
import com.pandemiagame.org.data.remote.models.game.InfectData
import com.pandemiagame.org.ui.screens.game.components.utils.GameState
import com.pandemiagame.org.ui.viewmodels.GameViewModel


@Composable
fun GameDialog(
    gameState: GameState,
    game: GameResponse,
    viewModel: GameViewModel
) {
    val winnerName = remember(gameState.winner) {
        game.players.firstOrNull { it.id == gameState.winner }?.name ?: ""
    }

    // Diálogo de movimientos
    if(gameState.seeingMoves){
        val moves by viewModel.moves.observeAsState()
        MovesDialog(
            moves = moves,
            onDismiss = {
                gameState.seeingMoves = false
            },
            players = game.players
        )
    }

    // Diálogo de ganador
    if (gameState.showWinnerDialog) {
        WinnerDialog(
            winnerName = winnerName,
            onDismiss = { gameState.showWinnerDialog = false }
        )
    }

    // Diálogo de cambio de turno
    if ((gameState.changingTurn == true) && (gameState.winner == 0)) {
        TurnChangeDialog(
            playerName = game.players[game.players.indexOfFirst{ it.id == game.turn}].name,
            onDismiss = { viewModel.setChangingTurn(false)
            }
        )
    }

    // Diálogo de cambio de cuerpo
    if (gameState.changingBody) {
        ChangeBodyDialog(
            otherPlayerName = game.players[gameState.otherPlayerIndex].name,
            onConfirm = {
                gameState.changingBody = false
                gameState.readyToChange = true
            },
            onCancel = { gameState.changingBody = false }
        )
    }

    // Diálogo de infección
    if (gameState.infecting) {
        InfectDialog(
            currentPlayer = game.players[gameState.currentPlayerIndex],
            otherPlayers = game.players.filter { it.id != game.players[gameState.currentPlayerIndex].id },
            onConfirm = { infectData ->
                gameState.infecting = false
                viewModel.doMoveInfect(gameState.selectedCard, infectData = infectData, currentTurn = game.players[gameState.currentPlayerIndex].id)
            },
            onCancel = { gameState.infecting = false
                for(i in 0..gameState.cardsSelected.size -1){
                    gameState.cardsSelected[i] = false
                }
            }
        )
    }

    // Diálogo de intercambio
    if (gameState.exchanging && (gameState.selectedOrgan != null)) {
        ExchangeDialog(
            game = game,
            currentPlayerIndex = gameState.currentPlayerIndex,
            otherPlayerIndex = gameState.otherPlayerIndex,
            selectedOrgan = gameState.selectedOrgan!!,
            onConfirm = { organToExchange ->
                val data = InfectData(
                    player1 = game.players[gameState.otherPlayerIndex].id,
                    organ1 = organToExchange,
                )
                viewModel.doMoveExchange(
                    gameState.selectedCard,
                    gameState.selectedOrgan!!,
                    infectData = data,
                    currentTurn = game.players[gameState.currentPlayerIndex].id
                )
                gameState.exchanging = false
                gameState.selectedOrgan = null
            },
            onCancel = {
                gameState.exchanging = false
                gameState.selectedOrgan = null
            }
        )
    }
}
