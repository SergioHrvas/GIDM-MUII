package com.pandemiagame.org.ui.screens.game.components.players

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.pandemiagame.org.R
import com.pandemiagame.org.ui.screens.game.components.utils.GameState
import com.pandemiagame.org.ui.viewmodels.GameViewModel

@Composable
fun MenuButton(gameState: GameState,
               viewModel: GameViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Registro", "Chat", "Consejo", "Rendirme")
    Box {
        Button(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = "Menú"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        expanded = false
                        when (option) {
                            "Registro" -> {
                                gameState.seeingMoves = true
                                viewModel.getMoves(viewModel.game.value?.id.toString())
                            }
                            "Consejo" -> {
                                viewModel.getRecommendations(viewModel.game.value?.turn ?: 0)
                            }
                            "Chat" -> { /* No implementado */ }
                            "Rendirme" -> {     val gameResponse = gameState.gameResponse
                                viewModel.surrender(currentTurn = gameResponse?.players?.getOrNull(gameState.currentPlayerIndex)?.id?: 0) }
                        }
                    }
                )
            }
        }
    }
}
