package com.pandemiagame.org.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pandemiagame.org.navigation.CustomTopAppBar

class GameActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PandemiaGameTheme {
                Scaffold(
                    topBar = {
                        CustomTopAppBar()
                    },
                    /*floatingActionButton = {
                        LargeExample {
                            println("FAB presionado")
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center*/
                ) { innerPadding ->
                    GameComp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewGame(){
    PandemiaGameTheme(darkTheme = false){
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)) {
            GameComp()
        }    }
}




@Composable
fun GameComp(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column (modifier = Modifier.padding(bottom = 2.dp)){
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(), // Ocupar todo el ancho disponible
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ){
                Box (modifier = Modifier.padding(start = 10.dp).weight(1F)) {
                    var expanded by remember { mutableStateOf(false) }
                    val options = listOf("Opción 1", "Opción 2", "Opción 3")

                    Button(onClick = { expanded = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu),
                            contentDescription = "Discard cards"
                        )

                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    expanded = false
                                    println("Seleccionaste: $option") // Aquí puedes manejar la selección
                                }
                            )
                        }
                    }
                }
                Text(
                    text="Usuario #1",
                    modifier = Modifier.padding(end = 20.dp)
                )
                Image(painter = painterResource(id = R.drawable.user),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .padding(end=10.dp).size(40.dp)
                        .border(width = 1.dp, color = Color.Black, shape = CircleShape)
                        )

            }

             Body(false)
        }
        Box(
            contentAlignment = Alignment.Center,

        ){
            HorizontalDivider(thickness = 2.dp)

            Image(
                painter = painterResource(id = R.drawable.backdeck),
                contentDescription = "Discard cards",
                modifier = Modifier.height(130.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(), // Ocupar todo el ancho disponible
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ){
                Text(
                    text="Usuario #2",
                    modifier = Modifier.padding(end = 20.dp)
                )
                Image(painter = painterResource(id = R.drawable.user),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .padding(end=10.dp).size(40.dp)
                        .border(width = 1.dp, color = Color.Black, shape = CircleShape)
                )

            }
            Body(true)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 15.dp),

                horizontalArrangement = Arrangement.spacedBy(15.dp)

            ){
                Image(
                    painter = painterResource(id = R.drawable.a),
                    contentDescription = "Cerebro",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(114.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.b),
                    contentDescription = "Cerebro",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(114.dp)

                )
                Image(
                    painter = painterResource(id = R.drawable.c),
                    contentDescription = "Cerebro",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(114.dp)

                )
            }
            
            Row(
                modifier = Modifier.padding(top=20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id = R.drawable.discard), contentDescription = "Discard cards")

                }

                }
            }


    }
}

@Composable
fun Body(myBody: Boolean){
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
            horizontalArrangement = Arrangement.spacedBy(if(myBody) 10.dp else 30.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.brain),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 68.dp else 50.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.heart),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 68.dp else 50.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.lungs),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 68.dp else 50.dp)

            )
            Image(
                painter = painterResource(id = R.drawable.intestine),
                contentDescription = "Cerebro",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(if(myBody) 68.dp else 50.dp)

            )
        }
    }
}