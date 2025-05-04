package com.pandemiagame.org.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pandemiagame.org.data.remote.GameRequest
import com.pandemiagame.org.data.remote.GameResponse
import com.pandemiagame.org.data.remote.RetrofitClient
import com.pandemiagame.org.data.remote.User
import com.pandemiagame.org.data.remote.utils.TokenManager
import com.pandemiagame.org.ui.screens.MAX_PLAYERS
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

    private val _numPlayers = MutableLiveData<String>()
    val numPlayers : LiveData<String> = _numPlayers

    private val _buttonEnable = MutableLiveData<Boolean>()
    val buttonEnable : LiveData<Boolean> = _buttonEnable

    private val _game = MutableLiveData<GameResponse>(null)
    val game: LiveData<GameResponse> = _game

    private val _gameCreationStatus = MutableLiveData<Boolean>()
    val gameCreationStatus: LiveData<Boolean> = _gameCreationStatus

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private var _multiplayer = MutableLiveData<Boolean>()
    val multiplayer : LiveData<Boolean> = _multiplayer

    private val _playerNames = mutableStateListOf<String>().apply {
        addAll(listOf("", ""))
    }

    val playerNames: List<String> = _playerNames


    private val tokenManager by lazy { TokenManager(context) } // Lazy initialization

    fun getUsers() {

        viewModelScope.launch {
            try {

                var token = "Bearer " + tokenManager.getToken()

                val response = RetrofitClient.instance.getUsers(token)

                _users.value = response

            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }


    fun createGame(mp: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val gameRequest = GameRequest(
                    status = "pending",
                    multiplayer = mp,
                    players = _playerNames
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

    fun notCreating(){
        _gameCreationStatus.value = false
    }

    fun onNameChanged(name: String, i: Int){
        _playerNames[i] = name
        _buttonEnable.value = (_playerNames.size > 1) && (_playerNames[0].isNotEmpty() == true) && (_playerNames[1].isNotEmpty() == true)
    }

    fun onButtonSelected() {
        viewModelScope.launch {
            _isLoading.value = true
            createGame(multiplayer.value == true)
            _isLoading.value = false
        }
    }

    fun addPlayer() {
        if ( _playerNames.size.toInt() < MAX_PLAYERS) {
            _playerNames.add("")
        }
    }

    fun removePlayer(i: Int) {
        if(i>=0 && (i < _playerNames.size.toInt())){
            _playerNames.removeAt(i)
        }
    }

    fun changeMultiplayer (){
        _multiplayer.value = multiplayer.value != true
    }
}