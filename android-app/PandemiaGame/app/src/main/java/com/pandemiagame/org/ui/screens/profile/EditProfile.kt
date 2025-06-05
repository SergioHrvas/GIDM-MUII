package com.pandemiagame.org.ui.screens.profile

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.pandemiagame.org.data.remote.Constants
import com.pandemiagame.org.ui.screens.profile.components.FormTextInput
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.viewmodels.EditProfileViewModel
import org.json.JSONObject
import com.pandemiagame.org.R


@Composable
fun EditProfileComp(navController: NavController,viewModel: EditProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("MyPref", Context.MODE_PRIVATE) }
    var userString by remember { mutableStateOf(sharedPref.getString("user", null)) }

    var userJSON = userString?.let { JSONObject(it) } ?: JSONObject()
    val image = userJSON.optString("image", "")
    val baseUrl = Constants.BASE_URL

    // Observar los estados del ViewModel
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")
    val name by viewModel.name.observeAsState("")
    val lastName by viewModel.lastname.observeAsState("")
    val username by viewModel.username.observeAsState("")
    val isLoading by viewModel.isLoading.observeAsState(false)
    val updateCompleted by viewModel.updateCompleted.observeAsState(false)
    val formEnable :Boolean by viewModel.formEnable.observeAsState(true)  // Estado para el botón activado

    var navigateBackAfterUpdate by remember { mutableStateOf(false) }


    // Efecto para inicializar los datos una vez
    LaunchedEffect(userString) {
        userString?.let {
            userJSON = JSONObject(it)
            viewModel.initializeUserData(userJSON)
        }
    }

    // Efecto para retroceder cuando se pulse el botón de modificar
    LaunchedEffect(updateCompleted) {
        if (updateCompleted) {
            val updatedUserJsonString = sharedPref.getString("user", null)

            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("updatedUserJson", updatedUserJsonString)

            navController.popBackStack()
        }
    }

    Scaffold (
        topBar = { CustomTopAppBar() },
    ) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(top=10.dp)
                    .verticalScroll(rememberScrollState())
                ,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter("${baseUrl}static/uploads/$image"),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
                Box(
                    modifier = Modifier
                        .padding(14.dp)
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        FormTextInput(
                            textContent = stringResource(R.string.nombre_usuario),
                            onValueChange = { newValue ->
                                viewModel.onUserNameChange(newValue)
                            },
                            value = username,
                            label = stringResource(R.string.ingresa_nombre_usuario)
                        )
                        FormTextInput(
                            textContent = stringResource(R.string.nombre),
                            onValueChange = { newValue ->
                                viewModel.onNameChange(newValue)
                            },
                            value = name,
                            label = stringResource(R.string.ingresa_nombre)
                        )
                        FormTextInput(
                            textContent = stringResource(R.string.apellidos),
                            onValueChange = { newValue ->
                                viewModel.onLastNameChange(newValue)
                            },
                            value = lastName,
                            label = stringResource(R.string.ingresa_apellidos)
                        )
                        FormTextInput(
                            textContent = stringResource(R.string.email),
                            onValueChange = { newValue ->
                                viewModel.onEmailChange(newValue)
                            },
                            value = email,
                            label = stringResource(R.string.ingresa_email)
                        )
                        Text(stringResource(R.string.password))
                        TextField(
                            value = password,
                            visualTransformation = PasswordVisualTransformation(),
                            onValueChange = {
                                    newValue -> viewModel.onPasswordChange(newValue)
                            },
                            label = { Text(stringResource(R.string.ingresa_password)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                        Button(
                            enabled = formEnable,
                            onClick = {
                                navigateBackAfterUpdate = true
                                viewModel.onUpdateSelected(context)
                            }
                        ) {
                            Text(stringResource(R.string.modificar_perfil))
                        }
                    }
                }
            }
        }
    }
}