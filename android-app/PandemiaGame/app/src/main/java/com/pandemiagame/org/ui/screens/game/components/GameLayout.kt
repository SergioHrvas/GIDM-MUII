package com.pandemiagame.org.ui.screens.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.models.game.GameResponse
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.screens.game.components.cards.DeckSection
import com.pandemiagame.org.ui.screens.game.components.dialogs.GameDialog
import com.pandemiagame.org.ui.screens.game.components.players.CurrentPlayerSection
import com.pandemiagame.org.ui.screens.game.components.players.OpponentPlayerSection
import com.pandemiagame.org.ui.screens.game.components.utils.GameState
import com.pandemiagame.org.ui.viewmodels.GameViewModel

@Composable
fun GameLayout(
    gameState: GameState,
    viewModel: GameViewModel,
) {
    Scaffold(
        topBar = { CustomTopAppBar() },
    ) { innerPadding ->
        gameState.gameResponse?.let { game ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Diálogos
                GameDialog(gameState, game, viewModel)

                // Jugador oponente
                OpponentPlayerSection(
                    game = game,
                    gameState = gameState,
                    otherPlayerIndex = gameState.otherPlayerIndex,
                    onPlayerChange = {
                        gameState.otherPlayerIndex = (gameState.otherPlayerIndex + 1) % game.players.size
                        if(gameState.otherPlayerIndex == gameState.currentPlayerIndex) {
                            gameState.otherPlayerIndex = (gameState.otherPlayerIndex + 1) % game.players.size
                        }
                    },
                    onOrganSelected = { organType ->
                        if (gameState.selecting == 1) {
                            gameState.selectedOrgan = organType
                        }
                    },
                    viewModel = viewModel
                )

                // Separador con mazo de cartas
                DeckSection(
                    isCardDrawn = gameState.isCardDrawn,
                    onDrawAnimationComplete = {
                        gameState.isCardDrawn = false
                        val idDiscards = mutableListOf<Int>()
                        for (i in 0..gameState.discards.size - 1) {
                            if (gameState.discards[i] == 1) {
                                idDiscards.add(game.players[gameState.currentPlayerIndex].playerCards[i].card.id)
                                gameState.discards[i] = 0
                            }
                        }
                        if (gameState.winner == 0) {
                            viewModel.discardCards(idDiscards, currentTurn = game.players[gameState.currentPlayerIndex].id)
                            viewModel.setChangingTurn(true)
                        }
                    }
                )

                // Jugador actual
                CurrentPlayerSection(
                    game = game,
                    currentPlayerIndex = gameState.currentPlayerIndex,
                    discards = gameState.discards,
                    discarting = gameState.discarting,
                    selecting = gameState.selecting,
                    exchanging = gameState.exchanging,
                    onCardSelected = { cardIndex ->
                        handleCardSelection(
                            cardIndex = cardIndex,
                            gameState = gameState,
                            game = game,
                            viewModel = viewModel
                        )
                    },
                    onDiscardToggle = {
                        if((game.multiplayer == false) || game.players[gameState.currentPlayerIndex].id == game.turn) {
                            gameState.discarting = true
                        }
                    },
                    onConfirmDiscard = {
                        gameState.isCardDrawn = true
                        gameState.discarting = false
                    },
                    onCancelAction = {
                        if(gameState.discarting) {
                            for(i in 0..gameState.discards.size - 1) {
                                gameState.discards[i] = 0
                            }
                            gameState.discarting = false
                        }

                        if(gameState.selecting > 0) {
                            gameState.selecting = 0
                        }
                        if(gameState.exchanging) {
                            gameState.exchanging = false
                        }
                    },
                    onOrganSelected = { organType ->
                        if (gameState.selecting == 2 || gameState.exchanging) {
                            gameState.selectedOrgan = organType
                        }
                    },
                    viewModel = viewModel,
                    gameState = gameState
                )
            }
        } ?: Text(stringResource(R.string.cargando))
    }
}


private fun handleCardSelection(
    cardIndex: Int,
    gameState: GameState,
    game: GameResponse,
    viewModel: GameViewModel
) {
    if((game.multiplayer == false) || game.players[gameState.currentPlayerIndex].id == game.turn){
        gameState.selectedCard = cardIndex

        if (gameState.discarting) {
            gameState.discards[cardIndex] = (gameState.discards[cardIndex] + 1) % 2
        } else {
            when (game.players[gameState.currentPlayerIndex].playerCards[cardIndex].card.type) {
                "organ" -> {
                    if(game.winner == 0) {

                        viewModel.doMove(cardIndex, currentTurn = game.players[gameState.currentPlayerIndex].id)
                    }
                    gameState.selecting = 0

                }
                "virus" -> {
                    gameState.selecting = 1
                }
                "cure" -> {
                    gameState.selecting = 2
                }
                "action" -> {
                    handleActionCard(cardIndex, gameState, game, viewModel)
                }
            }
        }
    }
}


private fun handleActionCard(
    cardIndex: Int,
    gameState: GameState,
    game: GameResponse,
    viewModel: GameViewModel
) {
    when (game.players[gameState.currentPlayerIndex].playerCards[cardIndex].card.name) {
        "Change Body" -> {
            if (game.players.size > 2) {
                gameState.changingBody = true
            } else {
                gameState.readyToChange = true
            }
        }
        "Steal Organ" -> {
            gameState.selecting = 1
        }
        "Discard Cards" -> {
            viewModel.doMove(cardIndex, currentTurn = game.players[gameState.currentPlayerIndex].id
            )
        }
        "Infect Player" -> {
            gameState.infecting = true
        }
        "Exchange Card" -> {
            gameState.exchanging = true
        }
    }
}