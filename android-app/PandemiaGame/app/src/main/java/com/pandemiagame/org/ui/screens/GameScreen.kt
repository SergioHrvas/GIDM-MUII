package com.pandemiagame.org.ui.screens

import android.app.AlertDialog
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.google.gson.Gson
import com.pandemiagame.org.data.remote.GameResponse
import com.pandemiagame.org.data.remote.Card
import com.pandemiagame.org.data.remote.CardWrapper
import com.pandemiagame.org.data.remote.InfectData
import com.pandemiagame.org.data.remote.MoveResponse
import com.pandemiagame.org.data.remote.Player
import com.pandemiagame.org.data.remote.User

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
    // Estados principales del juego
    val gameState = rememberGameState(viewModel)

    // Efectos y lógica principal
    GameEffects(gameState, viewModel, gameId, navController)

    // UI principal
    GameLayout(
        modifier = modifier,
        gameState = gameState,
        viewModel = viewModel,
    )
}
@Composable
fun rememberGameState(viewModel: GameViewModel): GameState {
    // Estados observados del ViewModel
    val gameResponse by viewModel.game.observeAsState()
    val changingTurn by viewModel.changingTurn.observeAsState()
    val context = LocalContext.current

    val moves by viewModel.moves.observeAsState()

    val sharedPref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    val userJson = sharedPref.getString("user", null)
    var user: User? = null

    if(gameResponse?.multiplayer == true) {
        if (userJson != null) {
            user = Gson().fromJson(userJson, User::class.java)

            Log.v("USER_RECUPERADO", user.toString())
        } else {
            Log.e("USER", "No se encontró usuario en SharedPreferences")
        }
    }

    // Calculamos estados derivados
    val winner = remember(gameResponse) { gameResponse?.winner }
    val currentPlayerIndex = remember(gameResponse) {
        if(gameResponse?.multiplayer == false)
            gameResponse?.players?.indexOfFirst { it.id == gameResponse?.turn } ?: 0
        else{
            gameResponse?.players?.indexOfFirst { it.user?.id == user?.id } ?: 0
        }
    }


    // Calculamos otherPlayerIndex directamente basado en currentPlayerIndex
    val otherPlayerIndex = remember(currentPlayerIndex, gameResponse) {
        gameResponse?.players?.let { players ->
            if (players.size > 1) {
                var nextIndex = (currentPlayerIndex + 1) % players.size
                if (nextIndex == currentPlayerIndex) (nextIndex + 1) % players.size else nextIndex
            } else 0
        } ?: 0
    }

    // Creamos el estado con los valores iniciales
    val state = remember(gameResponse, changingTurn) {
        GameState(
            showWinnerDialog = false,
            isCardDrawn = false,
            selecting = 0,
            discarting = false,
            discards = mutableListOf(0, 0, 0),
            exchanging = false,
            changingBody = false,
            readyToChange = false,
            infecting = false,
            selectedOrgan = null,
            selectedCard = -1,
            gameResponse = gameResponse,
            changingTurn = changingTurn,
            currentPlayerIndex = currentPlayerIndex,
            otherPlayerIndex = otherPlayerIndex,
            winner = winner,
            seeingMoves = false
        )
    }


    return state
}

