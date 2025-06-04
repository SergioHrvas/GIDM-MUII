package com.pandemiagame.org.ui.viewmodels

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandemiagame.org.data.remote.models.auth.RegisterRequest
import com.pandemiagame.org.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _surname = MutableLiveData<String>()
    val surname: LiveData<String> = _surname

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _registerEnable = MutableLiveData<Boolean>()
    val registerEnable: LiveData<Boolean> = _registerEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    sealed class RegisterState {
        object Loading : RegisterState()
        data class Registered(val user: String) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }

    fun onRegisterChange(
        name: String,
        surname: String,
        username: String,
        email: String,
        password: String
    ) {
        _name.value = name
        _surname.value = surname
        _username.value = username
        _email.value = email
        _password.value = password

        _registerEnable.value = isValidName(name) &&
                isValidName(surname) &&
                isValidUsername(username) &&
                isValidEmail(email) &&
                isValidPassword(password)
    }

    fun isValidName(value: String): Boolean = value.length >= 2
    fun isValidUsername(value: String): Boolean = value.length >= 4
    fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    fun isValidPassword(password: String): Boolean = password.length > 5

    fun onRegisterSelected(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _registerState.value = RegisterState.Loading

            try {
                val request = RegisterRequest(
                    name = name.value ?: "",
                    last_name = surname.value ?: "",
                    username = username.value ?: "",
                    email = email.value ?: "",
                    password = password.value ?: ""
                )

                val response = RetrofitClient.instance.register(request)

            } catch (e: Exception) {
                Log.e("Register Error", "Error en la petición: ${e.message}")
                _registerState.value = RegisterState.Error("Error en la petición: ${e.message}")
            }

            _isLoading.value = false
        }
    }
}
