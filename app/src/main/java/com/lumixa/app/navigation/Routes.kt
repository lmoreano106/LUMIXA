package com.lumixa.app.navigation

sealed class Routes(val route: String) {
    data object Onboarding : Routes("onboarding")
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object Currency : Routes("currency")
    data object Income : Routes("income")
    data object Dashboard : Routes("dashboard")
    data object Expenses : Routes("expenses")
    data object Statistics : Routes("statistics")
    data object Goal : Routes("goal")
    data object Admin : Routes("admin")
    data object AddExpense : Routes("add_expense")
    data object AddIncome : Routes("add_income")
    data object CreateGoal : Routes("create_goal")
}