package com.pandemiagame.org.ui.screens.tutorial.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandemiagame.org.ui.screens.tutorial.Section


@Composable
fun TutorialSubSubsection(
    section: Section
) {
    Row(modifier = Modifier.padding(bottom = 10.dp)) {
        if(section.imageRes != null) {
            Image(
                painter = painterResource(id = section.imageRes),
                contentDescription = section.title,
                modifier = Modifier
                    .width(90.dp).padding(end = 5.dp)
            )
        }
        Column {
            Text(
                text = section.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = section.description,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}
