package com.pandemiagame.org.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pandemiagame.org.data.remote.GameResponse
import com.pandemiagame.org.data.remote.RetrofitClient
import com.pandemiagame.org.data.remote.utils.TokenManager
import kotlinx.coroutines.launch


class GamesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GamesViewModel(context) as T
    }
}


class GamesViewModel(private val context: Context) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _gamesList = MutableLiveData<List<GameResponse>>()
    val gamesList: LiveData<List<GameResponse>> = _gamesList

    private val _gamesListDisplayed = MutableLiveData<List<GameResponse>>()
    val gamesListDisplayed: LiveData<List<GameResponse>> = _gamesListDisplayed

    private val _navegarADetalle = MutableLiveData<GameResponse?>(null)
    val navegarADetalle: LiveData<GameResponse?> = _navegarADetalle

    private val tokenManager by lazy { TokenManager(context) } // Lazy initialization

    // Estado para el juego seleccionado
    private val _selectedGame = MutableLiveData<GameResponse>()
    val selectedGame: LiveData<GameResponse> = _selectedGame

    init {
        getMyGames()
    }

    fun getMyGames() {
        viewModelScope.launch {
            _isLoading.value = true
            try {

                var token = "Bearer " + tokenManager.getToken()
                val response = RetrofitClient.instance.getMyGames(token)
                println(response)
                if (response.isNotEmpty()) {
                    _gamesList.value = response
                    _gamesListDisplayed.value = response
                } else {
                    Log.v("Error", "No se encontraron juegos: $response")
                }


            } catch (e: Exception){
                Log.v("Error", e.message ?: "Error desconocido")
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