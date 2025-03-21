package com.pandemiagame.org.navigation

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import com.pandemiagame.org.model.Screen
import com.pandemiagame.org.screen.Pantalla1
import com.pandemiagame.org.screen.Pantalla2
import com.pandemiagame.org.screen.Pantalla3
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandemiagame.org.screen.Login
import com.pandemiagame.org.screen.LoginComp

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
