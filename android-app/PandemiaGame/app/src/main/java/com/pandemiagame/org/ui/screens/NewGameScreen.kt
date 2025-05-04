package com.pandemiagame.org.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pandemiagame.org.R
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.theme.PandemiaGameTheme
import com.pandemiagame.org.ui.viewmodels.NewGameViewModel
import kotlinx.coroutines.launch
import kotlin.collections.set

const val MAX_PLAYERS = 5


@Preview
@Composable
fun PreviewNewGame(){
    PandemiaGameTheme(darkTheme = false){
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)) {
            NewGameComp(navController = NavController(
                context = TODO()
            ))
        }    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameComp(viewModel: NewGameViewModel = viewModel(), navController: NavController) {
    val buttonEnable :Boolean by viewModel.buttonEnable.observeAsState(initial=false)  // Estado para el boton activado

    val isLoading :Boolean by viewModel.isLoading.observeAsState(initial=false) // Estado para el cargando
    val coroutine = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val multiplayer: Boolean by viewModel.multiplayer.observeAsState(initial=false)

    LaunchedEffect(Unit) {
        viewModel.getUsers()
    }
    // Llamamos al estado de la creación del juego
    val gameCreationStatus by viewModel.gameCreationStatus.observeAsState(false)

    Scaffold (
        topBar = { CustomTopAppBar() },
    ) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding() // Esto añade padding cuando el teclado aparece
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState), // Hace scrollable el contenido
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Botón para añadir jugador (disabled cuando se alcanza el máximo)
                    IconButton(
                        onClick = { viewModel.changeMultiplayer() },
                    ) {
                        if(!multiplayer)Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                        else
                            Icon(
                                Icons.Default.KeyboardArrowLeft,
                                contentDescription = null
                            )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.login), // Nombre sin extensión
                        contentDescription = "Icono vectorial",
                        contentScale = ContentScale.Fit // Mantiene proporciones sin recortar
                    )
                    Box(
                        modifier = Modifier
                            .padding(14.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            repeat(viewModel.playerNames.size) { index ->
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    if(!multiplayer)TextField(
                                        value = viewModel.playerNames.getOrElse(index) { "" },
                                        onValueChange = { newName -> viewModel.onNameChanged(newName, index) },
                                        label = { Text("Jugador ${index + 1}") },
                                        modifier = Modifier
                                            .weight(1f)  // Ocupa todo el espacio disponible
                                            .padding(end = 8.dp)  // Espacio entre TextField y Button
                                    )
                                    else {
                                        var expanded by remember { mutableStateOf(false) }

                                        OutlinedButton(
                                            onClick = { expanded = true},
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(if (viewModel.playerNames[index].toString().isEmpty() == false) viewModel.playerNames[index] else " --- ")
                                            Icon(
                                                Icons.Default.ArrowDropDown,
                                                contentDescription = null
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            viewModel.users.value?.forEach { user ->
                                                DropdownMenuItem(
                                                    text = { Text("${user.id} - ${user.username}") },
                                                    onClick = {
                                                        viewModel.onNameChanged(user.id.toString(), index)
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    IconButton(
                                        modifier = Modifier.size(48.dp),
                                        onClick = { viewModel.removePlayer(index) },
                                        enabled = (viewModel.playerNames.size - 1) > 1
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar jugador",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            Row{
                                // Contador de jugadores
                                Text(
                                    text = "${viewModel.playerNames.size} Jugador${if (viewModel.playerNames.size != 1) "es" else ""}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                // Botón para añadir jugador (disabled cuando se alcanza el máximo)
                                IconButton(
                                    onClick = { viewModel.addPlayer() },
                                    enabled = (viewModel.playerNames.size < MAX_PLAYERS)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            }

                            NewGameButton(buttonEnable) {
                                coroutine.launch {
                                    viewModel.onButtonSelected()
                                }
                            }
                            // Observamos el estado de la creación y si es exitosa, navegamos
                            if (gameCreationStatus) {
                                LaunchedEffect(gameCreationStatus) {
                                    viewModel.notCreating()
                                    navController.navigate("game/${viewModel.game.value?.id}") {
                                        popUpTo("create-game") { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                }
                }
            }
        }
    }



@Composable
fun NewGameButton(buttonEnable: Boolean, onButtonSelected: () -> Unit){
    Button(
        onClick = {
            onButtonSelected()},
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(top = 10.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color.Gray,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContentColor = Color.White,
            contentColor = Color.White,
        ),
        enabled = buttonEnable
    ){
        Text(text = "Crear juego")
    }
}

