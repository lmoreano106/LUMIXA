package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lumixa.app.data.local.entity.ExpenseEntity
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.data.local.entity.IncomeEntity
import com.lumixa.app.data.local.entity.SavingsEntity
import com.lumixa.app.data.repository.AiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.roundToInt

data class ExpensePredictionUiState(
    val isLoading: Boolean = false,
    val riskLevel: String = "moderado",
    val prediction: String = "Aún no se ha generado la predicción.",
    val recommendation: String = "Registra más movimientos para mejorar la precisión.",
    val usedFallback: Boolean = false,
    val hasLoadedAtLeastOnce: Boolean = false
)

class ExpensePredictionViewModel(
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensePredictionUiState())
    val uiState: StateFlow<ExpensePredictionUiState> = _uiState.asStateFlow()

    fun loadPrediction(
        expenses: List<ExpenseEntity>,
        incomes: List<IncomeEntity>,
        savings: List<SavingsEntity>,
        goals: List<GoalEntity>,
        forceRefresh: Boolean = false
    ) {
        if (_uiState.value.isLoading) return
        if (_uiState.value.hasLoadedAtLeastOnce && !forceRefresh) return

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val fallback = buildLocalFallback(expenses, incomes, savings)

            if (expenses.isEmpty()) {
                _uiState.value = fallback.copy(
                    isLoading = false,
                    hasLoadedAtLeastOnce = true,
                    usedFallback = true
                )
                return@launch
            }

            val aiResult = aiRepository.generateExpensePrediction(
                expenses = expenses,
                incomes = incomes,
                savings = savings,
                goals = goals
            )

            val failed = aiResult == null
            _uiState.value = if (failed) {
                fallback.copy(
                    isLoading = false,
                    usedFallback = true,
                    hasLoadedAtLeastOnce = true
                )
            } else {
                ExpensePredictionUiState(
                    isLoading = false,
                    riskLevel = aiResult.riskLevel,
                    prediction = aiResult.prediction,
                    recommendation = aiResult.recommendation,
                    usedFallback = false,
                    hasLoadedAtLeastOnce = true
                )
            }
        }
    }

    private fun buildLocalFallback(
        expenses: List<ExpenseEntity>,
        incomes: List<IncomeEntity>,
        savings: List<SavingsEntity>
    ): ExpensePredictionUiState {
        val totalExpenses = expenses.sumOf { it.amount }
        val totalIncomes = incomes.sumOf { it.amount }
        val totalSavings = savings.sumOf { it.amount }

        val today = LocalDate.now()
        val dayOfMonth = today.dayOfMonth.coerceAtLeast(1)
        val daysInMonth = today.lengthOfMonth()
        val projectedMonthly = if (totalExpenses > 0) {
            (totalExpenses / dayOfMonth) * daysInMonth
        } else 0.0

        val weekly = expenseBucketsByWeek(expenses)
        val weeklyAvg = if (weekly.isNotEmpty()) weekly.average() else 0.0
        val weeklyLatest = weekly.lastOrNull() ?: 0.0

        val pressure = if (totalIncomes > 0) projectedMonthly / totalIncomes else 1.0
        val riskLevel = when {
            pressure >= 0.9 -> "alto"
            pressure >= 0.65 -> "moderado"
            else -> "bajo"
        }

        val topCategory = expenses
            .groupBy { it.category }
            .maxByOrNull { (_, list) -> list.sumOf { it.amount } }
            ?.key
            ?: "gastos variables"

        val trendMessage = if (weekly.size >= 2) {
            if (weeklyLatest > weeklyAvg) "Tu gasto semanal subió frente al promedio reciente."
            else "Tu gasto semanal está estable frente al promedio reciente."
        } else {
            "Aún no hay suficientes semanas para comparar tendencia semanal."
        }

        val savingsBuffer = if (totalSavings > 0) "Ahorros actuales: ${totalSavings.roundToInt()}." else ""

        return ExpensePredictionUiState(
            riskLevel = riskLevel,
            prediction = "Si mantienes este ritmo, gastarías aproximadamente ${projectedMonthly.roundToInt()} este mes. $trendMessage",
            recommendation = "Reduce gastos en la categoría más alta: $topCategory. $savingsBuffer".trim(),
            usedFallback = true
        )
    }

    private fun expenseBucketsByWeek(expenses: List<ExpenseEntity>): List<Double> {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("es", "ES"))
        return expenses.mapNotNull {
            try {
                LocalDate.parse(it.date.lowercase(Locale("es", "ES")), formatter) to it.amount
            } catch (_: DateTimeParseException) {
                null
            }
        }
            .groupBy { it.first.with(java.time.DayOfWeek.MONDAY) }
            .toSortedMap()
            .values
            .map { weekValues -> weekValues.sumOf { it.second } }
    }
}

class ExpensePredictionViewModelFactory(
    private val aiRepository: AiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpensePredictionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpensePredictionViewModel(aiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
