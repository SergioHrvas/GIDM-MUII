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

class LoginViewModel : ViewModel(){
    private val _email = MutableLiveData<String>()
    val email : LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password : LiveData<String> = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable : LiveData<Boolean> = _loginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _token = MutableLiveData<String?>()
    val token : LiveData<String?> = _token

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
            _token.value = result;
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