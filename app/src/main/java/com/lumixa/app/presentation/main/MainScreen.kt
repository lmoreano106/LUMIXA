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
@Composable
fun MainScreen(
    onAddExpenseClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Inicio") },
                    icon = { Text("🏠") }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Gastos") },
                    icon = { Text("💸") }
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Estadísticas") },
                    icon = { Text("📊") }
                )
            }
        }
    ) { innerPadding ->

        when (selectedTab) {
            0 -> DashboardScreen(
                modifier = Modifier.padding(innerPadding),
                onAddExpenseClick = onAddExpenseClick
            )

            1 -> ExpensesScreen()

            2 -> Text(
                text = "Módulo Estadísticas",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}