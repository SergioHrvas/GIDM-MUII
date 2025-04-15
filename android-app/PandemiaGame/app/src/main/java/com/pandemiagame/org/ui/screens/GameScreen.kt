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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pandemiagame.org.data.remote.Organ
import com.pandemiagame.org.data.remote.models.Card
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.viewmodels.GameViewModel
import android.util.Log
import androidx.collection.intListOf
import androidx.compose.material3.AlertDialog
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Preview
@Composable
fun PreviewGame(){
    PandemiaGameTheme(darkTheme = false){
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)) {
            GameComp(navController = NavController(
                context = TODO()
            ))
        }    }
}


@Composable
fun GameComp(modifier: Modifier = Modifier, gameId: String = "", viewModel: GameViewModel = viewModel(), navController: NavController) {
    // Estado para controlar la visibilidad del diálogo de final de partida
    var showWinnerDialog by remember { mutableStateOf(false) }

    var isCardDrawn by remember { mutableStateOf(false) }

    var infecting: Int by remember { mutableIntStateOf(0) }

    var discarting: Int by remember { mutableIntStateOf(0) }

    val discards = remember {
        mutableStateListOf<Int>().apply {
            addAll(listOf(0, 0, 0))
        }
    }

    // Observa el LiveData y lo convierte en un State<GameResponse?>
    val gameResponse by viewModel.game.observeAsState()

   LaunchedEffect(Unit) {
        viewModel.getGame(gameId)
    }

    // Observamos el winner del gameResponse
    val winner = remember(gameResponse) {
        gameResponse?.winner
    }

    // Cuando detectamos un ganador, mostramos el diálogo
    LaunchedEffect(winner) {
        if (winner != null) {
            if (winner > 0) {
                showWinnerDialog = true
            }
        }
    }

    // Encuentra el índice del jugador actual basado en el turno
    val currentPlayerIndex = remember(gameResponse) {
        gameResponse?.players?.indexOfFirst { it.id == gameResponse?.turn } ?: 0
    }

    // Cambia esto a un estado mutable
    var otherPlayerIndex by remember { mutableIntStateOf(0) }

    // Actualiza otherPlayerIndex cuando cambia currentPlayerIndex o gameResponse
    LaunchedEffect(currentPlayerIndex, gameResponse) {
        gameResponse?.players?.let { players ->
            if (players.size > 1) {
                var nextIndex = (currentPlayerIndex + 1) % players.size
                if (nextIndex == currentPlayerIndex) nextIndex = (nextIndex + 1) % players.size
                otherPlayerIndex = nextIndex
            } else {
                otherPlayerIndex = 0
            }
        }
    }

    var selectedOrgan by remember { mutableStateOf<String?>(null) }
    var selectedCard by remember { mutableStateOf<Int>(-1) }

    // Manejar la infección cuando se selecciona un órgano
    LaunchedEffect(selectedOrgan) {
        selectedOrgan?.let { organType ->

            if (infecting != 0) {

                viewModel.doMove(selectedCard, otherPlayerIndex, organType)
                infecting = 0

                selectedOrgan = null
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            isCardDrawn = false
            showWinnerDialog = false
            infecting = 0
            discarting = 0
            // En lugar de clear(), resetea los valores
            discards[0] = 0
            discards[1] = 0
            discards[2] = 0
        }
    }

    BackHandler(enabled = true) {
        // Limpiar todos los estados inmediatamente
        isCardDrawn = false
        showWinnerDialog = false
        infecting = 0
        discarting = 0
        discards[0] = 0
        discards[1] = 0
        discards[2] = 0

        navController.popBackStack("home", inclusive = false)
    }



    Scaffold (
        topBar = { CustomTopAppBar() },
    ) { innerPadding ->
            gameResponse?.let { game ->
                if (showWinnerDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            // Opcional: maneja lo que pasa cuando se hace clic fuera del diálogo
                            showWinnerDialog = false
                        },
                        title = {
                            Text(text = "¡Juego Terminado!")
                        },
                        text = {
                            Text(text = "Ganador: $winner")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showWinnerDialog = false
                                }
                            ) {
                                Text("Aceptar")
                            }
                        }
                    )
                }
                Column(
                    modifier = modifier.fillMaxSize().padding(innerPadding),
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
                                    contentDescription = "Cambiar jugador",
                                    modifier = Modifier.clickable {
                                        otherPlayerIndex = (otherPlayerIndex + 1) % game.players.size
                                        if(otherPlayerIndex == currentPlayerIndex) {
                                            otherPlayerIndex = (otherPlayerIndex + 1) % game.players.size
                                        }
                                    } // Disparar animación al hacer clic
                                )
                            }
                            Text(
                                text = "Usuario #${game.players[otherPlayerIndex].id}",
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

                        Body(false, game.players[otherPlayerIndex].organs,         isInfecting = infecting != 0,
                            onOrganSelected = { organType ->
                                if (infecting != 0) {
                                    selectedOrgan = organType
                                }
                            })
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
                                .height(110.dp)
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
                                text = "Usuario #${game.players[currentPlayerIndex].id}",
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

                        Body(false, game.players[currentPlayerIndex].organs,         isInfecting = infecting != 0,
                            onOrganSelected = { organType ->
                                if (infecting != 0) {
                                    selectedOrgan = organType
                                }
                            })

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 15.dp),

                            horizontalArrangement = Arrangement.spacedBy(5.dp)

                        ) {
                            Image(
                                painter = painterResource(id = Card.fromDisplayName(game.players[currentPlayerIndex].playerCards[0].card.name)?.drawable
                                    ?: 0),
                                contentDescription = "CARTA 1",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.width(100.dp).border(width = if(discards[0] == 1) 3.dp else 0.dp, color = if(discards[0] == 1) Color.Gray else Color.Transparent).clickable{
                                    selectedCard = 0

                                    if (discarting == 1){
                                        discards[0] = (discards[0] + 1) % 2
                                    }
                                    else{
                                        if(game.players[currentPlayerIndex].playerCards[0].card.type=="organ" || game.players[currentPlayerIndex].playerCards[0].card.type=="cure") {
                                                viewModel.doMove(0, -1)
                                        }
                                        else if(game.players[currentPlayerIndex].playerCards[0].card.type=="virus"){
                                            infecting = 1;
                                        }
                                    }
                                }

                            )
                            Image(
                                painter = painterResource(id = Card.fromDisplayName(game.players[currentPlayerIndex].playerCards[1].card.name)?.drawable
                                    ?: 0),
                                contentDescription = "CARTA 2",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.width(100.dp).border(width = if(discards[1] == 1) 3.dp else 0.dp, color = if(discards[1] == 1) Color.Gray else Color.Transparent).clickable{
                                    selectedCard = 1
                                    if (discarting == 1){
                                        discards[1] = (discards[1] + 1) % 2
                                    }
                                    else {
                                        if (game.players[currentPlayerIndex].playerCards[1].card.type == "organ" || game.players[currentPlayerIndex].playerCards[1].card.type == "cure") {
                                            viewModel.doMove(1, -1)
                                        } else if (game.players[currentPlayerIndex].playerCards[1].card.type == "virus") {
                                            infecting = 1;
                                        }
                                    }
                                }
                            )
                            Image(
                                painter = painterResource(id = Card.fromDisplayName(game.players[currentPlayerIndex].playerCards[2].card.name)?.drawable
                                    ?: 0),
                                contentDescription = "CARTA 3",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.width(100.dp).border(width = if(discards[2] == 1) 3.dp else 0.dp, color = if(discards[2] == 1) Color.Gray else Color.Transparent).clickable{
                                    selectedCard = 2
                                    if (discarting == 1){
                                        discards[2] = (discards[2] + 1) % 2
                                    }
                                    else {
                                        if (game.players[currentPlayerIndex].playerCards[2].card.type == "organ" || game.players[currentPlayerIndex].playerCards[2].card.type == "cure") {
                                            viewModel.doMove(2, -1)
                                        } else if (game.players[currentPlayerIndex].playerCards[2].card.type == "virus") {
                                            selectedCard = 2
                                            infecting = 1;
                                        }
                                    }
                                }

                            )
                        }

                        Row(
                            modifier = Modifier.padding(top = 20.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if( (discarting == 0) && (infecting == 0)) Button(onClick = {
                                discarting = 1
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.discard),
                                    contentDescription = "Discard cards"
                                )

                            }


                        if (discarting == 1) Button(onClick = {
                            isCardDrawn = true

                            var idDiscards = mutableListOf<Int>()
                            for (i in 0..discards.size-1){
                                if(discards[i] == 1){
                                    idDiscards.add(game.players[currentPlayerIndex].playerCards[i].card.id)
                                    discards[i] = 0
                                    }
                            }

                            viewModel.discardCards(idDiscards)
                            discarting = 0

                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.confirm),
                                contentDescription = "Confirmar"
                            )

                        }

                        if ((discarting == 1) || (infecting == 1)) Button(onClick = {
                            if(discarting == 1){
                                for(i in 0..discards.size - 1){
                                    discards[i] = 0
                                }
                                discarting = 0
                            }
                            if(infecting == 1){
                                infecting = 0
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.cancel),
                                contentDescription = "Cancelar"
                            )

                        }
                    }

                    }

                }
            } ?: Text("Cargando...")
        }

}

