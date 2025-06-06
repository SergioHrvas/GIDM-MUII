package com.pandemiagame.org.ui.screens.game.components.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pandemiagame.org.data.remote.models.game.Card
import com.pandemiagame.org.data.remote.models.game.CardWrapper
import com.pandemiagame.org.data.remote.models.game.GameResponse
import com.pandemiagame.org.ui.screens.game.components.cards.PlayerCardsRow
import com.pandemiagame.org.ui.screens.game.components.utils.GameState
import com.pandemiagame.org.ui.viewmodels.GameViewModel

@Composable
fun CurrentPlayerSection(
    game: GameResponse,
    gameState: GameState,
    viewModel: GameViewModel,
    currentPlayerIndex: Int,
    discards: List<Int>,
    discarting: Boolean,
    selecting: Int,
    exchanging: Boolean,
    onCardSelected: (Int) -> Unit,
    onDiscardToggle: () -> Unit,
    onConfirmDiscard: () -> Unit,
    onCancelAction: () -> Unit,
    onOrganSelected: (String) -> Unit
) {
    val recommendation by viewModel.recommendation.observeAsState()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerHeader(
            player = game.players[currentPlayerIndex],
            viewModel = viewModel,
            gameState = gameState,
            showChangeButton = false,
            onPlayerChange = {},
            current = true,
            multiplayer = (game.multiplayer && game.players[currentPlayerIndex].id == game.turn),
            winner = (game.winner != 0 && game.winner == game.players[currentPlayerIndex].id)
        )
        Body(
            myBody = true,
            organs = game.players[currentPlayerIndex].organs,
            onOrganSelected = { organType ->
                if (selecting == 2 || exchanging) {
                    onOrganSelected(organType)
                }
            }
        )
        val shouldHideOpponentCards by viewModel.shouldHideOpponentCards.observeAsState(false)


        if (shouldHideOpponentCards == false) {
            PlayerCardsRow(
                cards = game.players[currentPlayerIndex].playerCards,
                discards = discards,
                onCardSelected = onCardSelected,
                cardsSelected = gameState.cardsSelected,
                recommendation = recommendation
            )
        } else {
            // Muestra cartas boca abajo durante el cambio de turno
            PlayerCardsRow(
                cards = List(game.players[currentPlayerIndex].playerCards.size) {
                    CardWrapper(card = Card(id = 0, name = "BackCard", type = ""))
                },
                discards = discards,
                cardsSelected = gameState.cardsSelected,
                onCardSelected = {},
            )
        }

        if (gameState.winner == 0) {
            PlayerActions(
                discarting = discarting,
                selecting = selecting,
                exchanging = exchanging,
                onDiscardToggle = onDiscardToggle,
                onConfirmDiscard = onConfirmDiscard,
                onCancelAction = onCancelAction
            )
        }
    }
}