package com.pandemiagame.org.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.models.game.GameRequest
import com.pandemiagame.org.data.remote.models.game.GameResponse
import com.pandemiagame.org.data.remote.RetrofitClient
import com.pandemiagame.org.data.remote.models.user.User
import com.pandemiagame.org.data.remote.utils.TokenManager
import com.pandemiagame.org.ui.screens.MAX_PLAYERS
import kotlinx.coroutines.launch

class NewGameViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return NewGameViewModel(tokenManager) as T
    }
}

class NewGameViewModel(private val tokenManager: TokenManager) : ViewModel(){
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

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

    private val _players = mutableStateListOf<String>().apply {
        addAll(listOf("", ""))
    }

    private val _playerNames = mutableStateListOf<String>().apply {
        addAll(listOf("", ""))
    }

    val players: List<String> = _players
    val playerNames: List<String> = _playerNames


    fun getUsers(context: Context) {

        viewModelScope.launch {
            try {
                var token = "Bearer " + tokenManager.getToken()
                val response = RetrofitClient.instance.getUsers(token)
                _users.value = response
            } catch (e: Exception) {
                // Manejar error
                Log.v(context.getString(R.string.gen_error), e.toString())
            }
        }
    }


    fun createGame(mp: Boolean, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val gameRequest = GameRequest(
                    status = "pending",
                    multiplayer = mp,
                    players = _players
                )

                var token = "Bearer " + tokenManager.getToken()
                val response = RetrofitClient.instance.createGame(token, gameRequest)

                if (response.id > 0) {
                    _game.value = response
                    _gameCreationStatus.value = true
                } else {
                    Log.e(context.getString(R.string.gen_error), response.toString())
                    _gameCreationStatus.value = false
                }
            } catch (e: Exception){
                Log.e(context.getString(R.string.gen_error), e.message ?: context.getString(R.string.error_peticion))
                _gameCreationStatus.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun notCreating(){
        _gameCreationStatus.value = false
    }

    fun onNameChanged(id: String, i: Int, name:String = ""){
        _players[i] = id
        if (multiplayer.value == true)
            _playerNames[i] = name
        _buttonEnable.value = (_players.size > 1) && (_players[0].isNotEmpty() == true) && (_players[1].isNotEmpty() == true)
    }

    fun onButtonSelected(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            createGame(multiplayer.value == true, context)
            _isLoading.value = false
        }
    }

    fun addPlayer() {
        if ( _players.size.toInt() < MAX_PLAYERS) {
            _players.add("")
            if(multiplayer.value == true){
                _playerNames.add("")
            }
        }
    }

    fun removePlayer(i: Int) {
        if(i>=0 && (i < _players.size.toInt())){
            _players.removeAt(i)
            if(multiplayer.value == true){
                _playerNames.removeAt(i)
            }
        }
    }

    fun changeMultiplayer (){
        _multiplayer.value = multiplayer.value != true
        _players.clear()
        _playerNames.clear()

        _players.add("")
        _players.add("")
        _playerNames.add("")
        _playerNames.add("")
    }
}