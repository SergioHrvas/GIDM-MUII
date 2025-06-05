package com.pandemiagame.org.ui.screens.tutorial.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandemiagame.org.ui.screens.tutorial.Section


@Composable
fun TutorialSection(
    section: Section
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = section.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if(section.imageRes != null) {
            Image(
                painter = painterResource(id = section.imageRes),
                contentDescription = section.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        Text(
            text = section.description,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
