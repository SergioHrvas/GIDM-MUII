package com.pandemiagame.org.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pandemiagame.org.data.remote.GameResponse
import com.pandemiagame.org.data.remote.InfectData
import com.pandemiagame.org.data.remote.Move
import com.pandemiagame.org.data.remote.RetrofitClient
import com.pandemiagame.org.data.remote.utils.TokenManager
import kotlinx.coroutines.launch

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

    fun doMove(index_card: Int, playerSelected: Int = -1, organ: String = "") {
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

                var infect = if (playerSelected != -1) InfectData(
                    player1 = game.value?.players?.getOrNull(playerSelected)?.id,
                    organ1 = if (organ != "") organ.toString() else null
                ) else null


                // Creamos el movimiento
                var move = Move(
                    action = "card",
                    card = idCard,
                    infect = infect,
                )


                val response = RetrofitClient.instance.doMove(token, game.value?.id?.toInt() ?: 0,
                    game.value?.turn ?: 0, move)

                if(_game.value?.turn != response.turn){
                    _changingTurn.value = true
                }
                _game.value = response

            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }

    fun doMoveInfect(index_card: Int, infectData: InfectData) {
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
                    infect = infectData,
                )

                val response = RetrofitClient.instance.doMove(token, game.value?.id?.toInt() ?: 0,
                    game.value?.turn ?: 0, move)
                Log.v("BEFORE", _game.value.toString())

                if(_game.value?.turn != response.turn){
                    _changingTurn.value = true
                }
                _game.value = response
                Log.v("AFTER", _game.value.toString())

            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }


    fun doMoveExchange(index_card: Int, organToPass: String, infectData: InfectData) {
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

                val response = RetrofitClient.instance.doMove(token, game.value?.id?.toInt() ?: 0,
                    game.value?.turn ?: 0, move)
                Log.v("BEFORE", _game.value.toString())

                if(_game.value?.turn != response.turn){
                    _changingTurn.value = true
                }
                _game.value = response
                Log.v("AFTER", _game.value.toString())

            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }
    fun discardCards(cards: List<Int>) {
        viewModelScope.launch {
            try {
                var token = "Bearer " + tokenManager.getToken()

                // Creamos el movimiento
                var move = Move(
                    action = "discard",
                    discards = cards
                )

                val response = RetrofitClient.instance.doMove(token, game.value?.id?.toInt() ?: 0,
                    game.value?.turn ?: 0, move)

                _game.value = response

            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }

    fun setChangingTurn(ct: Boolean){
        _changingTurn.value = ct
    }

    fun setGame(game: GameResponse){
        _game.value = game
    }
}