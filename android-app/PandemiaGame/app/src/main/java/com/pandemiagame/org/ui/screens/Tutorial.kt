package com.pandemiagame.org.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pandemiagame.org.R
import com.pandemiagame.org.ui.navigation.BottomNavBar
import com.pandemiagame.org.ui.navigation.CustomTopAppBar

@Composable
fun Tutorial(navController: NavController) {
    Scaffold(
        topBar = { CustomTopAppBar() },
        bottomBar = { BottomNavBar(navController) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TutorialSection(
                title = "🎯 Objetivo del juego",
                imageRes = R.drawable.tutorial_1, // pon aquí tu imagen
                description = "El objetivo del juego es cooperar para contener y erradicar las pandemias que amenazan al mundo."
            )

            TutorialSection(
                title = "🃏 Turno del jugador",
                imageRes = R.drawable.tutorial_1, // pon aquí tu imagen
                description = "Cada jugador realiza acciones como moverse entre ciudades, curar enfermedades, construir centros de investigación o compartir cartas."
            )

            TutorialSection(
                title = "🌍 Propagación",
                imageRes = R.drawable.tutorial_1, // pon aquí tu imagen
                description = "Al final de cada turno, se propagan nuevas infecciones en distintas ciudades. ¡Evita los brotes!"
            )

            TutorialSection(
                title = "🏆 Cómo ganar",
                imageRes = R.drawable.tutorial_1, // pon aquí tu imagen
                description = "El equipo gana al descubrir las curas de las 4 enfermedades antes de que se agoten las cartas o haya demasiados brotes."
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TutorialSection(
    title: String,
    @DrawableRes imageRes: Int,
    description: String
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
        )

        Text(
            text = description,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
