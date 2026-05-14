package com.lumixa.app.presentation.expenses

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.presentation.viewmodel.ExpenseViewModel

@Composable
fun AddExpenseScreen(
    expenseViewModel: ExpenseViewModel,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Comida") }
    var customCategory by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val categories = listOf("Comida", "Transporte", "Ocio", "Estudios", "Otros")

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
            modifier = Modifier.clickable {
                onBackClick()
            }
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Registrar gasto",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2A44)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Agrega un movimiento para actualizar tu control diario.",
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "MONTO",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { value ->
                amount = value.filter { it.isDigit() }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ejemplo: 23000") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "CATEGORÍA",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(12.dp))

        categories.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { category ->
                    CategoryChip(
                        text = category,
                        selected = selectedCategory == category,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        onClick = {
                            selectedCategory = category
                        }
                    )
                }

                if (rowItems.size == 1) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )
                }
            }
        }

        if (selectedCategory == "Otros") {
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = customCategory,
                onValueChange = { customCategory = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nombre de la categoría") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "NOTA OPCIONAL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            placeholder = { Text("Ejemplo: Almuerzo en la universidad") },
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val finalCategory =
                    if (selectedCategory == "Otros" && customCategory.isNotBlank()) {
                        customCategory
                    } else {
                        selectedCategory
                    }

                val finalDescription =
                    if (note.isNotBlank()) {
                        note
                    } else {
                        "Sin descripción"
                    }

                expenseViewModel.addExpense(
                    category = finalCategory,
                    description = finalDescription,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    date = "14 mayo 2026",
                    time = "4:20 PM"
                )

                onSaveClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D6CDF)
            )
        ) {
            Text(
                text = "Guardar gasto",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val icon = when (text) {
        "Comida" -> "🍔"
        "Transporte" -> "🚗"
        "Ocio" -> "🎮"
        "Estudios" -> "📚"
        else -> "✨"
    }

    Card(
        modifier = modifier
            .height(58.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF2D6CDF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 3.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                color = if (selected) Color.White else Color(0xFF0F2A44),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}