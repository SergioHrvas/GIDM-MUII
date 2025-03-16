package com.pandemiagame.org.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

class Game: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PandemiaGameTheme {
                GameComp()
            }
        }
    }
}

@Preview
@Composable
fun GameComp() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TheirBody()

        HorizontalDivider(thickness = 2.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ){

            MyBody()
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Pro")
            }
        }

    }
}

@Composable
fun MyBody(){
    Box(
        modifier = Modifier
            .padding(14.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(20.dp))
            .padding(8.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.brain),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(30.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.heart),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(30.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.lungs),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(30.dp)

            )
            Image(
                painter = painterResource(id = R.drawable.intestine),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(30.dp)

            )
        }
    }
}


@Composable
fun TheirBody(){
    Box(
        modifier = Modifier
            .padding(14.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center // Centra el contenido dentro del Box
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.brain),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(50.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.heart),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(50.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.lungs),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(50.dp)

            )
            Image(
                painter = painterResource(id = R.drawable.intestine),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(50.dp)

            )
        }
    }
}