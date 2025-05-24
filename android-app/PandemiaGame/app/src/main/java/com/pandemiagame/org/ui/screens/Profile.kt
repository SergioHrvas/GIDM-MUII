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
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.pandemiagame.org.data.remote.Constants
import com.pandemiagame.org.ui.navigation.BottomNavBar
import com.pandemiagame.org.ui.navigation.CustomTopAppBar
import firebase.com.protolitewrapper.BuildConfig
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
    var userJSON = if(userString != null) {
        JSONObject(userString!!)
    }
    else{
        JSONObject()
    }
    Scaffold(
        topBar = { CustomTopAppBar() },
        bottomBar = { BottomNavBar(navController) },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding) // Añade el padding del Scaffold
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, // Alinea verticalmente los elementos
                modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho disponible
            ) {
                Column(
                    modifier = Modifier.weight(1f) // Toma todo el espacio restante
                ) {
                    Text(
                        text = "${userJSON.get("username")}"
                    )
                    Text(
                        text = "${userJSON.get("name")} ${userJSON.get("last_name")}"
                    )
                }


                val base_url = Constants.BASE_URL

                Image(
                    painter = rememberImagePainter("${base_url}static/uploads/${userJSON.get("image")}"),
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp) // Tamaño fijo para la imagen
                        .clip(CircleShape) // Para hacerla circular
                )
            }

            Text(text = "${userJSON.get("email")}")
            println(userJSON)
            if(userJSON.get("winned_games") != null && userJSON.get("played_games") != null) {
                //WinRatePieChart(userJSON.get("winned_games").toString().toInt(), userJSON.get("played_games").toString().toInt())
                Text(text = "Ganadas ${userJSON.get("winned_games")}/${userJSON.get("played_games")}")
            }
        }
    }
}