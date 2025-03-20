package com.pandemiagame.org

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pandemiagame.org.navigation.BottomNavBar
import com.pandemiagame.org.ui.theme.PandemiaGameTheme
import androidx.navigation.compose.rememberNavController
import com.pandemiagame.org.model.LoginViewModel
import com.pandemiagame.org.model.Screen
import com.pandemiagame.org.navigation.CustomTopAppBar
import com.pandemiagame.org.screen.GameActivity
import com.pandemiagame.org.screen.GameComp
import com.pandemiagame.org.screen.LoginComp
import com.pandemiagame.org.screen.Pantalla1
import com.pandemiagame.org.screen.Pantalla2
import com.pandemiagame.org.screen.Pantalla3

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PandemiaGameTheme {
                AppNavigation()
                /*val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        CustomTopAppBar()

                    },
                    bottomBar = { BottomNavBar(navController) },
                    floatingActionButton = {
                        val context = LocalContext.current
                        val intent = Intent(context, GameActivity::class.java) // Create intent first
                        FloatingActionButton(onClick = {
                            context.startActivity(intent) // Start the activity on click
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Start Game")
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) { paddingValues ->
                    NavGraph(navController, Modifier.padding(paddingValues))
                }*/

            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRoute = currentRoute(navController)
    val viewModel: LoginViewModel = viewModel() // Obtener el ViewModel correctamente
    NavHost(
            navController = navController,
            startDestination = "login",
        ) {
            composable("login") { LoginComp(navController, viewModel) }
            composable("home") { Pantalla1(navController) }
            composable("game") { GameComp() }
            composable("profile") { Pantalla2(navController) }
            composable("settings") { Pantalla3(navController) }
    }
}

// Función para obtener la ruta actual
@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}



fun ScaffoldTopBar(){

}
