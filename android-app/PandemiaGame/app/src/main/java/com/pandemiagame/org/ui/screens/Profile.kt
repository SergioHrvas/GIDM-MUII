package com.pandemiagame.org.ui.screens

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.pandemiagame.org.data.remote.Constants
import com.pandemiagame.org.ui.navigation.BottomNavBar
import com.pandemiagame.org.ui.navigation.CustomTopAppBar

import org.json.JSONObject


@Composable
fun WinRatePieChart(won: Int, total: Int) {
    val percentage = if (total > 0) (won.toFloat() / total.toFloat()) else 0f
    val colorWon = Color.Green
    val colorLost = Color.LightGray

    Canvas(modifier = Modifier.size(150.dp)) {
        // Fondo (parte perdida)
        drawArc(
            color = colorLost,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 20f, cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
        // Parte ganada
        drawArc(
            color = colorWon,
            startAngle = -90f,
            sweepAngle = 360f * percentage,
            useCenter = false,
            style = Stroke(width = 20f, cap = StrokeCap.Round),
            size = Size(size.width, size.height)
        )
    }

    // Texto del porcentaje
    Text(
        text = "${(percentage * 100).toInt()}%",
        modifier = Modifier.padding(top = 8.dp),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}
@Composable
fun Profile(navController: NavController) {
    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    var userString by remember { mutableStateOf(sharedPref.getString("user", null)) }
    val userJSON = userString?.let { JSONObject(it) } ?: JSONObject()

    var baseUrl = Constants.BASE_URL
    var username = userJSON.optString("username", "")
    var name = "${userJSON.optString("name", "")} ${userJSON.optString("last_name", "")}"
    var email = userJSON.optString("email", "")
    var image = userJSON.optString("image", "default.png")
    var winnedGames = userJSON.optInt("winned_games", 0)
    var playedGames = userJSON.optInt("played_games", 0)

    // Escuchar actualizaciones de navegación
    val updatedUser by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("updatedUserJson")
        ?.observeAsState() ?: remember { mutableStateOf(null) }

    println(updatedUser)

    LaunchedEffect(updatedUser) {
        updatedUser?.let {
            userString = it
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("updatedUserJson")
        }
    }


    Scaffold(
        topBar = { CustomTopAppBar() },
        bottomBar = { BottomNavBar(navController) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // Card de perfil
            androidx.compose.material3.Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(text = "@$username", fontSize = 14.sp, color = Color.Gray)
                            Text(text = "Principiante", fontSize = 14.sp)
                        }

                        Image(
                            painter = rememberImagePainter("${baseUrl}static/uploads/$image"),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                    }

                    Text(
                        text = email,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp),
                        color = Color.DarkGray
                    )

                    // Botón de editar
                    androidx.compose.material3.Button(
                        onClick = {
                            navController.navigate("edit-profile")
                        },
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .align(Alignment.End)
                    ) {
                        Text(text = "Editar Perfil")
                    }
                }
            }

            // Gráfico Win Rate
            if (playedGames > 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WinRatePieChart(won = winnedGames, total = playedGames)
                    Text(
                        text = "Ganadas $winnedGames/$playedGames",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
