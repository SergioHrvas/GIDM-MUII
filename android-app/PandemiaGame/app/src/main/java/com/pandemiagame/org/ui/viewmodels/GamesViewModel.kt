package com.pandemiagame.org.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.models.game.GameResponse
import com.pandemiagame.org.data.remote.RetrofitClient
import com.pandemiagame.org.data.remote.utils.TokenManager
import kotlinx.coroutines.launch


class GamesViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GamesViewModel(tokenManager) as T
    }
}

class GamesViewModel(private val tokenManager: TokenManager) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _gamesList = MutableLiveData<List<GameResponse>>()

    private val _gamesListDisplayed = MutableLiveData<List<GameResponse>>()
    val gamesListDisplayed: LiveData<List<GameResponse>> = _gamesListDisplayed

    private val _navegarADetalle = MutableLiveData<GameResponse?>(null)
    val navegarADetalle: LiveData<GameResponse?> = _navegarADetalle

    fun getMyGames(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                var token = "Bearer " + tokenManager.getToken()
                val response = RetrofitClient.instance.getMyGames(token)

                if (response.isNotEmpty()) {
                    _gamesList.value = response
                    _gamesListDisplayed.value = response
                } else {
                    Log.v(context.getString(R.string.gen_error), context.getString(R.string.no_juegos_encontrados, response))
                }


            } catch (e: Exception){
                Log.v(context.getString(R.string.gen_error), e.message ?: context.getString(R.string.error_peticion))
            } finally {
                _isLoading.value = false
            }

        }
    }

    fun seleccionarJuego(juego: GameResponse) {
        _navegarADetalle.value = juego  // Activa la navegación
    }

    fun navegacionCompletada() {
        _navegarADetalle.value = null
    }

    fun setGameDisplayed(mode: Int){
        if(mode == 0){
            _gamesListDisplayed.value = _gamesList.value
        }
        else if(mode == 1){
            _gamesListDisplayed.value = _gamesList.value?.filter { it.multiplayer == false }
        }
        else if(mode == 2){
            _gamesListDisplayed.value = _gamesList.value?.filter { it.multiplayer == true }
        }
    }


}