class GameState(
    showWinnerDialog: Boolean,
    isCardDrawn: Boolean,
    selecting: Int,
    discarting: Boolean,
    discards: MutableList<Int>,
    exchanging: Boolean,
    changingBody: Boolean,
    readyToChange: Boolean,
    infecting: Boolean,
    selectedOrgan: String?,
    selectedCard: Int,
    gameResponse: GameResponse?,
    changingTurn: Boolean?,
    currentPlayerIndex: Int,
    otherPlayerIndex: Int,
    winner: Int?,
    seeingMoves: Boolean
) {
    var showWinnerDialog by mutableStateOf(showWinnerDialog)
    var isCardDrawn by mutableStateOf(isCardDrawn)
    var selecting by mutableIntStateOf(selecting)
    var discarting by mutableStateOf(discarting)
    val discards = mutableStateListOf<Int>().apply { addAll(discards) }
    var exchanging by mutableStateOf(exchanging)
    var changingBody by mutableStateOf(changingBody)
    var readyToChange by mutableStateOf(readyToChange)
    var infecting by mutableStateOf(infecting)
    var selectedOrgan by mutableStateOf(selectedOrgan)
    var selectedCard by mutableIntStateOf(selectedCard)
    val gameResponse by mutableStateOf(gameResponse)
    val changingTurn by mutableStateOf(changingTurn)
    val currentPlayerIndex by mutableIntStateOf(currentPlayerIndex)
    var otherPlayerIndex by mutableIntStateOf(otherPlayerIndex)
    val winner by mutableStateOf(winner)
    var seeingMoves by mutableStateOf(seeingMoves)
}
@Composable
fun GameEffects(
    gameState: GameState,
    viewModel: GameViewModel,
    gameId: String,
    navController: NavController
) {
    val currentPlayerIndex = gameState.currentPlayerIndex
    val gameResponse = gameState.gameResponse

    // Efecto para cargar el juego inicialmente
    LaunchedEffect(Unit) {
        viewModel.setChangingTurn(true)
        viewModel.getGame(gameId)
        viewModel.getMoves(gameId)
    }

    // Efecto para manejar el ganador
    LaunchedEffect(gameState.winner) {
        if (gameState.winner != null && gameState.winner!! > 0) {
            gameState.showWinnerDialog = true
        }
    }

    // Efecto para actualizar el índice del otro jugador
    LaunchedEffect(currentPlayerIndex, gameResponse) {
        gameResponse?.players?.let { players ->
            if (players.size > 1) {
                var nextIndex = (currentPlayerIndex + 1) % players.size
                if (nextIndex == currentPlayerIndex) nextIndex = (nextIndex + 1) % players.size
                gameState.otherPlayerIndex = nextIndex
            } else {
                gameState.otherPlayerIndex = 0
            }
        }
    }

    // Efecto para manejar la infección
    LaunchedEffect(gameState.selectedOrgan) {
        gameState.selectedOrgan?.let { organType ->
            if (gameState.selecting > 0) {
                viewModel.doMove(gameState.selectedCard, gameState.otherPlayerIndex, organType, currentTurn = gameResponse?.players?.getOrNull(gameState.currentPlayerIndex)?.id)
                gameState.selecting = 0
                gameState.selectedOrgan = null
            }
        }
    }

    // Efecto para manejar el cambio de cuerpo
    LaunchedEffect(gameState.readyToChange) {
        if (gameState.readyToChange) {
            viewModel.doMove(gameState.selectedCard, gameState.otherPlayerIndex, currentTurn = gameResponse?.players?.getOrNull(gameState.currentPlayerIndex)?.id)
            gameState.readyToChange = false
        }
    }

    // Efecto de limpieza al desmontar
    DisposableEffect(Unit) {
        onDispose {
            gameState.isCardDrawn = false
            gameState.showWinnerDialog = false
            gameState.selecting = 0
            gameState.discarting = false
            gameState.discards[0] = 0
            gameState.discards[1] = 0
            gameState.discards[2] = 0
        }
    }


    // Manejador de botón de retroceso
    BackHandler(enabled = true) {
        resetGameState(gameState)
        viewModel.setGame(createEmptyGame()) // Usamos createEmptyGame() en lugar de resetGame()
        navController.popBackStack("home", inclusive = false)
    }
}

// Función para crear un juego vacío
private fun createEmptyGame(): GameResponse {
    return GameResponse(
        status = "pending",
        date = "",
        id = 0,
        turn = 0,
        numTurns = 0,
        turns = listOf(),
        winner = 0,
        cards = listOf(),
        players = listOf(
            Player(
                name = "",
                gameId = 0,
                id = 0,
                playerCards = listOf(
                    CardWrapper(card = Card(id = 0, name = "BackCard", type = "")),
                    CardWrapper(card = Card(id = 0, name = "BackCard", type = "")),
                    CardWrapper(card = Card(id = 0, name = "BackCard", type = ""))
                ),
                organs = listOf()
            ),
            Player(
                name = "",
                gameId = 0,
                id = 0,
                playerCards = listOf(
                    CardWrapper(card = Card(id = 0, name = "BackCard", type = "")),
                    CardWrapper(card = Card(id = 0, name = "BackCard", type = "")),
                    CardWrapper(card = Card(id = 0, name = "BackCard", type = ""))
                ),
                organs = listOf()
            )
        ),
        multiplayer = false
    )
}

private fun resetGameState(gameState: GameState) {
    gameState.isCardDrawn = false
    gameState.showWinnerDialog = false
    gameState.selecting = 0
    gameState.discarting = false
    gameState.discards[0] = 0
    gameState.discards[1] = 0
    gameState.discards[2] = 0
}

