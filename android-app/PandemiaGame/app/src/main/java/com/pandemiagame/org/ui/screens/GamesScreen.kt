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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Preview
@Composable
fun PreviewGames(){
    PandemiaGameTheme(darkTheme = false){
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)) {
            GamesComp()
        }    }
}



@Composable
fun GamesComp(
    modifier: Modifier = Modifier,
    viewModel: GamesViewModel = viewModel()
) {
    val games by viewModel.gamesList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    // Llamar a getMyGames cuando se inicia el composable
    LaunchedEffect(Unit) {
        viewModel.getMyGames { errorMessage ->
            println("Error: $errorMessage")
        }
    }

    Scaffold(
        topBar = { CustomTopAppBar() },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(games) { game ->
                        GameItem(game = game)
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
fun GameItem(game: GameResponse, modifier: Modifier = Modifier) {
    // Implementa cómo quieres mostrar cada juego
    // Por ejemplo:
    Card(modifier = modifier.fillMaxWidth().padding(8.dp)) {
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
