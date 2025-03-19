package com.pandemiagame.org

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.pandemiagame.org.navigation.BottomNavBar
import com.pandemiagame.org.navigation.NavGraph
import com.pandemiagame.org.ui.theme.PandemiaGameTheme
import androidx.navigation.compose.rememberNavController
import com.pandemiagame.org.navigation.CustomTopAppBar
import com.pandemiagame.org.screen.GameActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PandemiaGameTheme {
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        CustomTopAppBar()

                    },
                    bottomBar = { BottomNavBar(navController) },
                    floatingActionButton = {
                        val context = LocalContext.current
                        val intent = Intent(context, GameActivity::class.java) // Create intent first
                        FloatingActionButton(onClick = {
                            context.startActivity(intent) // Start the activity on click
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Start Game")
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) { paddingValues ->
                    NavGraph(navController, Modifier.padding(paddingValues))
                }

            }
        }
    }
}
