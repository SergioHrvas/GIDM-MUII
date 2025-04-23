package com.pandemiagame.org.ui.screens

import android.util.Log
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
import com.pandemiagame.org.data.remote.models.CardEnum
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.viewmodels.GameViewModel
import androidx.compose.material3.AlertDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.pandemiagame.org.data.remote.GameResponse
import com.pandemiagame.org.data.remote.Card
import com.pandemiagame.org.data.remote.CardWrapper
import com.pandemiagame.org.data.remote.InfectData
import com.pandemiagame.org.data.remote.Player

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

    var selecting: Int by remember { mutableStateOf(0) }

    var discarting: Int by remember { mutableIntStateOf(0) }

    val discards = remember {
        mutableStateListOf<Int>().apply {
            addAll(listOf(0, 0, 0))
        }
    }

    // Observa el LiveData y lo convierte en un State<GameResponse?>
    val gameResponse by viewModel.game.observeAsState()

    // Observa el LiveData y lo convierte en un State<GameResponse?>
    val changingTurn by viewModel.changingTurn.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.setChangingTurn(true)
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

            if (selecting != 0) {
                viewModel.doMove(selectedCard, otherPlayerIndex, organType)
                selecting = 0

                selectedOrgan = null
            }
        }
    }

    var changing_body: Boolean by remember { mutableStateOf(false) }
    var ready_to_change: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(ready_to_change) {
        if(ready_to_change) {
            viewModel.doMove(selectedCard, otherPlayerIndex)
            ready_to_change = false
        }
    }

    var infecting: Boolean by remember { mutableStateOf(false) }


    DisposableEffect(Unit) {
        onDispose {
            isCardDrawn = false
            showWinnerDialog = false
            selecting = 0
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
        selecting = 0
        discarting = 0
        discards[0] = 0
        discards[1] = 0
        discards[2] = 0

        viewModel.setGame(GameResponse(
            status = "pending",
            date = "",
            id = 0,
            turn = 0,
            numTurns = 0,
            turns = listOf<Int>(0),
            winner = 0,
            cards = listOf<Card>(Card(
                id = 0,
                name = "",
                type = ""
            )),
            players = listOf<Player>(Player(
                name = "",
                gameId = 0,
                id = 0,
                playerCards = listOf<CardWrapper>(CardWrapper(
                    card = Card(
                        id = 0,
                        name = "BackCard",
                        type = ""
                    )
                ),CardWrapper(
                    card = Card(
                        id = 0,
                        name = "BackCard",
                        type = ""
                    )
                ),CardWrapper(
                    card = Card(
                        id = 0,
                        name = "BackCard",
                        type = ""
                    )
                )),
                organs = listOf<Organ>()
            ),
            Player(
                name = "",
                gameId = 0,
                id = 0,
                playerCards = listOf<CardWrapper>(CardWrapper(
                    card = Card(
                        id = 0,
                        name = "BackCard",
                        type = ""
                    )
                ),
                    CardWrapper(
                        card = Card(
                            id = 0,
                            name = "BackCard",
                            type = ""
                        )
                    ),
                    CardWrapper(
                        card = Card(
                            id = 0,
                            name = "BackCard",
                            type = ""
                        )
                    )),
                organs = listOf<Organ>()
            ))
        ))

        navController.popBackStack("home", inclusive = false)
    }

    Scaffold (
        topBar = { CustomTopAppBar() },
    ) { innerPadding ->
            gameResponse?.let { game ->
                if (showWinnerDialog) {
                    AlertDialog(
                        onDismissRequest = {
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
                if ((changingTurn == true) && (winner != null)) {
                    Dialog(
                        onDismissRequest = {
                            viewModel.setChangingTurn(false)
                        },
                        properties = DialogProperties(
                            usePlatformDefaultWidth = false // Esto permite que el diálogo no use el ancho predeterminado
                        ),
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable{
                                    viewModel.setChangingTurn(false)

                                },

                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Turno del jugador ${game.players[currentPlayerIndex].name} ${game.players[currentPlayerIndex].id}",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )
                                Text("Pulse para jugar su turno", color=Color.White)
                            }
                        }
                    }
                }
                if(changing_body){

                    AlertDialog(
                        onDismissRequest = {
                            changing_body = false
                        },
                        title = {
                            Text(text = "Vas a cambiar el cuerpo con el jugador ${game.players[otherPlayerIndex].name}")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    changing_body = false
                                    ready_to_change = true
                                }
                            ) {
                                Text("Aceptar")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    changing_body = false
                                }
                            ) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
                if (infecting) {
                    // Mapa para guardar las selecciones: órgano -> jugador objetivo
                    val selections = remember {
                        mutableStateMapOf<String, Int?>().apply {
                            // Inicializar con null para cada órgano con virus
                            game.players[currentPlayerIndex].organs
                                .filter { (it.virus == 1) || (it.virus == 2)}
                                .forEach { put(it.tipo, null) }
                        }
                    }

                    Dialog(
                        onDismissRequest = { infecting = false }
                    ) {
                        Surface(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Seleccionar objetivos de infección",
                                    style = MaterialTheme.typography.titleLarge)

                                // Lista de órganos con virus y sus selectores
                                game.players[currentPlayerIndex].organs
                                    .filter { (it.virus == 1) || (it.virus == 2)}
                                    .forEach { organ ->
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            ) {
                                                Text(
                                                    text = "${organ.tipo}:",
                                                    modifier = Modifier.weight(1f)
                                                )

                                                // Dropdown para seleccionar jugador objetivo
                                                Box(modifier = Modifier.weight(2f)) {
                                                    var expanded by remember { mutableStateOf(false) }
                                                    val targetPlayers = game.players
                                                        .filter { player ->
                                                            player.id != game.players[currentPlayerIndex].id &&
                                                                    player.organs.any { targetOrgan ->
                                                                        targetOrgan.tipo == organ.tipo &&
                                                                        targetOrgan.cure != 3 // No está completamente curado
                                                                    }
                                                        }

                                                    OutlinedButton(
                                                        onClick = { expanded = true },
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(selections[organ.tipo]?.toString() ?: "Seleccionar jugador")
                                                        Icon(
                                                            Icons.Default.ArrowDropDown,
                                                            contentDescription = null
                                                        )
                                                    }

                                                    DropdownMenu(
                                                        expanded = expanded,
                                                        onDismissRequest = { expanded = false }
                                                    ) {
                                                        targetPlayers.forEach { player ->
                                                            DropdownMenuItem(
                                                                text = { Text(player.id.toString()) },
                                                                onClick = {
                                                                    selections[organ.tipo] = player.id
                                                                    expanded = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            HorizontalDivider()
                                        }
                                    }

                                // Botones de acción
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { infecting = false },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer
                                        )
                                    ) {
                                        Text("Cancelar")
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Button(
                                        onClick = {
                                            // Convertir las selecciones a lista
                                            val selectedPairs = selections.mapNotNull { (organ, player) ->
                                                player?.let { organ to it }
                                            }.take(5) // Limitar a máximo 5 pares

                                            val data = InfectData(
                                                player1 = selectedPairs.getOrNull(0)?.second,
                                                organ1 = selectedPairs.getOrNull(0)?.first,
                                                player2 = selectedPairs.getOrNull(1)?.second,
                                                organ2 = selectedPairs.getOrNull(1)?.first,
                                                player3 = selectedPairs.getOrNull(2)?.second,
                                                organ3 = selectedPairs.getOrNull(2)?.first,
                                                player4 = selectedPairs.getOrNull(3)?.second,
                                                organ4 = selectedPairs.getOrNull(3)?.first,
                                                player5 = selectedPairs.getOrNull(4)?.second,
                                                organ5 = selectedPairs.getOrNull(4)?.first
                                            )
                                            infecting = false
                                            viewModel.doMoveInfect(selectedCard, infectData = data)
                                        },
                                        enabled = selections.values.any { it != null } // Solo habilitar si hay al menos una selección
                                    ) {
                                        Text("Confirmar")
                                    }
                                }
                            }
                        }
                    }
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

                        Body(false, game.players[otherPlayerIndex].organs, onOrganSelected = { organType ->
                                if (selecting != 0) {
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
                            DrawCardAnimation () {

                                isCardDrawn = false

                                var idDiscards = mutableListOf<Int>()
                                for (i in 0..discards.size-1){
                                    if(discards[i] == 1){
                                        idDiscards.add(game.players[currentPlayerIndex].playerCards[i].card.id)
                                        discards[i] = 0
                                    }
                                }

                                viewModel.discardCards(idDiscards)

                                viewModel.setChangingTurn(true)

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

                        Body(false, game.players[currentPlayerIndex].organs, onOrganSelected = { organType ->
                                if (selecting != 0) {
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
                                painter = painterResource(id = CardEnum.fromDisplayName(game.players[currentPlayerIndex].playerCards[0].card.name)?.drawable
                                    ?: 0),
                                contentDescription = "CARTA 1",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.width(100.dp).border(width = if(discards[0] == 1) 3.dp else 0.dp, color = if(discards[0] == 1) Color.Gray else Color.Transparent).clickable{
                                    selectedCard = 0
                                    if (discarting == 1){
                                        discards[0] = (discards[0] + 1) % 2
                                    }
                                    else {
                                        when (game.players[currentPlayerIndex].playerCards[0].card.type) {
                                            "organ", "cure" -> {
                                                selecting = 0
                                                viewModel.doMove(0, game.players[currentPlayerIndex].id)
                                            }
                                            "virus" -> {
                                                selecting = 1
                                            }
                                            "action" -> {
                                                selecting = 0
                                                when (game.players[currentPlayerIndex].playerCards[0].card.name){
                                                    "Change Body" -> {
                                                        if(game.players.size > 2){
                                                            changing_body = true
                                                        }
                                                        else{
                                                            ready_to_change = true
                                                        }
                                                    }
                                                    "Steal Organ" -> {
                                                        selecting = 1
                                                    }
                                                    "Discard Cards" -> {
                                                        viewModel.doMove(0)
                                                    }
                                                    "Infect Player" -> {
                                                        Log.v("aa",
                                                            game.players[currentPlayerIndex].organs.size.toString()
                                                        )
                                                        infecting = true
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            )
                            Image(
                                painter = painterResource(id = CardEnum.fromDisplayName(game.players[currentPlayerIndex].playerCards[1].card.name)?.drawable
                                    ?: 0),
                                contentDescription = "CARTA 2",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.width(100.dp).border(width = if(discards[1] == 1) 3.dp else 0.dp, color = if(discards[1] == 1) Color.Gray else Color.Transparent).clickable{
                                    selectedCard = 1
                                    if (discarting == 1){
                                        discards[1] = (discards[1] + 1) % 2
                                    }
                                    else {
                                        when (game.players[currentPlayerIndex].playerCards[1].card.type) {
                                            "organ", "cure" -> viewModel.doMove(1)
                                            "virus" -> {
                                                selecting = 1
                                            }
                                            "action" -> {
                                                when (game.players[currentPlayerIndex].playerCards[1].card.name){
                                                    "Change Body" -> {
                                                        if(game.players.size > 2){
                                                            changing_body = true
                                                        }
                                                        else{
                                                            ready_to_change = true
                                                        }
                                                    }
                                                    "Steal Organ" -> {
                                                        selecting = 1
                                                    }
                                                    "Discard Cards" -> {
                                                        viewModel.doMove(1)
                                                    }
                                                    "Infect Player" -> {
                                                        infecting = true
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                            Image(
                                painter = painterResource(id = CardEnum.fromDisplayName(game.players[currentPlayerIndex].playerCards[2].card.name)?.drawable
                                    ?: 0),
                                contentDescription = "CARTA 3",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.width(100.dp).border(width = if(discards[2] == 1) 3.dp else 0.dp, color = if(discards[2] == 1) Color.Gray else Color.Transparent).clickable{
                                    selectedCard = 2
                                    if (discarting == 1){
                                        discards[2] = (discards[2] + 1) % 2
                                    }
                                    else {
                                        when (game.players[currentPlayerIndex].playerCards[2].card.type) {
                                            "organ", "cure" -> viewModel.doMove(2)
                                            "virus" -> {
                                                selecting = 1
                                            }
                                            "action" -> {
                                                when (game.players[currentPlayerIndex].playerCards[2].card.name){
                                                    "Change Body" -> {
                                                        if(game.players.size > 2){
                                                            changing_body = true
                                                        }
                                                        else{
                                                            ready_to_change = true
                                                        }
                                                    }
                                                    "Steal Organ" -> {
                                                        selecting = 1
                                                    }
                                                    "Discard Cards" -> {
                                                        viewModel.doMove(2)
                                                    }

                                                    "Infect Player" -> {
                                                        Log.v("aa",
                                                            game.players[currentPlayerIndex].organs.size.toString()
                                                        )
                                                        infecting = true
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        Row(
                            modifier = Modifier.padding(top = 20.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if( (discarting == 0) && (selecting == 0)) Button(onClick = {
                                discarting = 1
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.discard),
                                    contentDescription = "Discard cards"
                                )

                            }


                        if (discarting == 1) Button(onClick = {
                            isCardDrawn = true


                            discarting = 0

                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.confirm),
                                contentDescription = "Confirmar"
                            )

                        }

                        if ((discarting == 1) || (selecting == 1)) Button(onClick = {
                            if(discarting == 1){
                                for(i in 0..discards.size - 1){
                                    discards[i] = 0
                                }
                                discarting = 0
                            }
                            if(selecting == 1){
                                selecting = 0
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
fun Body(myBody: Boolean, organs: List<Organ>, onOrganSelected: (String) -> Unit = {}){
    // 0 = No lo tiene
    // 1 = Sí lo tiene
    // 2 = Lo tiene con cura
    // 3 = Lo tiene con cura magica
    // 4 = Lo tiene protegido
    // -1 = Lo tiene infectado
    // -2 = Lo tiene infectado magico


    // Calcula organPlace directamente basado en los organs recibidos
    val organPlace = remember(organs) {
        IntArray(4).apply {
            organs.forEach { organ ->
                when (organ.tipo) {
                    "brain" -> if (organ.cure == 1) this[0] = 2 else if (organ.cure == 2) this[0] = 3 else if (organ.cure == 3) this[0] = 4 else if(organ.virus == 1) this[0] = -1 else if(organ.virus == 2) this[0] = -2 else this[0] = 1
                    "heart" ->  if (organ.cure == 1) this[1] = 2 else if (organ.cure == 2) this[1] = 3 else if (organ.cure == 3) this[1] = 4 else if(organ.virus == 1) this[1] = -1 else if(organ.virus == 2) this[1] = -2 else this[1] = 1
                    "lungs" ->  if (organ.cure == 1) this[2] = 2 else if (organ.cure == 2) this[2] = 3 else if (organ.cure == 3) this[2] = 4 else if(organ.virus == 1) this[2] = -1 else if(organ.virus == 2) this[2] = -2 else this[2] = 1
                    "intestine" ->  if (organ.cure == 1) this[3] = 2 else if (organ.cure == 2) this[3] = 3 else if (organ.cure == 3) this[3] = 4 else if(organ.virus == 1) this[3] = -1 else if(organ.virus == 2) this[3] = -2 else this[3] = 1
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
                painter = painterResource(id = if(organPlace[0] == 1) R.drawable.brain else if(organPlace[0] == 2) R.drawable.brain_cure else if(organPlace[0] == 3) R.drawable.brain_cure else if (organPlace[0] == 4) R.drawable.brain_protected else if(organPlace[0] == -1) R.drawable.brain_virus else if(organPlace[0] == -2) R.drawable.brain_virus else R.drawable.brain_disable),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 60.dp else 45.dp).clickable{
                    onOrganSelected("brain")
                }
            )
            Image(
                painter = painterResource(id = if(organPlace[1] == 1) R.drawable.heart else if(organPlace[1] == 2) R.drawable.heart_cure else if(organPlace[1] == 3) R.drawable.heart_cure else if (organPlace[1] == 4) R.drawable.heart_protected else if(organPlace[1] == -1) R.drawable.heart_virus else if(organPlace[1] == -2) R.drawable.heart_virus else R.drawable.heart_disable),
                contentDescription = "Corazón",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 60.dp else 45.dp).clickable {
                    onOrganSelected("heart")
                }
            )
            Image(
                painter = painterResource(id = if(organPlace[2] == 1) R.drawable.lungs else if(organPlace[2] == 2) R.drawable.lungs_cure else if(organPlace[2] == 3) R.drawable.lungs_cure else if (organPlace[2] == 4) R.drawable.lungs_protected else if(organPlace[2] == -1) R.drawable.lungs_virus else if(organPlace[2] == -2) R.drawable.lungs_virus else R.drawable.lungs_disable),
                contentDescription = "Pulmones",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 60.dp else 45.dp).clickable {
                    onOrganSelected("lungs")
                }

            )
            Image(
                contentDescription = "Intestino",
                painter = painterResource(id = if(organPlace[3] == 1) R.drawable.intestine else if(organPlace[3] == 2) R.drawable.intestine_cure else if(organPlace[3] == 3) R.drawable.intestine_cure else if (organPlace[3] == 4) R.drawable.intestine_protected else if(organPlace[3] == -1) R.drawable.intestine_virus else if(organPlace[3] == -2) R.drawable.intestine_virus else R.drawable.intestine_disable),
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