@Composable
fun Body(myBody: Boolean, organs: List<Organ>, isInfecting: Boolean, onOrganSelected: (String) -> Unit = {}){
    // 0 = No lo tiene
    // 1 = Sí lo tiene
    // 2 = Lo tiene con cura
    // 3 = Lo tiene protegido
    // -1 = Lo tiene infectado

    // Calcula organPlace directamente basado en los organs recibidos
    val organPlace = remember(organs) {
        IntArray(4).apply {
            organs.forEach { organ ->
                when (organ.tipo) {
                    "brain" -> if (organ.cure == 1) this[0] = 2 else if (organ.cure == 2) this[0] = 3 else if(organ.virus == true) this[0] = -1 else this[0] = 1
                    "heart" ->  if (organ.cure == 1) this[1] = 2 else if (organ.cure == 2) this[1] = 3 else if(organ.virus == true) this[1] = -1 else this[1] = 1
                    "lungs" ->  if (organ.cure == 1) this[2] = 2 else if (organ.cure == 2) this[2] = 3 else if(organ.virus == true) this[2] = -1 else this[2] = 1
                    "intestine" ->  if (organ.cure == 1) this[3] = 2 else if (organ.cure == 2) this[3] = 3 else if(organ.virus == true) this[3] = -1 else this[3] = 1
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(5.dp),
        contentAlignment = Alignment.Center // Centra el contenido dentro del Box
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if(myBody) 10.dp else 30.dp)
        ) {
            Image(
                painter = painterResource(id = if(organPlace[0] == 1) R.drawable.brain else if(organPlace[0] == 2) R.drawable.brain_cure else if (organPlace[0] == 3) R.drawable.brain_protected else if(organPlace[0] == -1) R.drawable.brain_virus else R.drawable.brain_disable),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 60.dp else 45.dp).clickable{
                    onOrganSelected("brain")
                }
            )
            Image(
                painter = painterResource(id = if(organPlace[1] == 1) R.drawable.heart else if(organPlace[1] == 2) R.drawable.heart_cure else if (organPlace[1] == 3) R.drawable.heart_protected else if(organPlace[1] == -1) R.drawable.heart_virus else R.drawable.heart_disable),
                contentDescription = "Corazón",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 60.dp else 45.dp).clickable {
                    onOrganSelected("heart")
                }
            )
            Image(
                painter = painterResource(id = if(organPlace[2] == 1) R.drawable.lungs else if(organPlace[2] == 2) R.drawable.lungs_cure else if (organPlace[2] == 3) R.drawable.lungs_protected else if(organPlace[2] == -1) R.drawable.lungs_virus else R.drawable.lungs_disable),
                contentDescription = "Pulmones",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 60.dp else 45.dp).clickable {
                    onOrganSelected("lungs")
                }

            )
            Image(
                contentDescription = "Intestino",
                painter = painterResource(id = if(organPlace[3] == 1) R.drawable.intestine else if(organPlace[3] == 2) R.drawable.intestine_cure else if (organPlace[3] == 3) R.drawable.intestine_protected else if(organPlace[3] == -1) R.drawable.intestine_virus else R.drawable.intestine_disable),
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 60.dp else 45.dp).clickable {
                    onOrganSelected("intestine")
                }
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