@Composable
fun GameLayout(
    modifier: Modifier = Modifier,
    gameState: GameState,
    viewModel: GameViewModel,
) {
    Scaffold(
        topBar = { CustomTopAppBar() },
    ) { innerPadding ->
        gameState.gameResponse?.let { game ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Diálogos
                GameDialogs(gameState, game, viewModel)

                // Jugador oponente
                OpponentPlayerSection(
                    game = game,
                    gameState = gameState,
                    otherPlayerIndex = gameState.otherPlayerIndex,
                    onPlayerChange = {
                        gameState.otherPlayerIndex = (gameState.otherPlayerIndex + 1) % game.players.size
                        if(gameState.otherPlayerIndex == gameState.currentPlayerIndex) {
                            gameState.otherPlayerIndex = (gameState.otherPlayerIndex + 1) % game.players.size
                        }
                    },
                    onOrganSelected = { organType ->
                        if (gameState.selecting == 1) {
                            gameState.selectedOrgan = organType
                        }
                    },
                    viewModel = viewModel
                )

                // Separador con mazo de cartas
                DeckSection(
                    isCardDrawn = gameState.isCardDrawn,
                    onCardDrawn = {
                        gameState.isCardDrawn = true
                    },
                    onDrawAnimationComplete = {
                        gameState.isCardDrawn = false
                        val idDiscards = mutableListOf<Int>()
                        for (i in 0..gameState.discards.size - 1) {
                            if (gameState.discards[i] == 1) {
                                idDiscards.add(game.players[gameState.currentPlayerIndex].playerCards[i].card.id)
                                gameState.discards[i] = 0
                            }
                        }
                        if (gameState.winner == 0) {
                            viewModel.discardCards(idDiscards, currentTurn = game.players[gameState.currentPlayerIndex].id)
                            viewModel.setChangingTurn(true)
                        }
                    }
                )

                // Jugador actual
                CurrentPlayerSection(
                    game = game,
                    currentPlayerIndex = gameState.currentPlayerIndex,
                    discards = gameState.discards,
                    discarting = gameState.discarting,
                    selecting = gameState.selecting,
                    exchanging = gameState.exchanging,
                    onCardSelected = { cardIndex ->
                        handleCardSelection(
                            cardIndex = cardIndex,
                            gameState = gameState,
                            game = game,
                            viewModel = viewModel
                        )
                    },
                    onDiscardToggle = {
                        if((game.multiplayer == false) || game.players[gameState.currentPlayerIndex].id == game.turn) {
                            gameState.discarting = true
                        }
                    },
                    onConfirmDiscard = {
                        gameState.isCardDrawn = true
                        gameState.discarting = false
                    },
                    onCancelAction = {
                        if(gameState.discarting) {
                            for(i in 0..gameState.discards.size - 1) {
                                gameState.discards[i] = 0
                            }
                            gameState.discarting = false
                        }
                        if(gameState.selecting > 0) {
                            gameState.selecting = 0
                        }
                        if(gameState.exchanging) {
                            gameState.exchanging = false
                        }
                    },
                    onOrganSelected = { organType ->
                        if (gameState.selecting == 2 || gameState.exchanging) {
                            gameState.selectedOrgan = organType
                        }
                    },
                    viewModel = viewModel,
                    gameState = gameState
                )
            }
        } ?: Text("Cargando...")
    }
}

