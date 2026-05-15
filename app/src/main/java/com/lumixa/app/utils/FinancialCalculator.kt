package com.lumixa.app.utils

object FinancialCalculator {

    fun calculateMonthlyIncome(
        totalIncome: Double
    ): Double {
        return totalIncome
    }

    fun calculateDailyBudget(
        monthlyIncome: Double
    ): Double {
        return monthlyIncome / 30
    }

    fun calculateSpent(
        totalExpenses: Double
    ): Double {
        return totalExpenses
    }

    fun calculateAvailableToday(
        dailyBudget: Double,
        spentToday: Double
    ): Double {
        return dailyBudget - spentToday
    }

    fun calculateSavings(
        availableToday: Double
    ): Double {
        return availableToday.coerceAtLeast(0.0)
    }

    fun calculateBalance(
        monthlyIncome: Double,
        totalExpenses: Double
    ): Double {
        return monthlyIncome - totalExpenses
    }

    fun calculateGoalProgress(
        savedAmount: Double,
        targetAmount: Double
    ): Float {

        if (targetAmount <= 0) {
            return 0f
        }

        return (
                savedAmount / targetAmount
                ).coerceIn(0.0, 1.0).toFloat()
    }
}