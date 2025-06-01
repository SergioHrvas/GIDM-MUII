package com.pandemiagame.org.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.viewmodels.LoginViewModel
import com.pandemiagame.org.ui.viewmodels.RegisterViewModel
import kotlinx.coroutines.launch
@Composable
fun RegisterComp(navController: NavController, viewModel: RegisterViewModel) {
    val name: String by viewModel.name.observeAsState(initial = "")
    val surname: String by viewModel.surname.observeAsState(initial = "")
    val username: String by viewModel.username.observeAsState(initial = "")
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val loginEnable: Boolean by viewModel.registerEnable.observeAsState(initial = false)
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)

    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(topBar = { CustomTopAppBar() }) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.login),
                    contentDescription = "Icono vectorial",
                    contentScale = ContentScale.Fit
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
                        Text("Nombre")
                        TextField(
                            value = name,
                            onValueChange = {
                                viewModel.onRegisterChange(it, surname, username, email, password)
                            },
                            label = { Text("Ingresa tu nombre") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        Text("Apellidos")
                        TextField(
                            value = surname,
                            onValueChange = {
                                viewModel.onRegisterChange(name, it, username, email, password)
                            },
                            label = { Text("Ingresa tus apellidos") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        Text("Nombre de usuario")
                        TextField(
                            value = username,
                            onValueChange = {
                                viewModel.onRegisterChange(name, surname, it, email, password)
                            },
                            label = { Text("Ingresa tu nombre de usuario") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        Text("Correo electrónico")
                        TextField(
                            value = email,
                            onValueChange = {
                                viewModel.onRegisterChange(name, surname, username, it, password)
                            },
                            label = { Text("Ingresa tu email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        Text("Contraseña")
                        TextField(
                            value = password,
                            onValueChange = {
                                viewModel.onRegisterChange(name, surname, username, email, it)
                            },
                            label = { Text("Ingresa tu contraseña") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        RegisterButton(loginEnable) {
                            coroutine.launch {
                                viewModel.onRegisterSelected(context)
                                navController.navigate("login")
                            }
                        }

                        Text(
                            text = "¿Ya estás registrado? Inicia sesión",
                            color = Color(0xFF3D8433),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .clickable {
                                    navController.navigate("login"){
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun RegisterButton(registerEnable: Boolean, onRegisterSelected: () -> Unit){
    Button(
        onClick = {
            onRegisterSelected()},
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(top = 10.dp, bottom = 10.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color.Gray,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContentColor = Color.White,
            contentColor = Color.White,
        ),
        enabled = registerEnable
    ){
            Text(text = "Registrarme")
        }
}

