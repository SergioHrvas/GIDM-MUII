package com.pandemiagame.org.ui.screens.game.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.models.game.Card
import com.pandemiagame.org.data.remote.models.game.CardEnum


@Composable
fun PlayerCard(
    card: Card,
    isSelected: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    recommended: Boolean = false
) {

    Image(
        painter = painterResource(id = CardEnum.fromDisplayName(card.name)?.drawable ?: 0),
        contentDescription = stringResource(R.string.carta_jugador),
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .width(100.dp)
            .offset(y = if (isSelected || selected) (-8).dp else 0.dp) // Para subir la carta si está seleccionada
            .border(
                width = if (isSelected or recommended) 3.dp else 0.dp,
                color = if (isSelected) Color.Gray else if(recommended) Color(0xFFF17C00) else Color.Transparent
            )
            .clickable(onClick = {
                onClick()
            })
    )
}