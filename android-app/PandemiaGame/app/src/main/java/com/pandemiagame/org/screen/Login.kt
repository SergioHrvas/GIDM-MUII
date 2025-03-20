package com.pandemiagame.org.screen

import android.annotation.SuppressLint
import android.graphics.Paint.Align
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.pandemiagame.org.R
import com.pandemiagame.org.ui.theme.PandemiaGameTheme
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pandemiagame.org.model.LoginViewModel
import com.pandemiagame.org.navigation.BottomNavBar
import com.pandemiagame.org.navigation.CustomTopAppBar
import kotlinx.coroutines.launch

@Preview
@Composable
fun Login () {
    val navController = rememberNavController() // Crear un NavController falso para el preview
    val viewModel: LoginViewModel = viewModel() // Obtener el ViewModel

    PandemiaGameTheme {
        LoginComp(navController, viewModel)
    }
}

@Composable
fun LoginComp(navController: NavController, viewModel: LoginViewModel) {
    val email :String by viewModel.email.observeAsState(initial="")  // Estado para el email
    val password :String by viewModel.password.observeAsState(initial="")  // Estado para el email  // Estado para la contraseña
    val loginEnable :Boolean by viewModel.loginEnable.observeAsState(initial=false)  // Estado para el email  // Estado para la contraseña

    val isLoading :Boolean by viewModel.isLoading.observeAsState(initial=false)  // Estado para el email  // Estado para la contraseña

    val coroutine = rememberCoroutineScope()

    Scaffold (
        topBar = { CustomTopAppBar() },
        ) { innerPading ->
        if (isLoading) {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPading),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.login), // Nombre sin extensión
                    contentDescription = "Icono vectorial",
                    contentScale = ContentScale.Fit // Mantiene proporciones sin recortar
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
                        Text("Correo electrónico")        // Campo de texto para el nombre
                        TextField(
                            value = email,
                            onValueChange = {
                                viewModel.onLoginChange(it, password)
                            }, // Actualiza el estado con el texto ingresado
                            label = { Text("Ingresa tu email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 8.dp,
                                    bottom = 16.dp
                                )// Modificador opcional para ajustar el ancho
                        )
                        Text("Contraseña")
                        TextField(
                            value = password,
                            visualTransformation = PasswordVisualTransformation(),
                            onValueChange = {
                                viewModel.onLoginChange(email, it)
                            }, // Actualiza el estado con el texto ingresado
                            label = { Text("Ingresa tu contraseña") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp), // Modificador opcional para ajustar el ancho
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                        LoginButton(loginEnable) { coroutine.launch { viewModel.onLoginSelected() } }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginButton(loginEnable: Boolean, onLoginSelected: () -> Unit){
    Button(
        onClick = {
            Log.v("a", "b");
            onLoginSelected()},
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(top = 10.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color.Gray,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContentColor = Color.White,
            contentColor = Color.White,
        ),
        enabled = loginEnable
    ){
            Text(text = "Iniciar Sesión")
        }
}

