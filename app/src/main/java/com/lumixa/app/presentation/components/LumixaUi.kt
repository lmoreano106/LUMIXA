package com.lumixa.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.lumixa.app.ui.theme.LumixaBlue
import com.lumixa.app.ui.theme.LumixaMint
import com.lumixa.app.ui.theme.LumixaMutedLight
import com.lumixa.app.ui.theme.LumixaNavy
import com.lumixa.app.ui.theme.LumixaTextSecondaryDark

object LumixaColors {
    val Navy = LumixaNavy
    val Blue = LumixaBlue
    val Mint = LumixaMint

    @Composable
    fun surface() = MaterialTheme.colorScheme.background

    @Composable
    fun textPrimary() = MaterialTheme.colorScheme.onSurface

    @Composable
    fun textSecondary() = if (MaterialTheme.colorScheme.background == Color(0xFF0B1220)) {
        LumixaTextSecondaryDark
    } else {
        LumixaMutedLight
    }
}

@Composable
fun LumixaEmptyStateCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, color = LumixaColors.textPrimary(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = subtitle, color = LumixaColors.textSecondary(), fontSize = 13.sp)
        }
    }
}

@Composable
fun LumixaLoadingCard(text: String = "Cargando información") {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            LumixaColors.surface(),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CircularProgressIndicator(color = LumixaColors.Blue)
                Text(text = text, color = LumixaColors.textSecondary(), fontSize = 13.sp)
            }
        }
    }
}
