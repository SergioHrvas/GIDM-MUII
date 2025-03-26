package com.pandemiagame.org.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(Screen.Home, Screen.Profile, Screen.Settings)
    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }

    NavigationBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
        items.forEachIndexed { index, screen ->
            val isSelected = selectedNavigationIndex.intValue == index

            NavigationBarItem(
                icon = { Icon(
                    modifier = Modifier.size(42.dp),
                    imageVector = screen.icon,
                    contentDescription = screen.label,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.White // Color del icono
                ) },
                selected = isSelected,
                onClick = {
                        selectedNavigationIndex.intValue = index
                        navController.navigate(screen.route)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary, // Color del icono seleccionado
                    unselectedIconColor = Color.White,  // Color del icono no seleccionado
                    indicatorColor = Color.Transparent // Evita que el fondo sobrescriba colores
                )
            )
        }
    }
}
