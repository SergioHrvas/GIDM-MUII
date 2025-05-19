package com.pandemiagame.org.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.pandemiagame.org.ui.navigation.BottomNavBar
import com.pandemiagame.org.ui.navigation.CustomTopAppBar

@Composable
fun Profile(navController: NavController) {
    Scaffold (
        topBar = { CustomTopAppBar() },
        bottomBar = { BottomNavBar(navController) },
    ){ padding ->
        Text(modifier = Modifier.padding(padding), text = "👤 Perfil de Usuario")
        Column {
            Text(fontSize = 15.em, text = "NOMBRE DE USUARIO")
        }
    }
}