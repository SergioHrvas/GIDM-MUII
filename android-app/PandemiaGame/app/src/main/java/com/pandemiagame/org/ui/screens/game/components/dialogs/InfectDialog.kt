package com.pandemiagame.org.ui.screens.game.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pandemiagame.org.data.remote.models.game.InfectData
import com.pandemiagame.org.data.remote.models.game.Organ
import com.pandemiagame.org.data.remote.models.game.Player
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

@Composable
fun InfectDialog(
    currentPlayer: Player,
    otherPlayers: List<Player>,
    onConfirm: (InfectData) -> Unit,
    onCancel: () -> Unit
) {
    data class InfectionTarget(var playerId: Int, val organType: String)

    // Mapa para guardar las selecciones: órgano -> jugador objetivo
    var selections = remember {
        mutableStateMapOf<String, InfectionTarget?>().apply {
            currentPlayer.organs
                .filter { it.virus == 1 || it.virus == 2 }
                .forEach { put(it.tipo, null) }
        }
    }

    Dialog(onDismissRequest = onCancel) {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Seleccionar objetivos de infección",
                    style = MaterialTheme.typography.titleLarge
                )

                // Lista de órganos con virus y sus selectores
                currentPlayer.organs
                    .filter { it.virus == 1 || it.virus == 2 }
                    .forEach { organ ->
                        Column {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "${organ.tipo}:",
                                    modifier = Modifier.weight(1f)
                                )

                                // Dropdown para seleccionar jugador objetivo
                                Box(modifier = Modifier.weight(2f)) {
                                    var expanded by remember { mutableStateOf(false) }
                                    var organ2 by remember { mutableStateOf("") }
                                    val targetPlayerAndOrgans = otherPlayers
                                        .flatMap { player ->
                                            player.organs
                                                .filter { targetOrgan ->
                                                    mirarTipo(targetOrgan, organ)
                                                }
                                                .map { targetOrgan -> player to targetOrgan }
                                        }

                                    OutlinedButton(
                                        onClick = { expanded = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(if (selections[organ.tipo]?.organType.toString().isEmpty() == false) organ2 else "Seleccionar órgano")
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        targetPlayerAndOrgans.forEach { (player, targetOrgan) ->
                                            DropdownMenuItem(
                                                text = { Text("${targetOrgan.tipo} de ${player.name}") },
                                                onClick = {
                                                    organ2 = targetOrgan.tipo
                                                    selections[organ.tipo] = InfectionTarget(player.id, targetOrgan.tipo)
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            HorizontalDivider()
                        }
                    }

                // Botones de acción
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        enabled = true
                    ) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            val selectedPairs = selections.mapNotNull { (organ, player) ->
                                player?.let { organ to it }
                            }.take(5)

                            val data = InfectData(
                                player1 = selectedPairs.getOrNull(0)?.second?.playerId,
                                organ1 = selectedPairs.getOrNull(0)?.second?.organType,
                                organ1from = selectedPairs.getOrNull(0)?.first,
                                player2 = selectedPairs.getOrNull(1)?.second?.playerId,
                                organ2 = selectedPairs.getOrNull(1)?.second?.organType,
                                organ2from = selectedPairs.getOrNull(1)?.first,
                                player3 = selectedPairs.getOrNull(2)?.second?.playerId,
                                organ3 = selectedPairs.getOrNull(2)?.second?.organType,
                                organ3from = selectedPairs.getOrNull(2)?.first,
                                player4 = selectedPairs.getOrNull(3)?.second?.playerId,
                                organ4 = selectedPairs.getOrNull(3)?.second?.organType,
                                organ4from = selectedPairs.getOrNull(3)?.first,
                                player5 = selectedPairs.getOrNull(4)?.second?.playerId,
                                organ5 = selectedPairs.getOrNull(4)?.second?.organType,
                                organ5from = selectedPairs.getOrNull(4)?.first,
                            )

                            onConfirm(data)
                        },
                        enabled = selections.values.any { it != null }
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}



private fun mirarTipo(targetOrgan: Organ, organ: Organ): Boolean {
    if (targetOrgan.cure == 3){
        return false
    }
    else if(targetOrgan.cure == 2){
        return true
    }
    else if (organ.virus == 2){
        return true
    }
    else if (targetOrgan.tipo == "magic"){
        return true
    }
    else if((targetOrgan.cure == 0) || (targetOrgan.cure == 1)) {
        if(organ.tipo == "magic"){
            return if((organ.magic_organ == 1) && (targetOrgan.tipo == "heart")){
                true
            } else if((organ.magic_organ == 2) && (targetOrgan.tipo == "brain")){
                true
            } else if((organ.magic_organ == 3) && (targetOrgan.tipo == "intestine")){
                true
            } else if((organ.magic_organ == 4) && (targetOrgan.tipo == "lungs")){
                true
            } else{
                false
            }
        }
        else{
            return (organ.tipo == targetOrgan.tipo)
        }
    }
    else{
        return false
    }
}