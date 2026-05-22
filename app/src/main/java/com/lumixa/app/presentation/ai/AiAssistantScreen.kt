package com.lumixa.app.presentation.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.presentation.components.LumixaColors
import com.lumixa.app.presentation.viewmodel.AiAssistantViewModel
import com.lumixa.app.presentation.viewmodel.ChatAuthor
import com.lumixa.app.presentation.viewmodel.ChatMessage

private val suggestedQuestions = listOf(
    "¿Cómo puedo ahorrar más?",
    "¿Estoy gastando mucho?",
    "¿Voy bien con mis metas?",
    "Dame un resumen financiero"
)

@Composable
fun AiAssistantScreen(
    viewModel: AiAssistantViewModel,
    totalIncome: Double,
    totalExpenses: Double,
    totalSavings: Double,
    goals: List<GoalEntity>,
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var question by remember { mutableStateOf("") }

    LaunchedEffect(uiState.messages.size, uiState.isLoading) {
        if (uiState.messages.isNotEmpty() || uiState.isLoading) {
            listState.animateScrollToItem(uiState.messages.size)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "← Volver",
                color = Color(0xFF2D6CDF),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Asistente IA",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )
        }

        Text(
            text = "Tu guía financiera personal de LUMIXA.",
            color = Color(0xFF2B2B2B)
        )

        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.messages) { message ->
                    ChatBubble(message = message)
                }

                if (uiState.isLoading) {
                    item {
                        LoadingBubble()
                    }
                }
            }
        }

        uiState.error?.let {
            Text(text = it, color = Color(0xFFE11D48), fontWeight = FontWeight.SemiBold)
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Preguntas sugeridas",
                color = Color(0xFF0F2A44),
                fontWeight = FontWeight.SemiBold
            )
            suggestedQuestions.forEach { suggestion ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEAF0FF), RoundedCornerShape(10.dp))
                        .clickable { question = suggestion }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(text = suggestion, color = Color(0xFF0F2A44))
                }
            }
        }

        HorizontalDivider(color = Color(0xFFE5E7EB))

        OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            label = { Text("Escribe tu pregunta financiera") }
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
                question = ""
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Consultar a LUMIXA IA")
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.author == ChatAuthor.USER
    val bubbleColor = if (isUser) Color(0xFF2D6CDF) else Color(0xFF1FBF9F)
    val align = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = message.content,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun LoadingBubble() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFDFF7F2), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        CircularProgressIndicator(modifier = Modifier.height(18.dp), strokeWidth = 2.dp, color = Color(0xFF0F2A44))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "LUMIXA IA está analizando...", color = Color(0xFF0F2A44))
    }
}
