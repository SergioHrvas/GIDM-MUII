package com.pandemiagame.org.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pandemiagame.org.R
import com.pandemiagame.org.ui.theme.PandemiaGameTheme
import java.lang.reflect.Modifier

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
    Column() {
        Image(
            painter = painterResource(id = R.drawable.login), // Nombre sin extensión
            contentDescription = "Icono vectorial",
            contentScale = ContentScale.Fit // Mantiene proporciones sin recortar
        )

        Text(
            "a"
        )
    }
}

