package com.lumixa.app.presentation.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoleButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,

        modifier = modifier.height(46.dp),

        shape = RoundedCornerShape(12.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) {
                Color(0xFF2D6CDF)
            } else {
                Color.White
            },

            contentColor = if (selected) {
                Color.White
            } else {
                Color(0xFF0F2A44)
            }
        ),

        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (selected) 2.dp else 0.dp
        )
    ) {

        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}