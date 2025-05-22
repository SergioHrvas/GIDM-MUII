package com.pandemiagame.org.ui.viewmodels

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.pandemiagame.org.data.remote.RetrofitClient
import com.pandemiagame.org.data.remote.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Base64

class LoginViewModel : ViewModel(){
    private val _email = MutableLiveData<String>()
    val email : LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password : LiveData<String> = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable : LiveData<Boolean> = _loginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    sealed class AuthState {
        object Loading : AuthState()
        data class Authenticated(val user: String) : AuthState() // Cambia String por tu clase User si es necesario
        object Unauthenticated : AuthState()
        data class Error(val message: String) : AuthState()
    }

    init {
        _authState.value = AuthState.Loading
    }

    fun checkAuthState(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val tokenManager = TokenManager(context)
            val token = tokenManager.getToken()

            when {
                token == null -> {
                    _authState.value = AuthState.Unauthenticated
                }
                isTokenExpiredLocally(token) -> {
                    tokenManager.clearToken()
                    _authState.value = AuthState.Unauthenticated
                }
                else -> {
                    verifyTokenWithServer(token, context)
                }
            }
        }
    }

    private suspend fun verifyTokenWithServer(token: String, context: Context) {
        try {
            val response = RetrofitClient.instance.verifyToken("Bearer $token")

            if (response.valid) {
                val sharedPref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                val userJson = sharedPref.getString("user", null)
                _authState.value = AuthState.Authenticated(userJson ?: "")
            } else {
                TokenManager(context).clearToken()
                _authState.value = AuthState.Unauthenticated
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Error de conexión: ${e.message}")
        }
    }

    private fun isTokenExpiredLocally(token: String): Boolean {
        return try {

            val parts = token.split(".")
            if (parts.size != 3) return true

            val payload = String(
                android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE),
                Charsets.UTF_8
            )
            val json = Gson().fromJson(payload, Map::class.java)

            // Solución para el problema del tipo de 'exp'
            val exp = when (val expValue = json["exp"]) {
                is Double -> expValue.toLong()
                is String -> expValue.toLong()
                else -> null
            }

            exp?.let { expiration ->
                System.currentTimeMillis() / 1000 >= expiration
            } ?: true
        } catch (e: Exception) {
            true
        }
    }

    fun onLoginChange(email: String, password: String){
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPassword(password)
    }

    fun isValidEmail(email: String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean = password.length > 5

    fun onLoginSelected(ctx: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = email.value?.let { password.value?.let { it1 -> login(it, it1, ctx) } }
            if (result != null) {
                _authState.value = AuthState.Authenticated(
                    ctx.getSharedPreferences("MyPref", Context.MODE_PRIVATE).getString("user", "") ?: ""
                )
            }
            _isLoading.value = false
        }

    }

    suspend fun login(email: String, password: String, context: Context): String? {
        return withContext(Dispatchers.IO) {  // Ejecutar en hilo de fondo
            try {
                val response = RetrofitClient.instance.login(email, password, "password")

                val sharedPref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("user", Gson().toJson(response.user))
                    apply()
                }

                if (response.access_token.isNotEmpty()) {
                    val tm = TokenManager(context);

                    tm.saveToken(response.access_token);
                    return@withContext response.access_token
                } else {
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e("Login Error", "Error en la petición: ${e.message}")
                return@withContext null
            }
        }
    }

}