package com.lumixa.app.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GoalCard(
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = "Meta inteligente",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Laptop para estudios",
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { 0.38f },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),

                color = Color(0xFF1FBF9F),

                trackColor = Color(0xFFE5E7EB)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "$72.200 ahorrado",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2A44)
                )

                Text(
                    text = "38%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1FBF9F)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Necesitas ahorrar $6.334 por día para alcanzar tu meta.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}