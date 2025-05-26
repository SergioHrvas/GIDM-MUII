package com.pandemiagame.org

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pandemiagame.org.ui.theme.PandemiaGameTheme
import androidx.navigation.compose.rememberNavController
import com.pandemiagame.org.data.remote.utils.TokenManager
import com.pandemiagame.org.ui.screens.EditProfileComp
import com.pandemiagame.org.ui.screens.GameComp
import com.pandemiagame.org.ui.screens.GamesComp
import com.pandemiagame.org.ui.screens.LoginComp
import com.pandemiagame.org.ui.screens.NewGameComp
import com.pandemiagame.org.ui.screens.MainScreen
import com.pandemiagame.org.ui.screens.Profile
import com.pandemiagame.org.ui.screens.Tutorial
import com.pandemiagame.org.ui.screens.SplashScreen
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
    val context = LocalContext.current
    val tm = TokenManager(context)

    LaunchedEffect(Unit) {
        loginViewModel.checkAuthState(context)
    }

    val authState by loginViewModel.authState.observeAsState()


    LaunchedEffect(authState) {
        when(authState){
            is LoginViewModel.AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
            is LoginViewModel.AuthState.Unauthenticated -> {
                navController.navigate("login"){
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
            is LoginViewModel.AuthState.Error -> {
                navController.navigate("login"){
                    popUpTo("splash") { inclusive = true }
                    launchSingleTop = true
                }
            }
            else -> { }
        }
    }

    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(context))
    val newGameViewModel: NewGameViewModel = viewModel(factory = NewGameViewModelFactory(context))

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") { SplashScreen() }
        composable("login") { LoginComp(navController, loginViewModel) }
        composable("home") { MainScreen(navController,
            onLogout = {
                tm.clearToken()
                loginViewModel.checkAuthState(context)
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
        composable("profile") { Profile(navController) }
        composable("tutorial") { Tutorial(navController) }
        composable("edit-profile") { EditProfileComp(navController) }
    }
}
