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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.presentation.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGoalScreen(
    goalViewModel: GoalViewModel,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    existingGoal: GoalEntity? = null
) {

    var goalName by remember {
        mutableStateOf(existingGoal?.name ?: "")
    }

    var targetAmount by remember {
        mutableStateOf(
            existingGoal?.targetAmount?.toInt()?.toString() ?: ""
        )
    }

    var targetDate by remember {
        mutableStateOf(existingGoal?.targetDate ?: "")
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState()

    val isEditing = existingGoal != null

    if (showDatePicker) {

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        datePickerState.selectedDateMillis?.let { millis ->

                            val formatter = SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            )

                            targetDate =
                                formatter.format(Date(millis))
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancelar")
                }
            }

        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }

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
            text = if (isEditing) {
                "Editar meta"
            } else {
                "Crear meta"
            },

            fontSize = 28.sp,

            fontWeight = FontWeight.Bold,

            color = Color(0xFF0F2A44)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isEditing) {
                "Actualiza tu objetivo financiero."
            } else {
                "Define una meta financiera y LUMIXA calculará cuánto debes ahorrar."
            },

            fontSize = 13.sp,

            lineHeight = 19.sp,

            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(24.dp))

        GoalTextField(
            label = "NOMBRE DE LA META",

            value = goalName,

            onValueChange = {
                goalName = it
            },

            placeholder = "Ejemplo: Laptop para estudios"
        )

        Spacer(modifier = Modifier.height(16.dp))

        GoalTextField(
            label = "MONTO OBJETIVO",

            value = targetAmount,

            onValueChange = { value ->
                targetAmount =
                    value.filter { it.isDigit() }
            },

            placeholder = "Ejemplo: 190000",

            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "FECHA LÍMITE",

            fontSize = 11.sp,

            fontWeight = FontWeight.Bold,

            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDatePicker = true
                },

            shape = RoundedCornerShape(14.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),

            elevation = CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
        ) {

            Text(
                text = if (targetDate.isNotBlank()) {
                    targetDate
                } else {
                    "Seleccionar fecha límite"
                },

                modifier = Modifier.padding(18.dp),

                fontSize = 14.sp,

                color = if (targetDate.isNotBlank()) {
                    Color(0xFF0F2A44)
                } else {
                    Color(0xFF6B7280)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {

                if (isEditing && existingGoal != null) {

                    goalViewModel.updateGoal(
                        existingGoal.copy(
                            name = goalName,
                            targetAmount = targetAmount.toDoubleOrNull() ?: 0.0,
                            targetDate = targetDate
                        )
                    )

                } else {

                    goalViewModel.addGoal(
                        name = if (goalName.isNotBlank()) {
                            goalName
                        } else {
                            "Meta sin nombre"
                        },

                        targetAmount =
                            targetAmount.toDoubleOrNull() ?: 0.0,

                        savedAmount = 0.0,

                        targetDate = if (targetDate.isNotBlank()) {
                            targetDate
                        } else {
                            "Sin fecha"
                        }
                    )
                }

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
                text = if (isEditing) {
                    "Guardar cambios"
                } else {
                    "Guardar meta"
                },

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

        placeholder = {
            Text(placeholder)
        },

        singleLine = true,

        shape = RoundedCornerShape(14.dp),

        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        )
    )
}