@Composable
fun GameDialogs(
    gameState: GameState,
    game: GameResponse,
    viewModel: GameViewModel
) {
    val winnerName = remember(gameState.winner) {
        game.players.firstOrNull { it.id == gameState.winner }?.name ?: ""
    }

    if(gameState.seeingMoves){
        MovesDialog(
            moves = viewModel.moves.value,
            onDismiss = {
                gameState.seeingMoves = false
            }
        )
    }

    // Diálogo de ganador
    if (gameState.showWinnerDialog) {
        WinnerDialog(
            winnerName = winnerName,
            onDismiss = { gameState.showWinnerDialog = false }
        )
    }

    // Diálogo de cambio de turno
    if ((gameState.changingTurn == true) && (gameState.winner != null)) {
        TurnChangeDialog(
            playerName = game.players[game.players.indexOfFirst{ it.id == game.turn}].name,
            onDismiss = { viewModel.setChangingTurn(false) }
        )
    }

    // Diálogo de cambio de cuerpo
    if (gameState.changingBody) {
        ChangeBodyDialog(
            otherPlayerName = game.players[gameState.otherPlayerIndex].name,
            onConfirm = {
                gameState.changingBody = false
                gameState.readyToChange = true
            },
            onCancel = { gameState.changingBody = false }
        )
    }

    // Diálogo de infección
    if (gameState.infecting) {
        InfectDialog(
            currentPlayer = game.players[gameState.currentPlayerIndex],
            otherPlayers = game.players.filter { it.id != game.players[gameState.currentPlayerIndex].id },
            onConfirm = { infectData ->
                gameState.infecting = false
                viewModel.doMoveInfect(gameState.selectedCard, infectData = infectData, currentTurn = game.players[gameState.currentPlayerIndex].id)
            },
            onCancel = { gameState.infecting = false }
        )
    }

    // Diálogo de intercambio
    if (gameState.exchanging && (gameState.selectedOrgan != null)) {
        ExchangeDialog(
            game = game,
            currentPlayerIndex = gameState.currentPlayerIndex,
            otherPlayerIndex = gameState.otherPlayerIndex,
            selectedOrgan = gameState.selectedOrgan!!,
            onConfirm = { organToExchange ->
                val data = InfectData(
                    player1 = game.players[gameState.otherPlayerIndex].id,
                    organ1 = organToExchange,
                )
                viewModel.doMoveExchange(
                    gameState.selectedCard,
                    gameState.selectedOrgan!!,
                    infectData = data,
                    currentTurn = game.players[gameState.currentPlayerIndex].id
                )
                gameState.exchanging = false
                gameState.selectedOrgan = null
            },
            onCancel = {
                gameState.exchanging = false
                gameState.selectedOrgan = null
            }
        )
    }
}

@Composable
fun OpponentPlayerSection(
    game: GameResponse,
    gameState: GameState,
    otherPlayerIndex: Int,
    onPlayerChange: () -> Unit,
    onOrganSelected: (String) -> Unit,
    viewModel: GameViewModel
) {
    Column(modifier = Modifier.padding(bottom = 2.dp)) {
        PlayerHeader(
            gameState = gameState,
            viewModel = viewModel,
            player = game.players[otherPlayerIndex],
            showChangeButton = game.players.size > 2,
            onPlayerChange = onPlayerChange,
            current = false,
            multiplayer = (game.multiplayer && game.players[otherPlayerIndex].id == game.turn)
        )

        Body(
            myBody = false,
            organs = game.players[otherPlayerIndex].organs,
            onOrganSelected = onOrganSelected
        )
    }
}

@Composable
fun CurrentPlayerSection(
    game: GameResponse,
    gameState: GameState,
    viewModel: GameViewModel,
    currentPlayerIndex: Int,
    discards: List<Int>,
    discarting: Boolean,
    selecting: Int,
    exchanging: Boolean,
    onCardSelected: (Int) -> Unit,
    onDiscardToggle: () -> Unit,
    onConfirmDiscard: () -> Unit,
    onCancelAction: () -> Unit,
    onOrganSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerHeader(
            player = game.players[currentPlayerIndex],
            viewModel = viewModel,
            gameState = gameState,
            showChangeButton = false,
            onPlayerChange = {},
            current = true,
            multiplayer = (game.multiplayer && game.players[currentPlayerIndex].id == game.turn)
        )
        Body(
            myBody = true,
            organs = game.players[currentPlayerIndex].organs,
            onOrganSelected = { organType ->
                if (selecting == 2 || exchanging) {
                    onOrganSelected(organType)
                }
            }
        )

        PlayerCardsRow(
            cards = game.players[currentPlayerIndex].playerCards,
            discards = discards,
            onCardSelected = onCardSelected
        )

        PlayerActions(
            discarting = discarting,
            selecting = selecting,
            exchanging = exchanging,
            onDiscardToggle = onDiscardToggle,
            onConfirmDiscard = onConfirmDiscard,
            onCancelAction = onCancelAction
        )
    }
}

@Composable
fun PlayerHeader(
    gameState: GameState,
    viewModel: GameViewModel,
    player: Player,
    showChangeButton: Boolean,
    onPlayerChange: () -> Unit,
    current: Boolean,
    multiplayer: Boolean
) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (!current) Box(modifier = Modifier
            .padding(start = 10.dp)
            .weight(1F)) {
            MenuButton(gameState = gameState, viewModel = viewModel)
        }

        if (showChangeButton) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_right_24),
                contentDescription = "Cambiar jugador",
                modifier = Modifier.clickable(onClick = onPlayerChange)
            )
        }
        if(multiplayer)
            Icon(
                painter = painterResource((R.drawable.baseline_star_24)),
                contentDescription = "TURNO",
                tint = Color(0xFFFFA500)
            )
        Text(
            text = player.name,
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
}

