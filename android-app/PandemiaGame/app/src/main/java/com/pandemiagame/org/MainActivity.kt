package com.pandemiagame.org

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.pandemiagame.org.ui.screens.GamesComp
import com.pandemiagame.org.ui.screens.LoginComp
import com.pandemiagame.org.ui.screens.NewGameComp
import com.pandemiagame.org.ui.screens.MainScreen
import com.pandemiagame.org.ui.screens.Pantalla2
import com.pandemiagame.org.ui.screens.Pantalla3
import com.pandemiagame.org.ui.viewmodels.GameViewModel
import com.pandemiagame.org.ui.viewmodels.GameViewModelFactory
import com.pandemiagame.org.ui.viewmodels.NewGameViewModelFactory
import com.pandemiagame.org.ui.viewmodels.LoginViewModel
import com.pandemiagame.org.ui.viewmodels.NewGameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PandemiaGameTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val tm = TokenManager(LocalContext.current)

    // Observar cambios en el token
    var token by remember { mutableStateOf(tm.getToken() ?: "") }

    // Observar el éxito del login
    val loginSuccess by loginViewModel.loginSuccess.observeAsState()

    // Efecto para manejar éxito de login
    LaunchedEffect(loginSuccess) {
        if (loginSuccess == true) {
            token = tm.getToken() ?: ""
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // Efecto para manejar cambios en el token
    LaunchedEffect(token) {
        if (token.isEmpty()) {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val context = LocalContext.current
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(context))
    val newGameViewModel: NewGameViewModel = viewModel(factory = NewGameViewModelFactory(context))


    println(token.isNullOrEmpty())

    NavHost(
        navController = navController,
        startDestination = if (token.isNullOrEmpty()) "login" else "home"
    ) {
        composable("login") { LoginComp(navController, loginViewModel) }
        composable("home") { MainScreen(navController,
            onLogout = {
                tm.clearToken()
                token = ""
                loginViewModel.resetLoginState() // Resetear estado de login
            }
        ) }
        composable("games") { GamesComp(navController = navController) }
        composable("create-game") { NewGameComp(viewModel = newGameViewModel, navController = navController) }
        composable("game/{game_id}") { backStackEntry ->
            GameComp(
                viewModel = gameViewModel,
                gameId = backStackEntry.arguments?.getString("game_id") ?: "",
                navController = navController
            )
        }
        composable("profile") { Pantalla2(navController) }
        composable("settings") { Pantalla3(navController) }
    }
}
