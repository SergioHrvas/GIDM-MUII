package com.pandemiagame.org.ui.viewmodels

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.type.Date
import com.pandemiagame.org.data.remote.GameRequest
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

class GameViewModel(private val context: Context) : ViewModel(){
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val tokenManager by lazy { TokenManager(context) } // Lazy initialization


    fun createGame(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val gameRequest = GameRequest(
                    status = "pending",
                    date = getCurrentDateTimeLegacy(),
                    players = 2
                )


                var token = "Bearer " + tokenManager.getToken()

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