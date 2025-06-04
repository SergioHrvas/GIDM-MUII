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
import com.pandemiagame.org.data.remote.models.game.CardEnum
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
                        imageRes = R.drawable.tutorial_3,
                        title = "INFECTAR",
                        description = "Si usas un virus sobre un órgano libre, añades el virus. Mientras tenga el virus, este órgano no se contará" +
                                "para completar el cuerpo y ganar"
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_4,
                        title = "DESTRUIR",
                        description = "Si usas un virus sobre un órgano con virus, el órgano se destruye y desaparece del cuerpo del jugador."
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_5,
                        title = "QUITAR CURA",
                        description = "Si usas un virus sobre un órgano con cura, la cura desaparece y el órgano queda libre."
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_6,
                        title = "FALLLO POR INMUNE",
                        description = "No puedes usar un virus sobre un órgano inmunizado (doble cura)."
                    )

                )
            )


            TutorialSubsection(
                section = Section(
                    title = "Cura",
                    imageRes = R.drawable.cure_intestine,
                    description = "Las curas son cartas que se usan sobre tu propio cuerpo para curar y proteger tus órganos. Podrás utilizarlas sobre tu cuerpo siempre y cuando el órgano sea del mismo color."
                ),
                subsubsection = listOf<Section>(
                    Section(
                        imageRes = R.drawable.tutorial_7,
                        title = "PROTEGER",
                        description = "Si usas una cura sobre un órgano libre, añades la cura. Esta cura estará presente hasta que un usuario te la quite por virus."
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_9,
                        title = "CURAR",
                        description = "Si usas una cura sobre un órgano con virus, la cura desaparece y el órgano queda libre y sin protección."
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_8,
                        title = "INMUNIZAR",
                        description = "Si usas una cura sobre un órgano con cura, el órgano queda inmunizado, siendo imposible de robar, destruir o infectar."
                    ),
                )
            )

            TutorialSubsection(
                section = Section(
                    title = "Acciones",
                    imageRes = R.drawable.steal,
                    description = "Las acciones son cartas especiales que te ayudarán a ganar mejorando tu cuerpo o empeorando los de tus rivales."
                ),
                subsubsection = listOf<Section>(
                    Section(
                        imageRes = R.drawable.tutorial_13,
                        title = "INTERCAMBIO DE ÓRGANO",
                        description = "Si usas esta carta, podrás intercambiar uno de tus órganos con otro de otro jugador siempre y cuando los jugadores no tengan los órganos a adquirir" +
                                "y no estén inmunizados."
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_10,
                        title = "ROBAR ÓRGANO",
                        description = "Con esta carta podrás robar un órgano a otro jugador siempre y cuando no tengas dicho órgano en tu cuerpo y no esté inmunizado."
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_14,
                        title = "DESCARTAR CARTAS",
                        description = "Esta carta obliga a tus oponentes a descartar todas sus cartas en la mano y volver a robar del mazo, volviendo el turno a ti."
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_12,
                        title = "CAMBIO DE CUERPO",
                        description = "Si usas esta carta cambiarás tu cuerpo con el del otro jugador, incluyendo todos los órganos, virus y curas." +
                                " No importa si hay órganos inmunizados: también se cambian."
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_11,
                        title = "ESTORNUDO",
                        description = "Con esta carta podrás repartir los virus que tienen tus órganos por los cuerpos del resto de jugadores." +
                                " No importa si hay órganos inmunizados: también se cambian."
                    )
                )
            )

            TutorialSubsection(
                section = Section(
                    title = "Cartas mágicas",
                    imageRes = R.drawable.cure_intestine,
                    description = "Las cartas mágicas son un color de carta especial de órgano, virus y cura. Pueden afectar a cualquier color, lo cual les da muchisimo poder; pero también pueden ser muy débiles porque sobre ellas pueden usarse virus de cualquier color."
                ),
                subsubsection = listOf<Section>(
                    Section(
                        imageRes = R.drawable.tutorial_15,
                        title = "ÓRGANO MÁGICO",
                        description = "Puede curarse, protegerse y curarse con cualquier cura, pero también puede infectarse y destruirse con cualquier cura."
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_16,
                        title = "VIRUS MÁGICO",
                        description = "Puede usarse sobre cualquier órgano o cura, pero también se puede extirpar (curar) con una cura de cualquier color."
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_17,
                        title = "CURA MÁGICA",
                        description = "Puede usarse sobre cualquier órgano o virus, pero también se puede quitar con un virus de cualquier color."
                    ),
                )
            )

            TutorialSection(
                section = Section(
                    title = "🏆 Cómo ganar",
                    imageRes = R.drawable.tutorial_1,
                    description = "El jugador gana automáticamente al reunir cuatro de los cinco órganos sanos (sin virus)."
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            if(section.imageRes != null) {
                Image(
                    painter = painterResource(id = section.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .height(40.dp).padding(end = 5.dp)
                )
            }
            Text(
                text = section.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
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
        Row(modifier = Modifier.padding(bottom = 10.dp)) {
            if(section.imageRes != null) {
                Image(
                    painter = painterResource(id = section.imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .width(90.dp).padding(end = 5.dp)
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
