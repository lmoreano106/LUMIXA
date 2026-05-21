package com.lumixa.app.presentation.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.presentation.components.LumixaColors
import com.lumixa.app.presentation.viewmodel.AiAssistantViewModel

@Composable
fun AiAssistantScreen(
    viewModel: AiAssistantViewModel,
    totalIncome: Double,
    totalExpenses: Double,
    totalSavings: Double,
    goals: List<GoalEntity>
) {
    val uiState by viewModel.uiState.collectAsState()
    var question by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumixaColors.Surface)
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Asistente IA", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2A44))
        Text("Haz una consulta financiera y recibe recomendaciones personalizadas.")

        OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            label = { Text("Pregunta financiera") }
        )

        Button(
            onClick = {
                viewModel.ask(
                    question = question,
                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses,
                    totalSavings = totalSavings,
                    goals = goals
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("Consultar")
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
            Text("Analizando tu contexto financiero...")
        }

        uiState.error?.let {
            Text(text = it, color = Color(0xFFE11D48), fontWeight = FontWeight.SemiBold)
        }

        if (uiState.response.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Respuesta", fontWeight = FontWeight.Bold, color = Color(0xFF0F2A44))
            Text(uiState.response)
        }
    }
}
