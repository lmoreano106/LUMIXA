package com.lumixa.app.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IncomeScreen(
    onContinueClick: () -> Unit
) {
    var income by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Mensual") }

    val incomeValue = income.toDoubleOrNull() ?: 0.0

    val monthlyIncome = when (frequency) {
        "Diario" -> incomeValue * 30
        "Semanal" -> incomeValue * 4
        "Quincenal" -> incomeValue * 2
        else -> incomeValue
    }

    val dailyBudget = if (monthlyIncome > 0) monthlyIncome / 30 else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(horizontal = 28.dp, vertical = 34.dp)
    ) {
        Text(
            text = "Configura tus ingresos",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2A44)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Calcularemos tu presupuesto diario inteligente.",
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "FRECUENCIA",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(10.dp))

        FrequencySelector(
            selected = frequency,
            onSelected = { frequency = it }
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "INGRESO",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = income,
            onValueChange = { value ->
                income = value.filter { it.isDigit() }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ejemplo: 1130000") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(26.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F2A44),
                                Color(0xFF2D6CDF)
                            )
                        )
                    )
                    .padding(22.dp)
            ) {
                Text(
                    text = "Presupuesto diario recomendado",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "$${dailyBudget.toInt()}",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Este es el monto aproximado que puedes gastar por día.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onContinueClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D6CDF)
            )
        ) {
            Text(
                text = "Finalizar configuración",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun FrequencySelector(
    selected: String,
    onSelected: (String) -> Unit
) {
    val options = listOf("Diario", "Semanal", "Quincenal", "Mensual")

    Column {
        options.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { option ->
                    FrequencyChip(
                        text = option,
                        selected = selected == option,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        onClick = {
                            onSelected(option)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FrequencyChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(46.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF2D6CDF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 3.dp else 1.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else Color(0xFF0F2A44),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}