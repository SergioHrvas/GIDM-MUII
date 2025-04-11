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

    private val tokenManager by lazy { TokenManager(context) } // Lazy initialization

    fun getMyGames(onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            try {

                var token = "Bearer " + tokenManager.getToken()

                val response = RetrofitClient.instance.getMyGames(token)

                if (response.isNotEmpty()) {
                    _gamesList.value = response
                } else {
                    onError("No se encontraron juegos: $response")
                }


            } catch (e: Exception){
                onError(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }

        }
    }
}