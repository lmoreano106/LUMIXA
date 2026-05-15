package com.lumixa.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lumixa.app.data.provider.DatabaseProvider
import com.lumixa.app.data.repository.ExpenseRepository
import com.lumixa.app.data.repository.IncomeRepository
import com.lumixa.app.presentation.auth.login.LoginScreen
import com.lumixa.app.presentation.auth.register.RegisterScreen
import com.lumixa.app.presentation.dashboard.AddIncomeScreen
import com.lumixa.app.presentation.expenses.AddExpenseScreen
import com.lumixa.app.presentation.goals.CreateGoalScreen
import com.lumixa.app.presentation.main.MainScreen
import com.lumixa.app.presentation.onboarding.CurrencyScreen
import com.lumixa.app.presentation.onboarding.IncomeScreen
import com.lumixa.app.presentation.onboarding.OnboardingScreen
import com.lumixa.app.presentation.viewmodel.ExpenseViewModel
import com.lumixa.app.presentation.viewmodel.ExpenseViewModelFactory
import com.lumixa.app.presentation.viewmodel.IncomeViewModel
import com.lumixa.app.presentation.viewmodel.IncomeViewModelFactory

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current

    val database = DatabaseProvider.getDatabase(context)

    val expenseRepository = ExpenseRepository(
        expenseDao = database.expenseDao()
    )

    val expenseViewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModelFactory(expenseRepository)
    )

    val incomeRepository = IncomeRepository(
        incomeDao = database.incomeDao()
    )

    val incomeViewModel: IncomeViewModel = viewModel(
        factory = IncomeViewModelFactory(incomeRepository)
    )

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
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Onboarding.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
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
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Onboarding.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.Dashboard.route) {
            MainScreen(
                incomeViewModel = incomeViewModel,
                expenseViewModel = expenseViewModel,

                onAddExpenseClick = {
                    navController.navigate(Routes.AddExpense.route)
                },

                onAddIncomeClick = {
                    navController.navigate(Routes.AddIncome.route)
                },

                onGoalClick = {
                    navController.navigate(Routes.CreateGoal.route)
                },

                onLogoutClick = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Onboarding.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.AddExpense.route) {
            AddExpenseScreen(
                expenseViewModel = expenseViewModel,
                onSaveClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.AddIncome.route) {
            AddIncomeScreen(
                incomeViewModel = incomeViewModel,
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

        composable(Routes.Expenses.route) {}
        composable(Routes.Statistics.route) {}
        composable(Routes.Goal.route) {}
        composable(Routes.Admin.route) {}
    }
}