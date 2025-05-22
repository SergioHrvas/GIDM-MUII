package com.pandemiagame.org.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pandemiagame.org.data.remote.Card
import com.pandemiagame.org.data.remote.CardWrapper
import com.pandemiagame.org.data.remote.GameResponse
import com.pandemiagame.org.data.remote.InfectData
import com.pandemiagame.org.data.remote.Move
import com.pandemiagame.org.data.remote.MoveResponse
import com.pandemiagame.org.data.remote.Player
import com.pandemiagame.org.data.remote.RetrofitClient
import com.pandemiagame.org.data.remote.utils.TokenManager
import kotlinx.coroutines.launch

// Función para crear un juego vacío
fun createEmptyGame(): GameResponse {
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


class GameViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(context) as T
    }
}

class GameViewModel(private val context: Context) : ViewModel() {

    private val _game = MutableLiveData<GameResponse>()
    val game: LiveData<GameResponse> = _game

    private val _changingTurn = MutableLiveData<Boolean>()
    val changingTurn: LiveData<Boolean> = _changingTurn

    private val tokenManager by lazy { TokenManager(context) } // Lazy initialization

    private val _moves = MutableLiveData<List<MoveResponse>>()
    val moves: LiveData<List<MoveResponse>> = _moves

    override fun onCleared(){
        setGame(createEmptyGame()) // Usamos createEmptyGame() en lugar de resetGame()
    }


    fun getGame(juegoId: String) {

        viewModelScope.launch {
            try {

                var token = "Bearer " + tokenManager.getToken()

                val response = RetrofitClient.instance.getGame(token, juegoId.toInt())

                _game.value = response

                Log.v("game", _game.value.toString())
            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }


    private val _shouldHideOpponentCards = MutableLiveData(true)
    val shouldHideOpponentCards: LiveData<Boolean> = _shouldHideOpponentCards

    fun prepareTurnChange() {
        _shouldHideOpponentCards.value = true
    }

    fun completeTurnChange() {
        _shouldHideOpponentCards.value = false
    }

    // Modificar las funciones de movimiento para usar el nuevo flujo
    private suspend fun executeMove(move: Move, currentTurn: Int): GameResponse {
        val token = "Bearer ${tokenManager.getToken()}"
        val gameId = _game.value?.id?.toInt() ?: 0

        return RetrofitClient.instance.doMove(token, gameId, currentTurn, move).also { response ->
            if (_game.value?.turn != response.turn) {
                prepareTurnChange()
                _changingTurn.value = true
            }
            _game.value = response

        }
    }

    fun doMove(index_card: Int, playerSelected: Int = -1, organ: String = "", currentTurn: Int? = -1) {
        viewModelScope.launch {
            try {
                val indice = game.value?.players?.indexOfFirst { it.id == game.value?.turn } ?: 0
                val idCard = game.value?.players?.getOrNull(indice)?.playerCards?.getOrNull(index_card)?.card?.id

                val infect = if (playerSelected != -1) InfectData(
                    player1 = game.value?.players?.getOrNull(playerSelected)?.id,
                    organ1 = organ.takeIf { it.isNotEmpty() }
                ) else null

                executeMove(
                    Move(action = "card", card = idCard, infect = infect),
                    currentTurn ?: 0
                )
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error in doMove", e)
            }
        }
    }

    fun doMoveInfect(index_card: Int, infectData: InfectData, currentTurn: Int = -1) {
        viewModelScope.launch {
            try {
                // Encontramos el índice del jugador actual de forma segura
                val indice = game.value?.players?.indexOfFirst { it.id == game.value?.turn } ?: 0

                // Obtenemos el ID de la carta con seguridad
                val idCard = game.value
                    ?.players
                    ?.getOrNull(indice)
                    ?.playerCards
                    ?.getOrNull(index_card)
                    ?.card
                    ?.id

                // Creamos el movimiento
                var move = Move(
                    action = "card",
                    card = idCard,
                    infect = infectData,
                )

                executeMove(
                    move, currentTurn
                )


            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }


    fun doMoveExchange(index_card: Int, organToPass: String, infectData: InfectData, currentTurn: Int = -1) {
        viewModelScope.launch {
            try {
                var token = "Bearer " + tokenManager.getToken()

                // Encontramos el índice del jugador actual de forma segura
                val indice = game.value?.players?.indexOfFirst { it.id == game.value?.turn } ?: 0

                // Obtenemos el ID de la carta con seguridad
                val idCard = game.value
                    ?.players
                    ?.getOrNull(indice)
                    ?.playerCards
                    ?.getOrNull(index_card)
                    ?.card
                    ?.id

                // Creamos el movimiento
                var move = Move(
                    action = "card",
                    card = idCard,
                    organToPass = organToPass,
                    infect = infectData,
                )

                executeMove(move, currentTurn)

            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }
    fun discardCards(cards: List<Int>, currentTurn: Int = -1) {
        viewModelScope.launch {
            try {
                var token = "Bearer " + tokenManager.getToken()

                // Creamos el movimiento
                var move = Move(
                    action = "discard",
                    discards = cards
                )

                val response = RetrofitClient.instance.doMove(token, game.value?.id?.toInt() ?: 0,
                    currentTurn, move)

                _game.value = response

            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }

    fun surrender(currentTurn: Int = -1){
        viewModelScope.launch {
            try {
                // Creamos el movimiento
                var move = Move(
                    action = "surrender",
                )

                executeMove(move, currentTurn)
            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }

    fun setChangingTurn(ct: Boolean){
        if(ct == false){
            completeTurnChange()
        }
        _changingTurn.value = ct

    }

    fun setGame(game: GameResponse){
        _game.value = game
    }



    fun getMoves(juegoId: String) {
        viewModelScope.launch {
            try {

                var token = "Bearer " + tokenManager.getToken()

                val response = RetrofitClient.instance.getMoves(token, juegoId.toInt())

                _moves.value = response

            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }


}