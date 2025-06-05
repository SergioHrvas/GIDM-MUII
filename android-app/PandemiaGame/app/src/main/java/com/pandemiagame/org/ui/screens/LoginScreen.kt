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
import com.pandemiagame.org.R
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.screens.profile.components.FormTextInput
import com.pandemiagame.org.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginComp(navController: NavController, viewModel: LoginViewModel) {
    val email :String by viewModel.email.observeAsState(initial="")  // Estado para el email
    val password :String by viewModel.password.observeAsState(initial="")  // Estado para la password
    val loginEnable :Boolean by viewModel.loginEnable.observeAsState(initial=false)  // Estado para el botón activado

    val isLoading :Boolean by viewModel.isLoading.observeAsState(initial=false) // Estado para el cargando

    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current

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
                    .padding(innerPadding),
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
                        FormTextInput(
                            textContent = stringResource(R.string.email),
                            value = email,
                            label = stringResource(R.string.ingresa_email),
                            onValueChange = {
                                viewModel.onLoginChange(it, password)
                            }
                        )

                        Text(stringResource(R.string.password))
                        TextField(
                            value = password,
                            visualTransformation = PasswordVisualTransformation(),
                            onValueChange = {
                                viewModel.onLoginChange(email, it)
                            },
                            label = { Text(stringResource(R.string.ingresa_password)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        LoginButton(loginEnable) { coroutine.launch { viewModel.onLoginSelected(context) } }

                        Text(color = Color(0xFF3D8433), fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = {
                            navController.navigate("register"){
                                popUpTo("register") { inclusive = true }
                            }
                        }), text = stringResource(R.string.login_no_cuenta))
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
            onLoginSelected()},
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
        enabled = loginEnable
    ){
            Text(stringResource(R.string.iniciar_sesion))
        }
}