@Composable
fun DeckSection(
    isCardDrawn: Boolean,
    onCardDrawn: () -> Unit,
    onDrawAnimationComplete: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(1f)
    ) {
        HorizontalDivider(thickness = 3.dp, modifier = Modifier.fillMaxWidth())

        Image(
            painter = painterResource(id = R.drawable.backdeck),
            contentDescription = "Mazo de cartas",
            modifier = Modifier
                .height(110.dp)
        )

        if (isCardDrawn) {
            DrawCardAnimation(onAnimationEnd = onDrawAnimationComplete)
        }
    }
}

@Composable
fun PlayerCardsRow(
    cards: List<CardWrapper>,
    discards: List<Int>,
    onCardSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        cards.forEachIndexed { index, cardWrapper ->
            PlayerCard(
                card = cardWrapper.card,
                isSelected = discards[index] == 1,
                onClick = { onCardSelected(index) }
            )
        }
    }
}

@Composable
fun PlayerCard(
    card: Card,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = CardEnum.fromDisplayName(card.name)?.drawable ?: 0),
        contentDescription = "Carta del jugador",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .width(100.dp)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) Color.Gray else Color.Transparent
            )
            .clickable(onClick = onClick)
    )
}


@Composable
fun PlayerActions(
    discarting: Boolean,
    selecting: Int,
    exchanging: Boolean,
    onDiscardToggle: () -> Unit,
    onConfirmDiscard: () -> Unit,
    onCancelAction: () -> Unit
) {
    Row(
        modifier = Modifier.padding(top = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {

        if (discarting) {
            ActionButton(
                iconResId = R.drawable.confirm,
                contentDescription = "Confirmar",
                onClick = onConfirmDiscard
            )
        }
        if (!discarting && (selecting == 0) && !exchanging) {
            ActionButton(
                iconResId = R.drawable.discard,
                contentDescription = "Descartar cartas",
                onClick = onDiscardToggle
            )
        }
        else {
            ActionButton(
                iconResId = R.drawable.cancel,
                contentDescription = "Cancelar",
                onClick = onCancelAction
            )
        }
    }
}

@Composable
fun ActionButton(
    iconResId: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription
        )
    }
}

private fun handleCardSelection(
    cardIndex: Int,
    gameState: GameState,
    game: GameResponse,
    viewModel: GameViewModel
) {
    if((game.multiplayer == false) || game.players[gameState.currentPlayerIndex].id == game.turn){
    gameState.selectedCard = cardIndex

    if (gameState.discarting) {
        gameState.discards[cardIndex] = (gameState.discards[cardIndex] + 1) % 2
    } else {
        when (game.players[gameState.currentPlayerIndex].playerCards[cardIndex].card.type) {
            "organ" -> {
                gameState.selecting = 0
                if(game.winner == 0) {
                    viewModel.doMove(cardIndex, currentTurn = game.players[gameState.currentPlayerIndex].id)
                }
            }
            "virus" -> {
                gameState.selecting = 1
            }
            "cure" -> {
                gameState.selecting = 2
            }
            "action" -> {
                handleActionCard(cardIndex, gameState, game, viewModel)
            }
        }
    }
        }
}

private fun handleActionCard(
    cardIndex: Int,
    gameState: GameState,
    game: GameResponse,
    viewModel: GameViewModel
) {
    when (game.players[gameState.currentPlayerIndex].playerCards[cardIndex].card.name) {
        "Change Body" -> {
            if (game.players.size > 2) {
                gameState.changingBody = true
            } else {
                gameState.readyToChange = true
            }
        }
        "Steal Organ" -> {
            gameState.selecting = 1
        }
        "Discard Cards" -> {
            viewModel.doMove(cardIndex, currentTurn = game.players[gameState.currentPlayerIndex].id
            )
        }
        "Infect Player" -> {
            gameState.infecting = true
        }
        "Exchange Card" -> {
            gameState.exchanging = true
        }
    }
}

private fun puedoCambiarlo (myOrganSelected: String?, theirOrgan: Organ, myOrganList: List<Organ>): Boolean{
    return if (theirOrgan.tipo == myOrganSelected){
        true
    } else {
        !myOrganList.contains(theirOrgan)
    }
}

