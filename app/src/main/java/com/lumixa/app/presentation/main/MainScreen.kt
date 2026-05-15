package com.lumixa.app.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.lumixa.app.presentation.dashboard.DashboardScreen
import com.lumixa.app.presentation.expenses.ExpensesScreen
import com.lumixa.app.presentation.statistics.StatisticsScreen
import com.lumixa.app.presentation.viewmodel.ExpenseViewModel
import com.lumixa.app.presentation.viewmodel.IncomeViewModel

@Composable
fun MainScreen(
    expenseViewModel: ExpenseViewModel,
    incomeViewModel: IncomeViewModel,
    onAddExpenseClick: () -> Unit,
    onAddIncomeClick: () -> Unit,
    onGoalClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    var selectedTab by remember {
        mutableStateOf(0)
    }

    Scaffold(

        bottomBar = {

            NavigationBar {

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
            }
        }

    ) { innerPadding ->

        when (selectedTab) {

            0 -> DashboardScreen(
                expenseViewModel = expenseViewModel,
                incomeViewModel = incomeViewModel,
                modifier = Modifier.padding(innerPadding),
                onAddExpenseClick = onAddExpenseClick,
                onAddIncomeClick = onAddIncomeClick,
                onGoalClick = onGoalClick,
                onLogoutClick = onLogoutClick
            )

            1 -> ExpensesScreen(
                expenseViewModel = expenseViewModel
            )

            2 -> StatisticsScreen(
                expenseViewModel = expenseViewModel
            )
        }
    }
}