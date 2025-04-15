package com.pandemiagame.org.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.pandemiagame.org.ui.theme.PandemiaGameTheme
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pandemiagame.org.data.remote.GameResponse
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.viewmodels.GamesViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.pandemiagame.org.ui.viewmodels.GamesViewModelFactory

@Preview
@Composable
fun PreviewGames(){
    val navController = rememberNavController() // Crear un NavController falso para el preview

    PandemiaGameTheme(darkTheme = false){
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)) {
            GamesComp(navController=navController)
        }    }
}


@Composable
fun GamesComp(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val context = LocalContext.current

    val viewModel: GamesViewModel = viewModel(
        factory = GamesViewModelFactory(context.applicationContext)
    )

    val games by viewModel.gamesList.observeAsState(emptyList())
    val selectedGame by viewModel.navegarADetalle.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)


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

fun formatDateTimeCompat(isoDateTime: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return try {
        formatter.format(parser.parse(isoDateTime))
    } catch (e: Exception) {
        "Fecha inválida"
    }
}

// Composable para mostrar cada item del juego
@Composable
fun GameItem(game: GameResponse, onClick: () -> Unit, modifier: Modifier = Modifier) {
    // Implementa cómo quieres mostrar cada juego
    // Por ejemplo:
    Card(modifier = modifier.fillMaxWidth().padding(8.dp), onClick = {
        onClick()
    }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio igual entre todos los hijos
            ) {
                Text(text = "Fecha: " + formatDateTimeCompat(game.date))
                Text(text = "Estado: " + game.status)
                if(game.winner > 0){
                    Text(text = "Ganador: " + game.winner)
                }
            }
            Row (
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio igual entre todos los hijos
            ) {
                Text(text = "Turnos: " + game.numTurns.toString())
                Text(text = "Jugadores: " + game.turns.size)

            }
        }
    }
}