private fun mirarTipo(targetOrgan: Organ, organ: Organ): Boolean {
    if (targetOrgan.cure == 3){
        return false
    }
    else if(targetOrgan.cure == 2){
        return true
    }
    else if (organ.virus == 2){
        return true
    }
    else if (targetOrgan.tipo == "magic"){
        return true
    }
    else if((targetOrgan.cure == 0) || (targetOrgan.cure == 1)) {
        if(organ.tipo == "magic"){
            return if((organ.magic_organ == 1) && (targetOrgan.tipo == "heart")){
                true
            } else if((organ.magic_organ == 2) && (targetOrgan.tipo == "brain")){
                true
            } else if((organ.magic_organ == 3) && (targetOrgan.tipo == "intestine")){
                true
            } else if((organ.magic_organ == 4) && (targetOrgan.tipo == "lungs")){
                true
            } else{
                false
            }
        }
        else{
            return (organ.tipo == targetOrgan.tipo)
        }
    }
    else{
        return false
    }
}

@Composable
fun MovesDialog(moves: List<MoveResponse>?, onDismiss: () -> Unit){
    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Registro de movimientos",
                    style = MaterialTheme.typography.titleLarge
                )
                Column {
                    moves?.forEach {
                        if(it.action == "discard"){
                            Text(text =
                                "El usuario " + it.player + " ha descartado las cartas"
                            )                        }
                        else{
                            Text(text =
                                "El usuario " + it.player + " ha jugado la carta " + it.card?.name
                            )
                        }
                    }
                }
            }
        }
        }
}

@Composable
fun WinnerDialog(
    winnerName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "¡Juego Terminado!")
        },
        text = {
            Text(text = "Ganador: $winnerName")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}

@Composable
fun TurnChangeDialog(
    playerName: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onDismiss),
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
                    text = "Turno del jugador $playerName",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Text("Pulse para jugar su turno", color = Color.White)
            }
        }
    }
}

