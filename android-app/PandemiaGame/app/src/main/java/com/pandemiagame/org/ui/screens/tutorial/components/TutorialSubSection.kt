package com.pandemiagame.org.ui.screens.tutorial.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandemiagame.org.ui.screens.tutorial.Section
import kotlin.collections.forEach


@Composable
fun TutorialSubsection(
    section: Section,
    subsubsection: List<Section>? = null
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            if(section.imageRes != null) {
                Image(
                    painter = painterResource(id = section.imageRes),
                    contentDescription = section.title,
                    modifier = Modifier
                        .height(40.dp).padding(end = 5.dp)
                )
            }
            Text(
                text = section.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = section.description,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        subsubsection?.forEach {
            TutorialSubSubsection(it)
        }
    }
}
