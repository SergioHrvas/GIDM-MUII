package com.pandemiagame.org.ui.screens.game.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pandemiagame.org.R

@Composable
fun NewGameButton(buttonEnable: Boolean, onButtonSelected: () -> Unit){
    Button(
        onClick = {
            onButtonSelected()},
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(top = 10.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color.Gray,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContentColor = Color.White,
            contentColor = Color.White,
        ),
        enabled = buttonEnable
    ){
        Text(stringResource(R.string.crear_juego))
    }
}