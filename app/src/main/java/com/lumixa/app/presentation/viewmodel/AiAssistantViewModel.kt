package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.data.repository.AiRepository
import com.lumixa.app.data.repository.FinancialContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AiAssistantUiState(
    val isLoading: Boolean = false,
    val response: String = "",
    val error: String? = null
)

class AiAssistantViewModel(
    private val repository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    fun ask(
        question: String,
        totalIncome: Double,
        totalExpenses: Double,
        totalSavings: Double,
        goals: List<GoalEntity>
    ) {
        if (question.isBlank()) {
            _uiState.value = AiAssistantUiState(error = "Escribe una pregunta para continuar.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AiAssistantUiState(isLoading = true)
            runCatching {
                val goalsSummary = if (goals.isEmpty()) {
                    "Sin metas registradas"
                } else {
                    goals.joinToString(separator = "; ") {
                        "${it.name}: ${it.savedAmount}/${it.targetAmount}, fecha ${it.targetDate}"
                    }
                }

                repository.askFinancialAssistant(
                    question = question,
                    context = FinancialContext(
                        totalIncome = totalIncome,
                        totalExpenses = totalExpenses,
                        totalSavings = totalSavings,
                        goalsSummary = goalsSummary
                    )
                )
            }.onSuccess { answer ->
                _uiState.value = AiAssistantUiState(response = answer)
            }.onFailure {
                _uiState.value = AiAssistantUiState(
                    error = "No se pudo obtener respuesta de IA. Verifica tu conexión e intenta de nuevo."
                )
            }
        }
    }
}
