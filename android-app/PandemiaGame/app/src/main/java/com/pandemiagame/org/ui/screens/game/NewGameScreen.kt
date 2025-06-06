package com.pandemiagame.org.ui.screens.game

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.models.user.User
import com.pandemiagame.org.data.remote.utils.TokenManager
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.screens.game.components.NewGameButton
import com.pandemiagame.org.ui.viewmodels.NewGameViewModel
import com.pandemiagame.org.ui.viewmodels.NewGameViewModelFactory
import kotlinx.coroutines.launch

const val MAX_PLAYERS = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameComp(navController: NavController) {
    val context = LocalContext.current

    val tokenManager = TokenManager(context)
    val viewModel: NewGameViewModel = viewModel(
        factory = NewGameViewModelFactory(tokenManager)
    )

    val buttonEnable :Boolean by viewModel.buttonEnable.observeAsState(initial=false)  // Estado para el botón activado

    val isLoading :Boolean by viewModel.isLoading.observeAsState(initial=false) // Estado para el cargando
    val coroutine = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val multiplayer: Boolean by viewModel.multiplayer.observeAsState(initial=false)

    val sharedPref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    val userJson = sharedPref.getString("user", null)
    var user: User? = null
    if(multiplayer) {
        if (userJson != null) {
            user = Gson().fromJson(userJson, User::class.java)

            viewModel.onNameChanged(user.id.toString(), 0, user.username)
        } else {
            Log.e(context.getString(R.string.gen_error), context.getString(R.string.error_usuario_almacenado))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getUsers(context)
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
                    .imePadding() // Añade padding cuando el teclado aparece
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
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                        else
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null
                            )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.login),
                        contentDescription = stringResource(R.string.login_icon),
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
                            repeat(viewModel.players.size) { index ->
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    if(!multiplayer)
                                        TextField(
                                            value = viewModel.players.getOrElse(index) { "" },
                                            onValueChange = { newName -> viewModel.onNameChanged(newName, index) },
                                            label = { Text("Jugador ${index + 1}") },
                                            modifier = Modifier
                                                .weight(1f)  // Ocupa el espacio disponible
                                                .padding(end = 8.dp)  // Espacio entre TextField y Button
                                        )
                                    else {
                                        var expanded by remember { mutableStateOf(false) }

                                        OutlinedButton(
                                        enabled = (index != 0),
                                        onClick = {
                                                if((user == null) || (index != 0)) {expanded = true}},
                                            colors = ButtonColors(
                                                contentColor = Color.Green,
                                                disabledContainerColor = Color.Gray,
                                                disabledContentColor = Color.White,
                                                containerColor = Color.White
                                            ),
                                            modifier = Modifier
                                                .weight(1f)  // Ocupa el espacio disponible
                                                .padding(end = 8.dp)  // Espacio entre TextField y Button
                                        ) {
                                            Text(if (viewModel.players[index].toString().isEmpty() == false) viewModel.playerNames[index] else " --- ")
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
                                                if(user.id.toString() !in viewModel.players){
                                                    DropdownMenuItem(
                                                        text = { Text("${user.id} - ${user.username}") },
                                                        onClick = {
                                                            viewModel.onNameChanged(user.id.toString(), index, user.username)
                                                            expanded = false
                                                        }
                                                    )
                                                }

                                            }
                                        }
                                    }
                                    IconButton(
                                        modifier = Modifier.size(48.dp),
                                        onClick = { viewModel.removePlayer(index) },
                                        enabled = (viewModel.players.size - 1) > 1
                                    ) {
                                        if(index != 0 && viewModel.players.size > 2)
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.eliminar_jugador),
                                                modifier = Modifier.size(24.dp)
                                            )
                                    }
                                }
                            }

                            Row{
                                // Contador de jugadores
                                Text(
                                    text = "${viewModel.players.size} Jugador${if (viewModel.players.size != 1) "es" else ""}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                // Botón para añadir jugador (disabled cuando se alcanza el máximo)
                                IconButton(
                                    onClick = { viewModel.addPlayer() },
                                    enabled = (viewModel.players.size < MAX_PLAYERS)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            }

                            NewGameButton(buttonEnable) {
                                coroutine.launch {
                                    viewModel.onButtonSelected(context)
                                }
                            }

                            // Observamos el estado de la creación y si es exitosa, navegamos
                            LaunchedEffect(gameCreationStatus) {
                                if (gameCreationStatus) {
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





