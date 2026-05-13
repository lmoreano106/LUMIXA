package com.lumixa.app.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.R

@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.lumixalogo),
            contentDescription = "Logo LUMIXA",
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Tu control financiero diario",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D6CDF)
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "LUMIXA te ayuda a registrar tus gastos en segundos, entender en qué se va tu dinero y tomar mejores decisiones financieras cada día.",
            fontSize = 13.sp,
            color = Color(0xFF2B2B2B),
            lineHeight = 20.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        InfoCard(
            icon = R.drawable.ic_balance,
            title = "Controla tu saldo real",
            description = "Tus ingresos y gastos se actualizan al instante."
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoCard(
            icon = R.drawable.ic_habits,
            title = "Visualiza tus hábitos",
            description = "Mira en qué categorías gastas más cada mes."
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoCard(
            icon = R.drawable.ic_tips,
            title = "Recibe recomendaciones",
            description = "Consejos inteligentes para ahorrar más."
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D6CDF)
            )
        ) {
            Text(
                text = "Empezar ahora",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Tu dinero, bajo control · v1.0",
            fontSize = 11.sp,
            color = Color(0xFF7A869A)
        )
    }
}

@Composable
fun InfoCard(
    icon: Int,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = Color(0xFFEAF1FF),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2A44)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 16.sp
                )
            }
        }
    }
}