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
import com.lumixa.app.data.local.entity.GoalEntity

@Composable
fun GoalCard(
    goals: List<GoalEntity>,
    savingsToday: Double,
    onClick: () -> Unit = {}
) {
    val goal = goals.firstOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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

            if (goal == null) {
                Text(
                    text = "Aún no tienes metas creadas",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "+ Crear meta inteligente",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D6CDF)
                )
            } else {
                val smartSavedAmount =
                    savingsToday.coerceAtLeast(0.0)

                val progress =
                    if (goal.targetAmount > 0) {
                        (smartSavedAmount / goal.targetAmount).toFloat()
                    } else {
                        0f
                    }

                Text(
                    text = goal.name,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { progress },
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
                        text = "$${smartSavedAmount.toInt()} ahorrado hoy",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2A44)
                    )

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1FBF9F)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Meta: $${goal.targetAmount.toInt()} · Fecha: ${goal.targetDate}",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}