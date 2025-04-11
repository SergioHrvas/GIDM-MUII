package com.pandemiagame.org.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pandemiagame.org.data.remote.GameRequest
import com.pandemiagame.org.data.remote.GameResponse
import com.pandemiagame.org.data.remote.RetrofitClient
import com.pandemiagame.org.data.remote.utils.TokenManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun getCurrentDateTimeLegacy(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC") // Asegura que sea UTC (Z)
    return sdf.format(java.util.Date()) // Fecha actual en formato ISO 8601
}

class NewGameViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewGameViewModel(context) as T
    }
}


class NewGameViewModel(private val context: Context) : ViewModel(){
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _numPlayers = MutableLiveData<Int>()
    val numPlayers : LiveData<Int> = _numPlayers

    private val _buttonEnable = MutableLiveData<Boolean>()
    val buttonEnable : LiveData<Boolean> = _buttonEnable

    private val _game = MutableLiveData<GameResponse>(null)
    val game: LiveData<GameResponse> = _game

    private val _gameCreationStatus = MutableLiveData<Boolean>()
    val gameCreationStatus: LiveData<Boolean> = _gameCreationStatus

    private val tokenManager by lazy { TokenManager(context) } // Lazy initialization


    fun createGame(numPlayers: Int, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val gameRequest = GameRequest(
                    status = "pending",
                    date = getCurrentDateTimeLegacy(),
                    players = numPlayers
                )


                var token = "Bearer " + tokenManager.getToken()
                val response = RetrofitClient.instance.createGame(token, gameRequest)

                if (response.id > 0) {
                    _game.value = response
                    // Si la creación fue exitosa, actualizamos el estado
                    _gameCreationStatus.value = true
                } else {
                    Log.e("Error", response.toString())
                    _gameCreationStatus.value = false
                }


            } catch (e: Exception){
                Log.e("Error", e.message ?: "Error desconocido")
                _gameCreationStatus.value = false


            } finally {
                _isLoading.value = false
            }

        }
    }

    fun isValidValue(numPlayers: Int): Boolean = (numPlayers > 1 && numPlayers < 6)


    fun onValueChange(numPlayers: Int){
        _numPlayers.value = numPlayers
        _buttonEnable.value = isValidValue(numPlayers)
    }

    fun onButtonSelected(ctx: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = numPlayers.value?.let { it -> createGame(it, ctx) }
            _isLoading.value = false
        }

    }
}