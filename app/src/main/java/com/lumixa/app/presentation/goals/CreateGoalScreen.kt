package com.lumixa.app.presentation.goals

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.presentation.viewmodel.GoalViewModel

@Composable
fun CreateGoalScreen(
    goalViewModel: GoalViewModel,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var goalName by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(horizontal = 28.dp, vertical = 34.dp)
    ) {
        Text(
            text = "← Volver",
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.clickable { onBackClick() }
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Crear meta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2A44)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Define una meta financiera y LUMIXA calculará cuánto debes ahorrar.",
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(24.dp))

        GoalTextField(
            label = "NOMBRE DE LA META",
            value = goalName,
            onValueChange = { goalName = it },
            placeholder = "Ejemplo: Laptop para estudios"
        )

        Spacer(modifier = Modifier.height(16.dp))

        GoalTextField(
            label = "MONTO OBJETIVO",
            value = targetAmount,
            onValueChange = { value ->
                targetAmount = value.filter { it.isDigit() }
            },
            placeholder = "Ejemplo: 190000",
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        GoalTextField(
            label = "FECHA LÍMITE",
            value = targetDate,
            onValueChange = { targetDate = it },
            placeholder = "Ejemplo: 30/06/2026"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF1FF))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Recomendación inteligente",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2A44)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Al guardar tu meta, LUMIXA la mostrará en el inicio y calculará tu progreso financiero.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                goalViewModel.addGoal(
                    name = if (goalName.isNotBlank()) goalName else "Meta sin nombre",
                    targetAmount = targetAmount.toDoubleOrNull() ?: 0.0,
                    savedAmount = 0.0,
                    targetDate = if (targetDate.isNotBlank()) targetDate else "Sin fecha"
                )

                onSaveClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D6CDF))
        ) {
            Text(
                text = "Guardar meta",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun GoalTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6B7280)
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}