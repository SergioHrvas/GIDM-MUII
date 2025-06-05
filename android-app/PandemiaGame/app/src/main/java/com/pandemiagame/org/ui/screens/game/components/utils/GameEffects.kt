package com.pandemiagame.org.ui.screens.game.components.utils

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.pandemiagame.org.ui.viewmodels.GameViewModel
import com.pandemiagame.org.ui.viewmodels.createEmptyGame

@Composable
fun GameEffects(
    gameState: GameState,
    viewModel: GameViewModel,
    gameId: String,
    navController: NavController
) {
    val currentPlayerIndex = gameState.currentPlayerIndex
    val gameResponse = gameState.gameResponse

    val context = LocalContext.current

    // Efecto para cargar el juego inicialmente
    LaunchedEffect(Unit) {
        viewModel.setChangingTurn(true)
        viewModel.getGame(gameId, context)
        viewModel.getMoves(gameId)
    }

    // Efecto para manejar el ganador
    LaunchedEffect(gameState.winner) {
        if (gameState.winner != null && gameState.winner!! > 0) {
            gameState.showWinnerDialog = true
            viewModel.completeTurnChange()
        }
    }

    // Efecto para actualizar el índice del otro jugador
    LaunchedEffect(currentPlayerIndex, gameResponse) {
        gameResponse?.players?.let { players ->
            if (players.size > 1) {
                var nextIndex = (currentPlayerIndex + 1) % players.size
                if (nextIndex == currentPlayerIndex) nextIndex = (nextIndex + 1) % players.size
                gameState.otherPlayerIndex = nextIndex
            } else {
                gameState.otherPlayerIndex = 0
            }
            viewModel.clearRecommendation()
        }
    }

    // Efecto para manejar la infección
    LaunchedEffect(gameState.selectedOrgan) {
        gameState.selectedOrgan?.let { organType ->
            if (gameState.selecting > 0) {
                viewModel.doMove(gameState.selectedCard, gameState.otherPlayerIndex, organType, currentTurn = gameResponse?.players?.getOrNull(gameState.currentPlayerIndex)?.id)
                gameState.selecting = 0
                gameState.selectedOrgan = null
            }
        }
    }

    // Efecto para manejar el cambio de cuerpo
    LaunchedEffect(gameState.readyToChange) {
        if (gameState.readyToChange) {
            viewModel.doMove(gameState.selectedCard, gameState.otherPlayerIndex, currentTurn = gameResponse?.players?.getOrNull(gameState.currentPlayerIndex)?.id)
            gameState.readyToChange = false
        }
    }

    LaunchedEffect(gameState.discarting, gameState.selecting) {
        if(gameState.selecting == 0){
            gameState.cardsSelected[0] = false
            gameState.cardsSelected[1] = false
            gameState.cardsSelected[2] = false
        }
    }

    // Efecto de limpieza al desmontar
    DisposableEffect(Unit) {
        onDispose {
            gameState.isCardDrawn = false
            gameState.showWinnerDialog = false
            gameState.selecting = 0
            gameState.discarting = false
            gameState.discards[0] = 0
            gameState.discards[1] = 0
            gameState.discards[2] = 0
            viewModel.setGame(createEmptyGame())

        }

    }

    // Manejador de botón de retroceso
    BackHandler(enabled = true) {
        resetGameState(gameState)
        navController.popBackStack("home", inclusive = false)
    }
}


private fun resetGameState(gameState: GameState) {
    gameState.isCardDrawn = false
    gameState.showWinnerDialog = false
    gameState.selecting = 0
    gameState.discarting = false
    gameState.discards[0] = 0
    gameState.discards[1] = 0
    gameState.discards[2] = 0
}
