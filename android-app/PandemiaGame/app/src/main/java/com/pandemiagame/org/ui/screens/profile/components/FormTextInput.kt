package com.pandemiagame.org.ui.screens.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun FormTextInput (textContent: String, value: String, label: String, onValueChange: (newValue: String) -> Unit) {
    Column {
        Text(textContent)        // Campo de texto para el username
        TextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
            },
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp,
                    bottom = 16.dp
                )
        )
    }
}