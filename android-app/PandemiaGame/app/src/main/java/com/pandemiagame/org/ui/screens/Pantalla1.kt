package com.pandemiagame.org.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.pandemiagame.org.ui.navigation.BottomNavBar
import com.pandemiagame.org.ui.navigation.CustomTopAppBar

@Composable
fun Pantalla1(navController: NavController) {
    Scaffold(
        topBar = { CustomTopAppBar() },
        bottomBar = { BottomNavBar(navController) },
    ) { padding ->
        Text(modifier = Modifier.padding(padding), text = "🏠 Pantalla de Inicio")
    }
}
