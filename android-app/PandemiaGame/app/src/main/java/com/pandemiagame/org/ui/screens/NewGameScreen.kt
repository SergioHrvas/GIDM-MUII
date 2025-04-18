package com.pandemiagame.org.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.pandemiagame.org.R
import com.pandemiagame.org.ui.theme.PandemiaGameTheme
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.viewmodels.NewGameViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

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




@Composable
fun NewGameComp(modifier: Modifier = Modifier,  viewModel: NewGameViewModel = viewModel(),  navController: NavController) {
    val context = LocalContext.current  // Obtén el contexto actual
    val buttonEnable :Boolean by viewModel.buttonEnable.observeAsState(initial=false)  // Estado para el boton activado
    val numPlayers :String by viewModel.numPlayers.observeAsState(initial="")  // Estado para el email

    val isLoading :Boolean by viewModel.isLoading.observeAsState(initial=false) // Estado para el cargando
    val coroutine = rememberCoroutineScope()
    val scrollState = rememberScrollState()

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
                            Text("Numero de jugadores")        // Campo de texto para el nombre
                            TextField(
                                value = numPlayers,  // Convierte a String para mostrarlo en el TextField
                                onValueChange = {
                                    viewModel.onValueChange(it)
                                },
                                label = { Text("Introduce el número de jugadores (2-5)") },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        top = 8.dp,
                                        bottom = 16.dp
                                    )// Modificador opcional para ajustar el ancho
                            )

                            newGameButton(buttonEnable) {
                                coroutine.launch {
                                    viewModel.onButtonSelected(
                                        context
                                    )
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
fun newGameButton(buttonEnable: Boolean, onButtonSelected: () -> Unit){
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

