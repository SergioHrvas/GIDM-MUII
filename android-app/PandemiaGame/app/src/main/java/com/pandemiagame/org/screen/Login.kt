package com.pandemiagame.org.screen

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

class Login: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PandemiaGameTheme {
                LoginComp()
            }
        }
    }
}

@Preview
@Composable
fun LoginComp() {
    var email by remember { mutableStateOf("") }  // Estado para el email
    var password by remember { mutableStateOf("") }  // Estado para la contraseña
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.login), // Nombre sin extensión
            contentDescription = "Icono vectorial",
            contentScale = ContentScale.Fit // Mantiene proporciones sin recortar
        )
        Text(
            text = "INICIAR SESIÓN",
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Box(
            modifier = Modifier.padding(14.dp).border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(20.dp)).padding(16.dp)
        ) {
            Column {
                Text("Correo electrónico")        // Campo de texto para el nombre
                TextField(
                    value = email,
                    onValueChange = { email = it }, // Actualiza el estado con el texto ingresado
                    label = { Text("Ingresa tu email") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)// Modificador opcional para ajustar el ancho
                )
                Text("Contraseña")
                TextField(
                    value = password,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { password = it }, // Actualiza el estado con el texto ingresado
                    label = { Text("Ingresa tu contraseña") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), // Modificador opcional para ajustar el ancho
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

            }
        }
    }
}

