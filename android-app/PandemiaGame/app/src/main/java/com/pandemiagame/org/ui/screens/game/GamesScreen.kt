package com.pandemiagame.org.ui.screens.game

import com.pandemiagame.org.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.viewmodels.GamesViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.navigation.NavController
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.pandemiagame.org.data.remote.utils.TokenManager
import com.pandemiagame.org.ui.screens.game.components.ButtonFilterGames
import com.pandemiagame.org.ui.screens.game.components.GameItem
import com.pandemiagame.org.ui.viewmodels.GamesViewModelFactory

@Composable
fun GamesComp(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val viewModel: GamesViewModel = viewModel(
        factory = GamesViewModelFactory(tokenManager)
    )

    val games by viewModel.gamesListDisplayed.observeAsState(emptyList())
    val selectedGame by viewModel.navegarADetalle.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    var mode by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.getMyGames(context)
    }

    // Observar navegación
    LaunchedEffect(selectedGame) {
        selectedGame?.let { game ->
            navController.navigate("game/${game.id}")
            viewModel.navegacionCompletada()
        }
    }

    Scaffold(
        topBar = { CustomTopAppBar() },
    ) { innerPadding ->
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    ButtonFilterGames(
                        onClick = {
                            mode = 0
                            viewModel.setGameDisplayed(mode)
                        },
                        enabled = mode!=0,
                        painterResource = R.drawable.baseline_filter_list_off_24,
                        stringResource = R.string.sin_filtros
                    )
                    ButtonFilterGames(
                        onClick = {
                            mode = 1
                            viewModel.setGameDisplayed(mode)
                        },
                        enabled = mode!=1,
                        painterResource = R.drawable.baseline_person_24,
                        stringResource = R.string.un_dispositivo
                    )
                    ButtonFilterGames(
                        onClick = {
                            mode = 2
                            viewModel.setGameDisplayed(mode)
                        },
                        enabled = mode!=2,
                        painterResource = R.drawable.baseline_people_24,
                        stringResource = R.string.multijugador
                    )
                }

                LazyColumn {
                    items(games) { game ->
                        GameItem(game = game, onClick = {
                            viewModel.seleccionarJuego(game)
                        }
                        )
                    }
                }
            }
        }
    }
}