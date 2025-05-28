package com.pandemiagame.org.ui.screens

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.models.CardEnum
import com.pandemiagame.org.ui.navigation.BottomNavBar
import com.pandemiagame.org.ui.navigation.CustomTopAppBar

data class Section(
    val imageRes: Int? = null,
    val title: String,
    val description: String
)

@Composable
fun Tutorial(navController: NavController) {
    Scaffold(
        topBar = { CustomTopAppBar() },
        bottomBar = { BottomNavBar(navController) },
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TutorialSection(
                section = Section(
                    title = "🎯 Objetivo del juego",
                    imageRes = R.drawable.tutorial_1,
                    description = "El objetivo del juego es formar un cuerpo humano juntando cuatro órganos sanos y evitando que el resto de jugadores los infecten o roben."
                ),
)

            TutorialSection(
                section = Section(
                    title = "🃏 Contenido",
                    imageRes = R.drawable.tutorial_2,
                    description = "Existen 68 cartas entre las que están los siguientes tipos: \n" +
                            "\t ÓRGANOS\n" +
                            "\t\t Mágico (1) \n" +
                            "\t\t Corazón (5) \n" +
                            "\t\t Pulmones (5) \n" +
                            "\t\t Intestino (5) \n" +
                            "\t\t Cerebro (5) \n" +
                            "\t CURA\n" +
                            "\t\t Mágico (1) \n" +
                            "\t\t Corazón (4) \n" +
                            "\t\t Pulmones (4) \n" +
                            "\t\t Intestino (4) \n" +
                            "\t\t Cerebro (4) \n" +
                            "\t VIRUS\n" +
                            "\t\t Mágico (1) \n" +
                            "\t\t Corazón (4) \n" +
                            "\t\t Pulmones (4) \n" +
                            "\t\t Intestino (4) \n" +
                            "\t\t Cerebro (4) \n" +
                            "\t ACCION\n" +
                            "\t\t Estornudo (2) \n" +
                            "\t\t Cambiar órgano (3) \n" +
                            "\t\t Robar órgano (3) \n" +
                            "\t\t Cambio de cuerpo (1) \n" +
                            "\t\t Descarte de cartas (1) \n\n" +
                            "A continuación, se muestran todas las cartas:"
                )
            )
            var expanded by remember { mutableStateOf(false) }
            var selectedCard by remember { mutableStateOf(CardEnum.BACKCARD) } // Valor inicial

            Image(
                modifier = Modifier.width(150.dp),
                painter = painterResource(selectedCard.drawable),
                contentDescription = "Carta seleccionada"
            )

            OutlinedButton(onClick = {expanded = true} ) {
                Text(selectedCard.displayName)
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CardEnum.entries.forEach { card ->
                        DropdownMenuItem(
                            text = { Text(card.displayName) },
                            onClick = {
                                selectedCard = card
                                expanded = false
                            }
                        )
                    }

                }
            }

            TutorialSection(
                section = Section(
                    title = "🌍 Mecánica del juego",
                    description = "Al inicio se reparten 3 cartas a cada jugador. En cada turno se podrá " +
                            "realizar solamente una acción: descartar cartas de la mano o jugar una de esas cartas. " +
                            "Tras la acción, se robarán el número de cartas correspondientes para volver a tener 3. " +
                            "El jugador podrá realizar su acción sobre su cuerpo o sobre el de otro jugador, en función del tipo de carta que utilice." +
                            "¡Sé el primero en construir un cuerpo completo sano!"
                )
            )

            TutorialSection(
                section = Section(
                    title = "🏆 Tipos de carta",
                    description = "A continuación explicamos detalladamente cada tipo de carta."

                )
            )

            TutorialSubsection(
                section = Section(
                    title = "Órganos",
                    imageRes = R.drawable.organ_heart,
                    description = "Los órganos son las partes de un cuerpo. Para completar este y ganar, debes reunir cuatro órganos" +
                            "distintos y sanos. No se pueden tener más de dos órganos iguales. Se pueden tener hasta cinco órganos distintos (cerebro," +
                            "corazón, intestino, pulmones y mágico."
                )
            )

            TutorialSubsection(
                section = Section(
                    title = "Virus",
                    imageRes = R.drawable.virus_brain,
                    description = "Los virus son cartas que se usan sobre el cuerpo de otro jugador. Podrás infectar o destruir órganos o quitar medicinas siempre y cuando sean del mismo color."
                ),
                subsubsection = listOf<Section>(
                    Section(
                        title = "INFECTAR",
                        description = "Si usas un virus sobre un órgano libre, añades el virus. Mientras tenga el virus, este órgano no se contará" +
                                "para completar el cuerpo y ganar"
                    ),
                    Section(
                        title = "DESTRUIR",
                        description = "Si usas un virus sobre un órgano con virus, el órgano se destruye y desaparece del cuerpo del jugador."
                    ),
                    Section(
                        title = "QUITAR CURA",
                        description = "Si usas un virus sobre un órgano con cura, la cura desaparece y el órgano queda libre."
                    ),
                    Section(
                        title = "FALLLO POR INMUNE",
                        description = "No puedes usar un virus sobre un órgano inmunizado (doble cura)."
                    )

                )
            )
            TutorialSection(
                section = Section(
                    title = "🏆 Cómo ganar",
                    imageRes = R.drawable.tutorial_1,
                    description = "El equipo gana al descubrir las curas de las 4 enfermedades antes de que se agoten las cartas o haya demasiados brotes."
                )
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TutorialSection(
    section: Section
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = section.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if(section.imageRes != null) {
            Image(
                painter = painterResource(id = section.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        Text(
            text = section.description,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Composable
fun TutorialSubsection(
    section: Section,
    subsubsection: List<Section>? = null
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row() {
            if(section.imageRes != null) {
                Image(
                    painter = painterResource(id = section.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .height(20.dp)
                )
            }
            Text(
                text = section.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

        }

        Text(
            text = section.description,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        subsubsection?.forEach {
            TutorialSubSubsection(it)
        }
    }


}


@Composable
fun TutorialSubSubsection(
    section: Section
) {
        Row() {
            if(section.imageRes != null) {
                Image(
                    painter = painterResource(id = section.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .height(20.dp)
                )
            }
            Column () {
                Text(
                    text = section.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = section.description,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
}
