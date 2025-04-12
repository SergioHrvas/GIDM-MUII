package com.pandemiagame.org.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pandemiagame.org.data.remote.GameResponse
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
    private val tokenManager by lazy { TokenManager(context) } // Lazy initialization

    fun getGame(juegoId: String) {

        viewModelScope.launch {
            try {

                var token = "Bearer " + tokenManager.getToken()

                val response = RetrofitClient.instance.getGame(token, juegoId.toInt())

                _game.value = response
            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }

    fun doMove(index_card: Int, ) {
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
                val move = Move(
                    action = "card",
                    card = idCard,
                )
                val response = RetrofitClient.instance.doMove(token, game.value?.id?.toInt() ?: 0,
                    game.value?.turn ?: 0, move)
                Log.v("res", response.toString())

                _game.value = response
            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }

}