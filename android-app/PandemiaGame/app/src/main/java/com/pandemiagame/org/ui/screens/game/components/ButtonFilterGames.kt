package com.pandemiagame.org.ui.screens.game.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource


@Composable
fun ButtonFilterGames(onClick: () -> Unit, enabled: Boolean, painterResource: Int, stringResource: Int){
    IconButton(
        onClick = {
            onClick
        },
        enabled = enabled
    ){
        Icon(
            painter = painterResource(painterResource),
            contentDescription = stringResource(stringResource),
            tint = Color(0xFFFFA500)
        )
    }
}