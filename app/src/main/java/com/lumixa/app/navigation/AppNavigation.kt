package com.lumixa.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lumixa.app.presentation.auth.login.LoginScreen
import com.lumixa.app.presentation.auth.register.RegisterScreen
import com.lumixa.app.presentation.expenses.AddExpenseScreen
import com.lumixa.app.presentation.main.MainScreen
import com.lumixa.app.presentation.onboarding.CurrencyScreen
import com.lumixa.app.presentation.onboarding.IncomeScreen
import com.lumixa.app.presentation.onboarding.OnboardingScreen
import com.lumixa.app.presentation.dashboard.AddIncomeScreen
import com.lumixa.app.presentation.goals.CreateGoalScreen
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Onboarding.route
    ) {

        composable(Routes.Onboarding.route) {
            OnboardingScreen(
                onStartClick = {
                    navController.navigate(Routes.Login.route)
                }
            )
        }

        composable(Routes.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Routes.Dashboard.route)
                },
                onRegisterClick = {
                    navController.navigate(Routes.Register.route)
                },
                onBackToHomeClick = {
                    navController.navigate(Routes.Onboarding.route)
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                onCreateAccountClick = {
                    navController.navigate(Routes.Currency.route)
                },
                onBackToLoginClick = {
                    navController.popBackStack()
                },
                onBackToHomeClick = {
                    navController.navigate(Routes.Onboarding.route)
                }
            )
        }

        composable(Routes.Currency.route) {
            CurrencyScreen(
                onContinueClick = {
                    navController.navigate(Routes.Income.route)
                }
            )
        }

        composable(Routes.Income.route) {
            IncomeScreen(
                onContinueClick = {
                    navController.navigate(Routes.Dashboard.route)
                }
            )
        }

        composable(Routes.Dashboard.route) {
            MainScreen(
                onAddExpenseClick = {
                    navController.navigate(Routes.AddExpense.route)
                },

                onAddIncomeClick = {
                    navController.navigate(Routes.AddIncome.route)
                },

                onGoalClick = {
                    navController.navigate(Routes.CreateGoal.route)
                }
            )
        }

        composable(Routes.AddExpense.route) {
            AddExpenseScreen(
                onSaveClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.CreateGoal.route) {
            CreateGoalScreen(
                onSaveClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.Expenses.route) {

        }

        composable(Routes.Statistics.route) {

        }

        composable(Routes.Goal.route) {

        }

        composable(Routes.Admin.route) {

        }

        composable(Routes.AddIncome.route) {
            AddIncomeScreen(
                onSaveClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}