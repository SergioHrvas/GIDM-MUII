package com.pandemiagame.org

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pandemiagame.org.ui.theme.PandemiaGameTheme
import androidx.navigation.compose.rememberNavController
import com.pandemiagame.org.data.remote.utils.TokenManager
import com.pandemiagame.org.ui.screens.GameComp
import com.pandemiagame.org.ui.screens.LoginComp
import com.pandemiagame.org.ui.screens.Pantalla1
import com.pandemiagame.org.ui.screens.Pantalla2
import com.pandemiagame.org.ui.screens.Pantalla3
import com.pandemiagame.org.ui.viewmodels.GameViewModel
import com.pandemiagame.org.ui.viewmodels.GameViewModelFactory
import com.pandemiagame.org.ui.viewmodels.LoginViewModel

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
    val loginViewModel: LoginViewModel = viewModel() // Obtener el ViewModel correctamente
    val tm = TokenManager(LocalContext.current);
    val context = LocalContext.current
    val gameViewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(context)
    )
    val token = tm.getToken();

    val startDest = if(token.isNullOrEmpty()) "login" else "home"

    NavHost(
            navController = navController,
            startDestination = startDest,
        ) {
            composable("login") { LoginComp(navController, loginViewModel) }
            composable("home") { Pantalla1(navController, viewModel = gameViewModel) }
            composable("game") { GameComp(viewModel = gameViewModel) }
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
