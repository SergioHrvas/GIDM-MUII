package com.pandemiagame.org.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pandemiagame.org.data.remote.Organ
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.viewmodels.GameViewModel

@Preview
@Composable
fun PreviewGame(){
    PandemiaGameTheme(darkTheme = false){
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)) {
            GameComp()
        }    }
}

Enum


@Composable
fun GameComp(modifier: Modifier = Modifier, gameId: String = "", viewModel: GameViewModel = viewModel()) {
    var isCardDrawn by remember { mutableStateOf(false) }

    // Observa el LiveData y lo convierte en un State<GameResponse?>
    val gameResponse by viewModel.game.observeAsState()

    LaunchedEffect(gameId) {
        viewModel.getGame(gameId)
    }
    var indice = 0

    gameResponse?.let {
        for (i in 0 .. (it.players.size - 1)) {
            if (gameResponse!!.players[i].id == gameResponse!!.turn) {
                indice = i
                break
            }
        }
    }


    val otherPlayerIndice = (indice + 1) % (gameResponse?.players?.size ?: 1)

    Scaffold (
        topBar = { CustomTopAppBar() },
    ) { innerPading ->
            gameResponse?.let { game ->
                Column(
                    modifier = modifier.fillMaxSize().padding(innerPading),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(modifier = Modifier.padding(bottom = 2.dp)) {
                        Row(
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth(), // Ocupar todo el ancho disponible
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .weight(1F)
                            ) {
                                var expanded by remember { mutableStateOf(false) }
                                val options = listOf("Opción 1", "Opción 2", "Opción 3")

                                Button(onClick = { expanded = true }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.menu),
                                        contentDescription = "Discard cards"
                                    )

                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    options.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                expanded = false
                                                println("Seleccionaste: $option") // Aquí puedes manejar la selección
                                            }
                                        )
                                    }
                                }
                            }
                            game.players.size.let {
                                if (it > 2) Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_right_24),
                                    contentDescription = "Cambiar jugador"
                                )
                            }
                            Text(
                                text = "Usuario #${game.players[indice].id}",
                                modifier = Modifier.padding(end = 20.dp)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "Imagen de perfil",
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .size(40.dp)
                                    .border(width = 1.dp, color = Color.Black, shape = CircleShape)
                            )

                        }

                        game.let { response ->
                            response.players.getOrNull(1)?.organs?.let { organs ->
                                Body(false, organs)
                            }
                        }
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(1f)  // Asegura que el Box con la animación esté por encima de otros elementos
                    ) {
                        HorizontalDivider(thickness = 3.dp, modifier = Modifier.fillMaxWidth())

                        // Mazo de cartas (backdeck)
                        Image(
                            painter = painterResource(id = R.drawable.backdeck),
                            contentDescription = "Mazo de cartas",
                            modifier = Modifier
                                .height(150.dp)
                                .clickable {
                                    isCardDrawn = true
                                } // Disparar animación al hacer clic
                        )

                        // Carta animada al robar
                        if (isCardDrawn) {
                            DrawCardAnimation {
                                isCardDrawn = false
                            } // Resetea el estado cuando termina
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth(), // Ocupar todo el ancho disponible
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Usuario #${game.players[otherPlayerIndice].id}",
                                modifier = Modifier.padding(end = 20.dp)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "Imagen de perfil",
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .size(40.dp)
                                    .border(width = 1.dp, color = Color.Black, shape = CircleShape)
                            )

                        }
                        game.let { response ->
                            response.players.getOrNull(indice)?.organs?.let { organs ->
                                Body(true, organs)
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 15.dp),

                            horizontalArrangement = Arrangement.spacedBy(15.dp)

                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.a),
                                contentDescription = "Cerebro",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.width(114.dp)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.b),
                                contentDescription = "Cerebro",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.width(114.dp)

                            )
                            Image(
                                painter = painterResource(id = R.drawable.c),
                                contentDescription = "Cerebro",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.width(114.dp)

                            )
                        }

                        Row(
                            modifier = Modifier.padding(top = 20.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = { /*TODO*/ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.discard),
                                    contentDescription = "Discard cards"
                                )

                            }

                        }
                    }


                }
            } ?: Text("Cargando...")
        }

}

@Composable
fun Body(myBody: Boolean, organs: List<Organ>){
    var organPlace = intArrayOf(0, 0, 0, 0)

    organs.forEach {
        if(it.tipo == "brain") {
            organPlace[0] = 1
        }
        else if(it.tipo == "heart"){
            organPlace[1] = 1
        }
        else if(it.tipo == "lunge"){
            organPlace[2] = 1
        }
        else if(it.tipo == "intestine"){
            organPlace[3] = 1
        }
    }

    Box(
        modifier = Modifier
            .padding(14.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center // Centra el contenido dentro del Box
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if(myBody) 10.dp else 30.dp)
        ) {
            Image(
                painter = painterResource(id = if(organPlace[0] == 1) R.drawable.brain else R.drawable.brain_disable),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 68.dp else 50.dp)
            )
            Image(
                painter = painterResource(id = if(organPlace[1] == 1) R.drawable.heart else R.drawable.heart_disable),
                contentDescription = "Corazón",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 68.dp else 50.dp)
            )
            Image(
                painter = painterResource(id = if(organPlace[1] == 1) R.drawable.lungs else R.drawable.lunge_disable),
                contentDescription = "Pulmones",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 68.dp else 50.dp)

            )
            Image(
                painter = painterResource(id = if(organPlace[1] == 1) R.drawable.intestine else R.drawable.intestine_disable),
                contentDescription = "Intestino",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 68.dp else 50.dp)

            )
        }
    }
}



@Composable
fun DrawCardAnimation(onAnimationEnd: () -> Unit) {
    val startY = 0f  // Comienza desde el mazo
    val endY = 500f  // Baja hasta el jugador (ajusta según necesidad)

    val positionY = remember { Animatable(startY) }
    val rotation = remember { Animatable(0f) }  // Empieza sin rotación

    LaunchedEffect(Unit) {
        // Primero rotamos 90 grados
        rotation.animateTo(
            90f,
            animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
        )

        positionY.animateTo(
            endY,
            animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
        )
        onAnimationEnd() // Restablece el estado después de la animación
    }

    Box(
        modifier = Modifier
            .width(150.dp)
            .offset(y = positionY.value.dp)
            .rotate(rotation.value) // Aplicamos la rotación
            .zIndex(2f)  // Asegura que la carta esté por encima de otros elementos
    )
    {
        Image(        painter = painterResource(id = R.drawable.backrotated),
            contentDescription = "",)
    }
}