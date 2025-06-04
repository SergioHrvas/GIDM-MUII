package com.pandemiagame.org.ui.screens.game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.models.game.Organ
import kotlin.collections.forEach


@Composable
fun Body(myBody: Boolean, organs: List<Organ>, onOrganSelected: (String) -> Unit = {}){
    // 0 = No lo tiene
    // 1 = Sí lo tiene
    // 2 = Lo tiene con cura
    // 3 = Lo tiene con cura magica
    // 4 = Lo tiene protegido
    // -1 = Lo tiene infectado
    // -2 = Lo tiene infectado magico

    // Calcula organPlace directamente basado en los organs recibidos
    val organPlace = remember(organs) {
        IntArray(5).apply {
            organs.forEach { organ ->
                when (organ.tipo) {
                    "brain" -> if (organ.cure == 1) this[0] = 2 else if (organ.cure == 2) this[0] = 3 else if (organ.cure == 3) this[0] = 4 else if(organ.virus == 1) this[0] = -1 else if(organ.virus == 2) this[0] = -2 else this[0] = 1
                    "heart" ->  if (organ.cure == 1) this[1] = 2 else if (organ.cure == 2) this[1] = 3 else if (organ.cure == 3) this[1] = 4 else if(organ.virus == 1) this[1] = -1 else if(organ.virus == 2) this[1] = -2 else this[1] = 1
                    "lungs" ->  if (organ.cure == 1) this[2] = 2 else if (organ.cure == 2) this[2] = 3 else if (organ.cure == 3) this[2] = 4 else if(organ.virus == 1) this[2] = -1 else if(organ.virus == 2) this[2] = -2 else this[2] = 1
                    "intestine" ->  if (organ.cure == 1) this[3] = 2 else if (organ.cure == 2) this[3] = 3 else if (organ.cure == 3) this[3] = 4 else if(organ.virus == 1) this[3] = -1 else if(organ.virus == 2) this[3] = -2 else this[3] = 1
                    "magic" ->  {
                        if (organ.cure == 1){
                            if(organ.magic_organ == 1){
                                this[4] = 2
                            }
                            else if(organ.magic_organ == 2){
                                this[4] = 3
                            }
                            else if(organ.magic_organ == 3){
                                this[4] = 4
                            }
                            else if(organ.magic_organ == 4){
                                this[4] = 5
                            }
                        } else if (organ.cure == 2) {
                            this[4] = 6
                        } else if (organ.cure == 3) {
                            this[4] = 7
                        } else if(organ.virus == 1) {
                            if(organ.magic_organ == 1){
                                this[4] = -1
                            }
                            else if(organ.magic_organ == 2){
                                this[4] = -2
                            }
                            else if(organ.magic_organ == 3){
                                this[4] = -3
                            }
                            else if(organ.magic_organ == 4){
                                this[4] = -4
                            }
                        } else if(organ.virus == 2) {
                            this[4] = -5
                        } else {
                            this[4] = 1
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(5.dp),
        contentAlignment = Alignment.Center // Centra el contenido dentro del Box
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if(myBody) 10.dp else 20.dp)
        ) {
            Image(
                painter = painterResource(id = if(organPlace[0] == 1) R.drawable.brain else if(organPlace[0] == 2) R.drawable.brain_cure else if(organPlace[0] == 3) R.drawable.brain_cure_magic else if (organPlace[0] == 4) R.drawable.brain_protected else if(organPlace[0] == -1) R.drawable.brain_virus else if(organPlace[0] == -2) R.drawable.brain_virus_magic else R.drawable.brain_disable),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(if (myBody) 60.dp else 45.dp)
                    .clickable {
                        if (organPlace[0] != 0) {
                            onOrganSelected("brain")
                        }
                    }
            )
            Image(
                painter = painterResource(id = if(organPlace[1] == 1) R.drawable.heart else if(organPlace[1] == 2) R.drawable.heart_cure else if(organPlace[1] == 3) R.drawable.heart_cure_magic else if (organPlace[1] == 4) R.drawable.heart_protected else if(organPlace[1] == -1) R.drawable.heart_virus else if(organPlace[1] == -2) R.drawable.heart_virus_magic else R.drawable.heart_disable),
                contentDescription = "Corazón",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(if (myBody) 60.dp else 45.dp)
                    .clickable {
                        if (organPlace[1] != 0) {
                            onOrganSelected("heart")
                        }
                    }
            )
            Image(
                painter = painterResource(id = if(organPlace[2] == 1) R.drawable.lungs else if(organPlace[2] == 2) R.drawable.lungs_cure else if(organPlace[2] == 3) R.drawable.lungs_cure_magic else if (organPlace[2] == 4) R.drawable.lungs_protected else if(organPlace[2] == -1) R.drawable.lungs_virus else if(organPlace[2] == -2) R.drawable.lungs_virus_magic else R.drawable.lungs_disable),
                contentDescription = "Pulmones",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(if (myBody) 60.dp else 45.dp)
                    .clickable {
                        if (organPlace[2] != 0) {
                            onOrganSelected("lungs")
                        }
                    }

            )
            Image(
                contentDescription = "Intestino",
                painter = painterResource(id = if(organPlace[3] == 1) R.drawable.intestine else if(organPlace[3] == 2) R.drawable.intestine_cure else if(organPlace[3] == 3) R.drawable.intestine_cure_magic else if (organPlace[3] == 4) R.drawable.intestine_protected else if(organPlace[3] == -1) R.drawable.intestine_virus else if(organPlace[3] == -2) R.drawable.intestine_virus_magic else R.drawable.intestine_disable),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(if (myBody) 60.dp else 45.dp)
                    .clickable {
                        if (organPlace[3] != 0) {
                            onOrganSelected("intestine")
                        }
                    }
            )
            Image(
                contentDescription = "Magic",
                painter = painterResource(id = if(organPlace[4] == 1) R.drawable.magic_organ else if(organPlace[4] == 2) R.drawable.magic_organ_cure_heart else if(organPlace[4] == 3) R.drawable.magic_organ_cure_brain else if (organPlace[4] == 4) R.drawable.magic_organ_cure_intestine else if(organPlace[4] == 5) R.drawable.magic_organ_cure_lungs else if(organPlace[4] == 6) R.drawable.magic_cure else if(organPlace[4] == 7) R.drawable.magic_protected else if(organPlace[4] == -1) R.drawable.magic_organ_virus_heart else if(organPlace[4] == -2) R.drawable.magic_organ_virus_brain else if (organPlace[4] == -3) R.drawable.magic_organ_virus_intestine else if(organPlace[4] == -4) R.drawable.magic_organ_virus_lungs else if(organPlace[4] == -5) R.drawable.magic_virus else R.drawable.magic_disable),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(if (myBody) 60.dp else 45.dp)
                    .clickable {
                        if (organPlace[4] != 0) {
                            onOrganSelected("magic")
                        }
                    }
            )
        }
    }
}

