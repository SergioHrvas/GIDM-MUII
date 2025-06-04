package com.pandemiagame.org.ui.screens.game.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.pandemiagame.org.R
import com.pandemiagame.org.ui.screens.game.components.utils.DrawCardAnimation


@Composable
fun DeckSection(
    isCardDrawn: Boolean,
    onDrawAnimationComplete: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(1f)
    ) {
        HorizontalDivider(thickness = 3.dp, modifier = Modifier.fillMaxWidth())

        Image(
            painter = painterResource(id = R.drawable.backdeck),
            contentDescription = "Mazo de cartas",
            modifier = Modifier
                .height(110.dp)
        )

        if (isCardDrawn) {
            DrawCardAnimation(onAnimationEnd = onDrawAnimationComplete)
        }
    }
}