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
import kotlinx.coroutines.withTimeout

private const val LUMIXA_IA_QUOTA_FALLBACK = "LUMIXA IA alcanzó el límite temporal de consultas. Intenta nuevamente en unos minutos."
private const val LUMIXA_IA_TIMEOUT_MS = 15_000L

private fun Throwable.isGeminiQuotaExceeded(): Boolean {
    val messageText = message.orEmpty()
    val classText = this::class.simpleName.orEmpty()
    return classText.contains("QuotaExceededException", ignoreCase = true) ||
        messageText.contains("Quota exceeded", ignoreCase = true)
}

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
            runCatching {
                withTimeout(LUMIXA_IA_TIMEOUT_MS) {
                    val goalsSummary = if (goals.isEmpty()) {
                        "Sin metas registradas"
                    } else {
                        goals.joinToString(separator = "; ") {
                            "${it.name}: ${it.savedAmount}/${it.targetAmount}, fecha ${it.targetDate}"
                        }
                    }

                    aiRepository.generateDashboardInsight(
                        context = FinancialContext(
                            totalIncome = totalIncome,
                            totalExpenses = totalExpenses,
                            totalSavings = totalSavings,
                            goalsSummary = goalsSummary
                        )
                    )
                }
            }.onSuccess { insight ->
                _uiState.value = _uiState.value.copy(
                    insight = insight,
                    hasLoadedAtLeastOnce = true
                )
            }.onFailure { throwable ->
                val message = if (throwable.isGeminiQuotaExceeded()) {
                    LUMIXA_IA_QUOTA_FALLBACK
                } else {
                    "No se pudo obtener insight de IA en este momento."
                }
                _uiState.value = _uiState.value.copy(
                    insight = message,
                    hasLoadedAtLeastOnce = true
                )
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
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
