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
        Log.v("aaaaaaaaa", juegoId)

        viewModelScope.launch {
            try {

                var token = "Bearer " + tokenManager.getToken()

                val response = RetrofitClient.instance.getGame(token, juegoId.toInt())
                Log.v("res", response.toString())

                _game.value = response
            } catch (e: Exception) {
                // Manejar error
                Log.v("Error", e.toString())
            }
        }
    }
}