@Composable
fun ChangeBodyDialog(
    otherPlayerName: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(text = "Vas a cambiar el cuerpo con el jugador $otherPlayerName")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun InfectDialog(
    currentPlayer: Player,
    otherPlayers: List<Player>,
    onConfirm: (InfectData) -> Unit,
    onCancel: () -> Unit
) {
    // Mapa para guardar las selecciones: órgano -> jugador objetivo
    val selections = remember {
        mutableStateMapOf<String, Int?>().apply {
            currentPlayer.organs
                .filter { it.virus == 1 || it.virus == 2 }
                .forEach { put(it.tipo, null) }
        }
    }

    val organFrom = mutableListOf<String>()

    Dialog(onDismissRequest = onCancel) {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Seleccionar objetivos de infección",
                    style = MaterialTheme.typography.titleLarge
                )

                // Lista de órganos con virus y sus selectores
                currentPlayer.organs
                    .filter { it.virus == 1 || it.virus == 2 }
                    .forEach { organ ->
                        organFrom.add(organ.tipo)
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
                                    var organ2 by remember { mutableStateOf("") }
                                    val targetPlayerAndOrgans = otherPlayers
                                        .flatMap { player ->
                                            player.organs
                                                .filter { targetOrgan ->
                                                    mirarTipo(targetOrgan, organ)
                                                }
                                                .map { targetOrgan -> player to targetOrgan }
                                        }

                                    OutlinedButton(
                                        onClick = { expanded = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(if (selections[organ2]?.toString()?.isEmpty() == false) "$organ2 ${selections[organ2]?.toString() }" else "Seleccionar jugador")
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        targetPlayerAndOrgans.forEach { (player, targetOrgan) ->
                                            DropdownMenuItem(
                                                text = { Text("${player.id} ${targetOrgan.tipo}") },
                                                onClick = {
                                                    organ2 = targetOrgan.tipo
                                                    selections[targetOrgan.tipo] = player.id
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
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            val selectedPairs = selections.mapNotNull { (organ, player) ->
                                player?.let { organ to it }
                            }.take(5)

                            val data = InfectData(
                                player1 = selectedPairs.getOrNull(0)?.second,
                                organ1 = selectedPairs.getOrNull(0)?.first,
                                organ1from = organFrom.getOrNull(0),
                                player2 = selectedPairs.getOrNull(1)?.second,
                                organ2 = selectedPairs.getOrNull(1)?.first,
                                organ2from = organFrom.getOrNull(1),
                                player3 = selectedPairs.getOrNull(2)?.second,
                                organ3 = selectedPairs.getOrNull(2)?.first,
                                organ3from = organFrom.getOrNull(2),
                                player4 = selectedPairs.getOrNull(3)?.second,
                                organ4 = selectedPairs.getOrNull(3)?.first,
                                organ4from = organFrom.getOrNull(3),
                                player5 = selectedPairs.getOrNull(4)?.second,
                                organ5 = selectedPairs.getOrNull(4)?.first,
                                organ5from = organFrom.getOrNull(4),
                            )

                            Log.v("data", data.toString())
                            onConfirm(data)
                        },
                        enabled = selections.values.any { it != null }
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}


@Composable
fun ExchangeDialog(
    game: GameResponse,
    currentPlayerIndex: Int,
    otherPlayerIndex: Int,
    selectedOrgan: String,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(text = "Cambiando $selectedOrgan por...")

                val indice = game.players[otherPlayerIndex].organs.indexOfFirst { it.tipo == selectedOrgan }
                val targetOrgans = if (indice != -1) {
                    listOf(game.players[otherPlayerIndex].organs[indice])
                } else {
                    game.players[otherPlayerIndex].organs.filter {
                        (it.cure != 3) && puedoCambiarlo(
                            selectedOrgan,
                            it,
                            game.players[currentPlayerIndex].organs
                        )
                    }
                }

                var otherOrganSelected by remember { mutableStateOf<String?>(null) }
                var expanded by remember { mutableStateOf(false) }

                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(otherOrganSelected ?: "Seleccionar órgano")
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    targetOrgans.forEach { organ ->
                        DropdownMenuItem(
                            text = { Text(organ.tipo) },
                            onClick = {
                                otherOrganSelected = organ.tipo
                                expanded = false
                            }
                        )
                        HorizontalDivider()
                    }
                }

                // Botones de acción
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            otherOrganSelected?.let { onConfirm(it) }
                        },
                        enabled = otherOrganSelected != null
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

@Composable
fun MenuButton(gameState: GameState,
               viewModel: GameViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Registro", "Chat", "Rendirme")
    Box {
        Button(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = "Menú"
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
                        when (option) {
                            "Registro" -> {
                                viewModel.getMoves(viewModel.game.value?.id.toString())
                                gameState.seeingMoves = true
                            }
                            "Chat" -> { /* Acción para opción 2 */ }
                            "Rendirme" -> { /* Acción para opción 3 */ }
                        }
                    }
                )
            }
        }
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
        IntArray(5).apply {
            organs.forEach { organ ->
                when (organ.tipo) {
                    "brain" -> if (organ.cure == 1) this[0] = 2 else if (organ.cure == 2) this[0] = 3 else if (organ.cure == 3) this[0] = 4 else if(organ.virus == 1) this[0] = -1 else if(organ.virus == 2) this[0] = -2 else this[0] = 1
                    "heart" ->  if (organ.cure == 1) this[1] = 2 else if (organ.cure == 2) this[1] = 3 else if (organ.cure == 3) this[1] = 4 else if(organ.virus == 1) this[1] = -1 else if(organ.virus == 2) this[1] = -2 else this[1] = 1
                    "lungs" ->  if (organ.cure == 1) this[2] = 2 else if (organ.cure == 2) this[2] = 3 else if (organ.cure == 3) this[2] = 4 else if(organ.virus == 1) this[2] = -1 else if(organ.virus == 2) this[2] = -2 else this[2] = 1
                    "intestine" ->  if (organ.cure == 1) this[3] = 2 else if (organ.cure == 2) this[3] = 3 else if (organ.cure == 3) this[3] = 4 else if(organ.virus == 1) this[3] = -1 else if(organ.virus == 2) this[3] = -2 else this[3] = 1
                    "magic" ->  {
                        if (organ.cure == 1){
                            if(organ.magic_organ == 1){
                                this[4] = 2
                            }
                            else if(organ.magic_organ == 2){
                                this[4] = 3
                            }
                            else if(organ.magic_organ == 3){
                                this[4] = 4
                            }
                            else if(organ.magic_organ == 4){
                                this[4] = 5
                            }
                        } else if (organ.cure == 2) {
                            this[4] = 6
                        } else if (organ.cure == 3) {
                            this[4] = 7
                        } else if(organ.virus == 1) {
                            if(organ.magic_organ == 1){
                                this[4] = -1
                            }
                            else if(organ.magic_organ == 2){
                                this[4] = -2
                            }
                            else if(organ.magic_organ == 3){
                                this[4] = -3
                            }
                            else if(organ.magic_organ == 4){
                                this[4] = -4
                            }
                        } else if(organ.virus == 2) {
                            this[4] = -5
                        } else {
                            this[4] = 1
                        }
                    }
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
            horizontalArrangement = Arrangement.spacedBy(if(myBody) 10.dp else 20.dp)
        ) {
            Image(
                painter = painterResource(id = if(organPlace[0] == 1) R.drawable.brain else if(organPlace[0] == 2) R.drawable.brain_cure else if(organPlace[0] == 3) R.drawable.brain_cure_magic else if (organPlace[0] == 4) R.drawable.brain_protected else if(organPlace[0] == -1) R.drawable.brain_virus else if(organPlace[0] == -2) R.drawable.brain_virus_magic else R.drawable.brain_disable),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(if (myBody) 60.dp else 45.dp)
                    .clickable {
                        if (organPlace[0] != 0) {
                            onOrganSelected("brain")
                        }
                    }
            )
            Image(
                painter = painterResource(id = if(organPlace[1] == 1) R.drawable.heart else if(organPlace[1] == 2) R.drawable.heart_cure else if(organPlace[1] == 3) R.drawable.heart_cure_magic else if (organPlace[1] == 4) R.drawable.heart_protected else if(organPlace[1] == -1) R.drawable.heart_virus else if(organPlace[1] == -2) R.drawable.heart_virus_magic else R.drawable.heart_disable),
                contentDescription = "Corazón",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(if (myBody) 60.dp else 45.dp)
                    .clickable {
                        if (organPlace[1] != 0) {
                            onOrganSelected("heart")
                        }
                    }
            )
            Image(
                painter = painterResource(id = if(organPlace[2] == 1) R.drawable.lungs else if(organPlace[2] == 2) R.drawable.lungs_cure else if(organPlace[2] == 3) R.drawable.lungs_cure_magic else if (organPlace[2] == 4) R.drawable.lungs_protected else if(organPlace[2] == -1) R.drawable.lungs_virus else if(organPlace[2] == -2) R.drawable.lungs_virus_magic else R.drawable.lungs_disable),
                contentDescription = "Pulmones",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(if (myBody) 60.dp else 45.dp)
                    .clickable {
                        if (organPlace[2] != 0) {
                            onOrganSelected("lungs")
                        }
                    }

            )
            Image(
                contentDescription = "Intestino",
                painter = painterResource(id = if(organPlace[3] == 1) R.drawable.intestine else if(organPlace[3] == 2) R.drawable.intestine_cure else if(organPlace[3] == 3) R.drawable.intestine_cure_magic else if (organPlace[3] == 4) R.drawable.intestine_protected else if(organPlace[3] == -1) R.drawable.intestine_virus else if(organPlace[3] == -2) R.drawable.intestine_virus_magic else R.drawable.intestine_disable),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(if (myBody) 60.dp else 45.dp)
                    .clickable {
                        if (organPlace[3] != 0) {
                            onOrganSelected("intestine")
                        }
                    }
            )
            Image(
                contentDescription = "Magic",
                painter = painterResource(id = if(organPlace[4] == 1) R.drawable.magic_organ else if(organPlace[4] == 2) R.drawable.magic_organ_cure_heart else if(organPlace[4] == 3) R.drawable.magic_organ_cure_brain else if (organPlace[4] == 4) R.drawable.magic_organ_cure_intestine else if(organPlace[4] == 5) R.drawable.magic_organ_cure_lungs else if(organPlace[4] == 6) R.drawable.magic_cure else if(organPlace[4] == 7) R.drawable.magic_protected else if(organPlace[4] == -1) R.drawable.magic_organ_virus_heart else if(organPlace[4] == -2) R.drawable.magic_organ_virus_brain else if (organPlace[4] == -3) R.drawable.magic_organ_virus_intestine else if(organPlace[4] == -4) R.drawable.magic_organ_virus_lungs else if(organPlace[4] == -5) R.drawable.magic_virus else R.drawable.magic_disable),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(if (myBody) 60.dp else 45.dp)
                    .clickable {
                        if (organPlace[4] != 0) {
                            onOrganSelected("magic")
                        }
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