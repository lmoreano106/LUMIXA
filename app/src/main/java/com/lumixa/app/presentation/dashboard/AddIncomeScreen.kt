package com.lumixa.app.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddIncomeScreen(
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var incomeType by remember { mutableStateOf("Ingreso extra") }
    var description by remember { mutableStateOf("") }

    val incomeTypes = listOf("Ingreso extra", "Sueldo", "Regalo", "Otro")

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
            text = "Agregar ingreso",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2A44)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Registra un ingreso para actualizar tu saldo.",
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text("MONTO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { char -> char.isDigit() } },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ejemplo: 50000") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text("TIPO DE INGRESO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))

        Spacer(modifier = Modifier.height(12.dp))

        incomeTypes.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { type ->
                    IncomeTypeChip(
                        text = type,
                        selected = incomeType == type,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        onClick = { incomeType = type }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text("DESCRIPCIÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            placeholder = { Text("Ejemplo: pago de trabajo, apoyo familiar...") },
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D6CDF))
        ) {
            Text(
                text = "Guardar ingreso",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun IncomeTypeChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF2D6CDF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 3.dp else 1.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
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