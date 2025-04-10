package com.pandemiagame.org.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pandemiagame.org.data.remote.GameRequest
import com.pandemiagame.org.data.remote.GameResponse
import com.pandemiagame.org.data.remote.RetrofitClient
import kotlinx.coroutines.launch

data class GameDto(
    val id: Int,
    val deck_id: Int,
    val status: String,
    val created_at: String,
)

class GamesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GamesViewModel(context) as T
    }
}


class GamesViewModel(private val context: Context) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    fun createGame(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val gameRequest = GameRequest(
                    status = "pending",
                    date = getCurrentDateTimeLegacy(),
                    players = 2
                )


                var token = "Bearer " //+ tokenManager.getToken()
                val response = RetrofitClient.instance.createGame(token, gameRequest)

                if (response.id > 0) {
                    onSuccess()
                } else {
                    onError("Error: ${response}")
                }


            } catch (e: Exception){
                onError(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }

        }
    }
}