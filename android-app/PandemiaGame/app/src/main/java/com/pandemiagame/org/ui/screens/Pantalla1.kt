package com.pandemiagame.org.ui.screens

import android.graphics.drawable.PaintDrawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pandemiagame.org.R
import com.pandemiagame.org.data.remote.utils.TokenManager
import com.pandemiagame.org.ui.navigation.BottomNavBar
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import com.pandemiagame.org.ui.viewmodels.GameViewModel
import com.pandemiagame.org.ui.viewmodels.GamesViewModel
import kotlinx.coroutines.launch

@Composable
fun Pantalla1(navController: NavController,  viewModel: GameViewModel) {
    val tm = TokenManager(LocalContext.current);


    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current  // Obtén el contexto actual


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

            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo", modifier = Modifier.size(220.dp).padding(bottom = 10.dp))

            Button(onClick = {
                viewModel.createGame(
                    onSuccess = { navController.navigate("game") },
                    onError = { error -> Log.e("Game", error) }
                )
            }, modifier = Modifier.padding(bottom = 10.dp)) {
                Text(text = "Nueva partida", fontSize = 26.sp)
            }

            Button(onClick = {  },  modifier = Modifier.padding(bottom = 10.dp))  {
                Text(text = "Cargar partida", fontSize = 26.sp)
            }

            Button(onClick = { tm.clearToken() }, modifier = Modifier.padding(bottom = 10.dp))  {
                Text(text = "Configuración", fontSize = 26.sp)
            }

        }


    }
}
