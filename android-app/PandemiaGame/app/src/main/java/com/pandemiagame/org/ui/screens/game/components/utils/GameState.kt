package com.pandemiagame.org.ui.screens.game.components.utils

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.pandemiagame.org.data.remote.models.game.GameResponse
import com.pandemiagame.org.data.remote.models.user.User
import com.pandemiagame.org.ui.viewmodels.GameViewModel

@Composable
fun rememberGameState(viewModel: GameViewModel): GameState {
    // Estados observados del ViewModel
    val gameResponse by viewModel.game.observeAsState()

    val changingTurn by viewModel.changingTurn.observeAsState()
    val context = LocalContext.current


    val sharedPref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    val userJson = sharedPref.getString("user", null)
    var user: User? = null

    if(gameResponse?.multiplayer == true) {
        if (userJson != null) {
            user = Gson().fromJson(userJson, User::class.java)
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
            println(user?.id)
            print(gameResponse?.players.toString())
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
    val cardsSelected = mutableStateListOf(false, false, false)
}

