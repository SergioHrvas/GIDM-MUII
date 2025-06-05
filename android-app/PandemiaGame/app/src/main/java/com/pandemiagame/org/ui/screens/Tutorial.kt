package com.pandemiagame.org.ui.screens

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
import androidx.compose.ui.res.stringResource
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
                    title = stringResource(R.string.tutorial_title_goal),
                    imageRes = R.drawable.tutorial_1,
                    description = stringResource(R.string.tutorial_description_goal)
                ),
)

            TutorialSection(
                section = Section(
                    title = stringResource(R.string.tutorial_title_content),
                    imageRes = R.drawable.tutorial_2,
                    description = stringResource(R.string.tutorial_description_content),
                )
            )
            var expanded by remember { mutableStateOf(false) }
            var selectedCard by remember { mutableStateOf(CardEnum.BACKCARD) } // Valor inicial

            Image(
                modifier = Modifier.width(150.dp),
                painter = painterResource(selectedCard.drawable),
                contentDescription = stringResource(R.string.carta_seleccionada)
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
                    title = stringResource(R.string.tutorial_title_mechanics),
                    description = stringResource(R.string.tutorial_description_mechanics)
                )
            )

            TutorialSection(
                section = Section(
                    title = stringResource(R.string.tutorial_title_card_types),
                    description = stringResource(R.string.tutorial_description_card_types)

                )
            )

            TutorialSubsection(
                section = Section(
                    title = stringResource(R.string.tutorial_title_organs),
                    imageRes = R.drawable.organ_heart,
                    description = stringResource(R.string.tutorial_description_organs)
                )
            )

            TutorialSubsection(
                section = Section(
                    title = stringResource(R.string.tutorial_title_virus),
                    imageRes = R.drawable.virus_brain,
                    description = stringResource(R.string.tutorial_description_virus),
                ),
                subsubsection = listOf<Section>(
                    Section(
                        imageRes = R.drawable.tutorial_3,
                        title = stringResource(R.string.tutorial_subtitle_infectar),
                        description = stringResource(R.string.tutorial_description_infectar),
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_4,
                        title = stringResource(R.string.tutorial_subtitle_destruir),
                        description = stringResource(R.string.tutorial_description_destruir),
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_5,
                        title = stringResource(R.string.tutorial_subtitle_quitar_cura),
                        description = stringResource(R.string.tutorial_description_quitar_cura),
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_6,
                        title = stringResource(R.string.tutorial_subtitle_fallo_inmune),
                        description = stringResource(R.string.tutorial_description_fallo_inmune),
                    )
                )
            )


            TutorialSubsection(
                section = Section(
                    title = stringResource(R.string.tutorial_title_cura),
                    imageRes = R.drawable.cure_intestine,
                    description = stringResource(R.string.tutorial_description_cura)
                ),
                subsubsection = listOf<Section>(
                    Section(
                        imageRes = R.drawable.tutorial_7,
                        title = stringResource(R.string.tutorial_subtitle_proteger),
                        description = stringResource(R.string.tutorial_description_proteger)
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_9,
                        title = stringResource(R.string.tutorial_subtitle_curar),
                        description = stringResource(R.string.tutorial_description_curar)
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_8,
                        title = stringResource(R.string.tutorial_subtitle_inmunizar),
                        description = stringResource(R.string.tutorial_description_inmunizar)
                    ),
                )
            )

            TutorialSubsection(
                section = Section(
                    title = stringResource(R.string.tutorial_title_acciones),
                    imageRes = R.drawable.steal,
                    description = stringResource(R.string.tutorial_description_acciones)
                ),
                subsubsection = listOf<Section>(
                    Section(
                        imageRes = R.drawable.tutorial_13,
                        title = stringResource(R.string.tutorial_subtitle_intercambio),
                        description = stringResource(R.string.tutorial_description_intercambio)
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_10,
                        title = stringResource(R.string.tutorial_subtitle_robar),
                        description = stringResource(R.string.tutorial_description_robar)
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_14,
                        title = stringResource(R.string.tutorial_subtitle_descartar),
                        description = stringResource(R.string.tutorial_description_descartar)
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_12,
                        title =stringResource(R.string.tutorial_subtitle_cambio_cuerpo),
                        description = stringResource(R.string.tutorial_description_cambio_cuerpo)
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_11,
                        title = stringResource(R.string.tutorial_subtitle_estornudo),
                        description = stringResource(R.string.tutorial_description_estornudo)
                    )
                )
            )

            TutorialSubsection(
                section = Section(
                    title = stringResource(R.string.tutorial_title_magicas),
                    imageRes = R.drawable.cure_intestine,
                    description = stringResource(R.string.tutorial_description_magicas)
                ),
                subsubsection = listOf<Section>(
                    Section(
                        imageRes = R.drawable.tutorial_15,
                        title = stringResource(R.string.tutorial_subtitle_organo_magico),
                        description = stringResource(R.string.tutorial_description_organo_magico)
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_16,
                        title = stringResource(R.string.tutorial_subtitle_virus_magico),
                        description = stringResource(R.string.tutorial_description_virus_magico)
                    ),
                    Section(
                        imageRes = R.drawable.tutorial_17,
                        title = stringResource(R.string.tutorial_subtitle_cura_magica),
                        description = stringResource(R.string.tutorial_description_cura_magica)
                    ),
                )
            )

            TutorialSection(
                section = Section(
                    title = stringResource(R.string.tutorial_title_how_to_win),
                    imageRes = R.drawable.tutorial_1,
                    description = stringResource(R.string.tutorial_description_how_to_win),
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
                contentDescription = section.title,
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
                    contentDescription = section.title,
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
                    contentDescription = section.title,
                    modifier = Modifier
                        .width(90.dp).padding(end = 5.dp)
                )
            }
            Column {
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
