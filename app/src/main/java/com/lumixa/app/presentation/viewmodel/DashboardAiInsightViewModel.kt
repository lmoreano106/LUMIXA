package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.data.repository.AiRepository
import com.lumixa.app.data.repository.FinancialContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardAiInsightUiState(
    val isLoading: Boolean = false,
    val insight: String = "Registra tus ingresos y gastos para recibir consejos personalizados.",
    val hasLoadedAtLeastOnce: Boolean = false
)

class DashboardAiInsightViewModel(
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardAiInsightUiState())
    val uiState: StateFlow<DashboardAiInsightUiState> = _uiState.asStateFlow()

    fun loadInsight(
        totalIncome: Double,
        totalExpenses: Double,
        totalSavings: Double,
        goals: List<GoalEntity>,
        forceRefresh: Boolean = false
    ) {
        if (_uiState.value.isLoading) return
        if (_uiState.value.hasLoadedAtLeastOnce && !forceRefresh) return

        if (totalIncome <= 0.0 && totalExpenses <= 0.0 && totalSavings <= 0.0 && goals.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                insight = "Registra tus ingresos y gastos para recibir consejos personalizados.",
                hasLoadedAtLeastOnce = true
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val goalsSummary = if (goals.isEmpty()) {
                "Sin metas registradas"
            } else {
                goals.joinToString(separator = "; ") {
                    "${it.name}: ${it.savedAmount}/${it.targetAmount}, fecha ${it.targetDate}"
                }
            }

            val insight = aiRepository.generateDashboardInsight(
                context = FinancialContext(
                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses,
                    totalSavings = totalSavings,
                    goalsSummary = goalsSummary
                )
            )

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                insight = insight,
                hasLoadedAtLeastOnce = true
            )
        }
    }
}

class DashboardAiInsightViewModelFactory(
    private val aiRepository: AiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardAiInsightViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardAiInsightViewModel(aiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
