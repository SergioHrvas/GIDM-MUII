package com.pandemiagame.org.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.utils.TokenManager
import com.pandemiagame.org.ui.navigation.BottomNavBar
import com.pandemiagame.org.ui.navigation.CustomTopAppBar


@Composable
fun MainScreen(navController: NavController, onLogout: () -> Unit) {
    Scaffold(
        topBar = { CustomTopAppBar() },
        bottomBar = { BottomNavBar(navController) },
    ) { innerPading ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPading),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo",
                modifier = Modifier.size(220.dp).padding(bottom = 10.dp))

            Button(onClick = { navController.navigate("create-game") },
                modifier = Modifier.padding(bottom = 10.dp)) {
                Text(text = "Nueva partida", fontSize = 26.sp)
            }

            Button(onClick = { navController.navigate("games") },
                modifier = Modifier.padding(bottom = 10.dp)) {
                Text(text = "Cargar partida", fontSize = 26.sp)
            }

            Button(onClick = onLogout,
                modifier = Modifier.padding(bottom = 10.dp)) {
                Text(text = "Cerrar sesión", fontSize = 26.sp)
            }
        }
    }
}