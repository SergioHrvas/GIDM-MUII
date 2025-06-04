package com.pandemiagame.org.ui.screens.game.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pandemiagame.org.data.remote.models.game.CardWrapper

@Composable
fun PlayerCardsRow(
    cards: List<CardWrapper>,
    discards: List<Int>,
    onCardSelected: (Int) -> Unit,
    cardsSelected: MutableList<Boolean>,
    recommendation: Int? = -1
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        cards.forEachIndexed { index, cardWrapper ->
            PlayerCard(
                card = cardWrapper.card,
                isSelected = discards[index] == 1,
                selected = cardsSelected[index],
                onClick = {
                    onCardSelected(index)
                    if(discards[index] == 0){
                        cardsSelected[index] = !cardsSelected[index]
                        if(cardsSelected[index]){
                            cardsSelected[(index+1)%cardsSelected.size] = false
                            cardsSelected[(index+2)%cardsSelected.size] = false
                        }
                    }
                },
                recommended = if(recommendation!=null) ((recommendation > 0) && (cards[index].card.id == recommendation)) else false
            )
        }
    }
}
