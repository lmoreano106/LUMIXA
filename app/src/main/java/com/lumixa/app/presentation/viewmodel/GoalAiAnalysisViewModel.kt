package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lumixa.app.data.repository.AiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil


data class GoalAiAnalysisUiState(
    val isLoading: Boolean = false,
    val analysis: String = "Aún no se ha generado análisis para esta meta.",
    val usedFallback: Boolean = false,
    val hasLoadedAtLeastOnce: Boolean = false
)

class GoalAiAnalysisViewModel(
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalAiAnalysisUiState())
    val uiState: StateFlow<GoalAiAnalysisUiState> = _uiState.asStateFlow()

    fun loadAnalysis(
        goalName: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDate: String,
        totalIncome: Double,
        totalExpenses: Double,
        totalSavings: Double,
        daysLeft: Int,
        forceRefresh: Boolean = false
    ) {
        if (_uiState.value.isLoading) return
        if (_uiState.value.hasLoadedAtLeastOnce && !forceRefresh) return

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val aiText = aiRepository.generateGoalAnalysis(
                goalName = goalName,
                targetAmount = targetAmount,
                savedAmount = savedAmount,
                targetDate = targetDate,
                totalIncome = totalIncome,
                totalExpenses = totalExpenses,
                totalSavings = totalSavings
            )

            val fallback = buildLocalFallback(
                targetAmount = targetAmount,
                savedAmount = savedAmount,
                totalIncome = totalIncome,
                totalExpenses = totalExpenses,
                daysLeft = daysLeft
            )

            val failed = aiText.startsWith("No fue posible") || aiText.startsWith("No se pudo")

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                analysis = if (failed) fallback else aiText,
                usedFallback = failed,
                hasLoadedAtLeastOnce = true
            )
        }
    }

    private fun buildLocalFallback(
        targetAmount: Double,
        savedAmount: Double,
        totalIncome: Double,
        totalExpenses: Double,
        daysLeft: Int
    ): String {
        val remaining = (targetAmount - savedAmount).coerceAtLeast(0.0)
        val weeklyBase = if (daysLeft > 0) ceil(remaining / daysLeft * 7.0).toInt() else remaining.toInt()
        val dailyBase = if (daysLeft > 0) ceil(remaining / daysLeft).toInt() else remaining.toInt()
        val surplus = (totalIncome - totalExpenses).coerceAtLeast(0.0)
        val reachable = remaining <= 0.0 || (daysLeft > 0 && surplus > 0.0)
        val recommendation = if (surplus <= 0.0) {
            "Recomendación: reduce gastos variables y separa un monto fijo apenas recibas ingresos."
        } else {
            "Recomendación: automatiza un ahorro semanal y evita tocar ese dinero."
        }

        return """
            Alcanzable: ${if (reachable) "Sí" else "No"}.
            Faltante: ${remaining.toInt()}.
            Ahorro sugerido: diario ${dailyBase} / semanal ${weeklyBase}.
            $recommendation
        """.trimIndent()
    }
}

class GoalAiAnalysisViewModelFactory(
    private val aiRepository: AiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalAiAnalysisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoalAiAnalysisViewModel(aiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
