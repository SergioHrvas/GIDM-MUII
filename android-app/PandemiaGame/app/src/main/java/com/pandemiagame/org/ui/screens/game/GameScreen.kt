package com.pandemiagame.org.ui.screens.game

import androidx.compose.runtime.Composable
import com.pandemiagame.org.ui.viewmodels.GameViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pandemiagame.org.data.remote.utils.TokenManager
import com.pandemiagame.org.ui.screens.game.components.utils.GameEffects
import com.pandemiagame.org.ui.screens.game.components.GameLayout
import com.pandemiagame.org.ui.screens.game.components.utils.rememberGameState
import com.pandemiagame.org.ui.viewmodels.GameViewModelFactory

@Composable
fun GameScreen(gameId: String = "", navController: NavController) {
    val context = LocalContext.current

    val tokenManager = TokenManager(context)
    // Creamos el ViewModel
    val viewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(tokenManager)
    )

    // Estados principales del juego
    val gameState = rememberGameState(viewModel)

    // Efectos y lógica principal
    GameEffects(gameState, viewModel, gameId, navController)

    // UI principal
    GameLayout(
        gameState = gameState,
        viewModel = viewModel,
    )
}