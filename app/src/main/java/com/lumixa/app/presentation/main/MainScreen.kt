package com.lumixa.app.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.lumixa.app.presentation.dashboard.DashboardScreen
import com.lumixa.app.presentation.expenses.ExpensesScreen
import com.lumixa.app.presentation.profile.ProfileScreen
import com.lumixa.app.presentation.statistics.StatisticsScreen
import com.lumixa.app.presentation.viewmodel.ExpenseViewModel
import com.lumixa.app.presentation.viewmodel.GoalViewModel
import com.lumixa.app.presentation.viewmodel.IncomeViewModel
import com.lumixa.app.presentation.viewmodel.ExpensePredictionViewModel
import com.lumixa.app.presentation.viewmodel.SavingsViewModel
import com.lumixa.app.presentation.viewmodel.DashboardAiInsightViewModel

@Composable
fun MainScreen(
    expenseViewModel: ExpenseViewModel,
    incomeViewModel: IncomeViewModel,
    goalViewModel: GoalViewModel,
    onAddExpenseClick: () -> Unit,
    onAddIncomeClick: () -> Unit,
    onGoalClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAiAssistantClick: () -> Unit,
    savingsViewModel: SavingsViewModel,
    dashboardAiInsightViewModel: DashboardAiInsightViewModel,
    expensePredictionViewModel: ExpensePredictionViewModel,
    darkModeEnabled: Boolean,
    onThemeChange: (Boolean) -> Unit
) {

    var selectedTab by remember {
        mutableStateOf(0)
    }

    Scaffold(

        bottomBar = {

            NavigationBar(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface) {

                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                    },
                    label = {
                        Text("Inicio")
                    },
                    icon = {
                        Text("🏠")
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                    },
                    label = {
                        Text("Gastos")
                    },
                    icon = {
                        Text("💸")
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                    },
                    label = {
                        Text("Estadísticas")
                    },
                    icon = {
                        Text("📊")
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                    },
                    label = {
                        Text("Perfil")
                    },
                    icon = {
                        Text("👤")
                    }
                )
            }
        }

    ) { innerPadding ->

        when (selectedTab) {

            0 -> DashboardScreen(
                expenseViewModel = expenseViewModel,
                incomeViewModel = incomeViewModel,
                goalViewModel = goalViewModel,
                modifier = Modifier.padding(innerPadding),
                onAddExpenseClick = onAddExpenseClick,
                onAddIncomeClick = onAddIncomeClick,
                onGoalClick = onGoalClick,
                onLogoutClick = onLogoutClick,
                onAiAssistantClick = onAiAssistantClick,
                savingsViewModel = savingsViewModel,
                dashboardAiInsightViewModel = dashboardAiInsightViewModel
            )

            1 -> ExpensesScreen(
                expenseViewModel = expenseViewModel
            )

            2 -> StatisticsScreen(
                expenseViewModel = expenseViewModel,
                goalViewModel = goalViewModel,
                savingsViewModel = savingsViewModel,
                incomeViewModel = incomeViewModel,
                expensePredictionViewModel = expensePredictionViewModel
            )

            3 -> ProfileScreen(
                onLogoutClick = onLogoutClick,
                darkModeEnabled = darkModeEnabled,
                onThemeChange = onThemeChange
            )
        }
    }
}
