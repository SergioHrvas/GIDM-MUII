package com.pandemiagame.org.ui.viewmodels

import com.pandemiagame.org.data.remote.models.user.UserUpdating

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
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.pandemiagame.org.R


class EditProfileViewModel : ViewModel(){
    // Estados privados mutables
    private val _id = MutableLiveData<Int>(0)
    private val _email = MutableLiveData<String>("")
    private val _password = MutableLiveData<String>("")
    private val _name = MutableLiveData<String>("")
    private val _lastname = MutableLiveData<String>("")
    private val _username = MutableLiveData<String>("")

    // Estados públicos de solo lectura
    val id: LiveData<Int> get() = _id
    val email: LiveData<String> get() = _email
    val password: LiveData<String> get() = _password
    val name: LiveData<String> get() = _name
    val lastname: LiveData<String> get() = _lastname
    val username: LiveData<String> get() = _username


    private val _formEnable = MutableLiveData<Boolean>()
    val formEnable : LiveData<Boolean> = _formEnable

    private val _updateCompleted = MutableLiveData(false)
    val updateCompleted: LiveData<Boolean> = _updateCompleted

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private var emailChanged: Boolean = false
    private var passwordChanged: Boolean = false

    // Función para inicializar con datos del usuario
    fun initializeUserData(userJSON: JSONObject) {
        _id.value = userJSON.optString("id", "0").toIntOrNull() ?: 0
        _email.value = userJSON.optString("email", "")
        _name.value = userJSON.optString("name", "")
        _lastname.value = userJSON.optString("last_name", "")
        _username.value = userJSON.optString("username", "")
    }

    // Función para cuando cambia el email
    fun onEmailChange(email: String){
        _email.value = email
        emailChanged = true
        _formEnable.value = isValidEmail(email) && (!passwordChanged || isValidPassword(password.value.toString()))
    }

    // Función para cuando cambia la contraseña
    fun onPasswordChange(password: String){
        _password.value = password
        passwordChanged = true
        _formEnable.value = isValidPassword(password) && (!emailChanged || isValidEmail(email.value.toString()))
    }

    // Función para cuando cambia el nombre
    fun onNameChange(name: String){
        _name.value = name
    }

    // Función para cuando cambia el apellido
    fun onLastNameChange(lastName: String){
        _lastname.value = lastName
    }

    // Función para cuando cambia el nombre de usuario
    fun onUserNameChange(username: String){
        _username.value = username
    }

    // Función para verificar que el email es válido
    fun isValidEmail(email: String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Función para verificar que la contraseña es válida
    fun isValidPassword(password: String): Boolean = password.length > 5

    // Función para cuando se pulsa el botón de actualizar
    fun onUpdateSelected(ctx: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Preparamos el token
                val tm = TokenManager(ctx)
                var token = "Bearer " + tm.getToken()
                val id = _id.value

                if (id == null) {
                    return@launch
                }

                // Construir objeto de actualización con solo los campos modificados
                val user = UserUpdating().apply {
                    _email.value.takeIf { it!!.isNotEmpty() }?.let { email = it }
                    _password.value.takeIf { it!!.isNotEmpty() }?.let { password = it }
                    _username.value.takeIf { it!!.isNotEmpty() }?.let { username = it }
                    _name.value.takeIf { it!!.isNotEmpty() }?.let { name = it }
                    _lastname.value.takeIf { it!!.isNotEmpty() }?.let { lastName = it }
                }

                // Llamamos a la función de Retrofit
                val response = RetrofitClient.instance.updateUser(token, id, user)

                // Guardamos el nuevo token y nuevo usuario
                val sharedPref = ctx.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                val updatedUserJsonString = Gson().toJson(response)

                if(response.token != null)
                    tm.saveToken(response.token.toString())

                with(sharedPref.edit()) {
                    putString("user", updatedUserJsonString)
                    apply()
                }

                _updateCompleted.value = true

            } catch (e: Exception) {
                Log.e(ctx.getString(R.string.gen_error), "${ctx.getString(R.string.error_modificar_perfil)}: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

}