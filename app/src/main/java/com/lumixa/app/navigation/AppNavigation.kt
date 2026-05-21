package com.lumixa.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lumixa.app.data.provider.DatabaseProvider
import com.lumixa.app.data.repository.ExpenseRepository
import com.lumixa.app.data.repository.FirestoreRepository
import com.lumixa.app.data.repository.IncomeRepository
import com.lumixa.app.presentation.admin.AdminScreen
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
import com.lumixa.app.data.repository.GoalRepository
import com.lumixa.app.presentation.viewmodel.GoalViewModel
import com.lumixa.app.presentation.viewmodel.GoalViewModelFactory
import com.lumixa.app.data.repository.SavingsRepository
import com.lumixa.app.presentation.viewmodel.SavingsViewModel
import com.lumixa.app.presentation.viewmodel.SavingsViewModelFactory
import com.lumixa.app.data.repository.AuthRepository
import com.lumixa.app.presentation.viewmodel.AuthViewModel
import com.lumixa.app.presentation.viewmodel.AuthViewModelFactory
import com.lumixa.app.presentation.goals.GoalDetailScreen
import androidx.compose.runtime.LaunchedEffect
import com.google.firebase.auth.FirebaseAuth
import com.lumixa.app.data.repository.LocalDataMigrationRepository
import com.lumixa.app.presentation.viewmodel.AiAssistantViewModel
import com.lumixa.app.presentation.ai.AiAssistantScreen
import com.lumixa.app.data.repository.AiRepository
import com.lumixa.app.BuildConfig
@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val authRepository = AuthRepository()

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )
    val database = DatabaseProvider.getDatabase(context)
    val firestoreRepository = FirestoreRepository()
    val expenseRepository = ExpenseRepository(
        expenseDao = database.expenseDao(),
        firestoreRepository = firestoreRepository
    )

    val expenseViewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModelFactory(expenseRepository)
    )

    val incomeRepository = IncomeRepository(
        incomeDao = database.incomeDao(),
        firestoreRepository = firestoreRepository
    )

    val incomeViewModel: IncomeViewModel = viewModel(
        factory = IncomeViewModelFactory(incomeRepository)
    )
    val goalRepository = GoalRepository(
        goalDao = database.goalDao(),
        firestoreRepository = firestoreRepository
    )

    val goalViewModel: GoalViewModel = viewModel(
        factory = GoalViewModelFactory(goalRepository)
    )
    val savingsRepository = SavingsRepository(
        savingsDao = database.savingsDao(),
        firestoreRepository = firestoreRepository
    )

    val savingsViewModel: SavingsViewModel = viewModel(
        factory = SavingsViewModelFactory(savingsRepository)
    )
    val aiRepository = AiRepository(BuildConfig.GEMINI_API_KEY)

    val aiAssistantViewModel = AiAssistantViewModel(aiRepository)

    val migrationRepository = LocalDataMigrationRepository(
        expenseDao = database.expenseDao(),
        incomeDao = database.incomeDao(),
        goalDao = database.goalDao(),
        savingsDao = database.savingsDao(),
        firestoreRepository = firestoreRepository
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
                authViewModel = authViewModel,
                onUserLoginClick = {
                    incomeViewModel.refreshIncomes()
                    expenseViewModel.refreshExpenses()
                    goalViewModel.refreshGoals()
                    savingsViewModel.refreshSavings()

                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Onboarding.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onAdminLoginClick = {
                    navController.navigate(Routes.Admin.route) {
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
                authViewModel = authViewModel,
                onCreateAccountClick = {
                    incomeViewModel.refreshIncomes()
                    expenseViewModel.refreshExpenses()
                    goalViewModel.refreshGoals()
                    savingsViewModel.refreshSavings()

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
                incomeViewModel = incomeViewModel,
                onContinueClick = {
                    incomeViewModel.refreshIncomes()

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
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            LaunchedEffect(userId) {
                if (!userId.isNullOrBlank()) {
                    migrationRepository.migrateCurrentUserDataToFirestore()
                }
            }

            MainScreen(
                goalViewModel = goalViewModel,

                incomeViewModel = incomeViewModel,
                expenseViewModel = expenseViewModel,
                savingsViewModel = savingsViewModel,
                onAddExpenseClick = {
                    navController.navigate(Routes.AddExpense.route)
                },

                onAddIncomeClick = {
                    navController.navigate(Routes.AddIncome.route)
                },

                onGoalClick = {
                    if (goalViewModel.goals.value.isEmpty()) {
                        navController.navigate(Routes.CreateGoal.route)
                    } else {
                        navController.navigate(Routes.GoalDetail.route)
                    }
                },
                onAiAssistantClick = {
                    navController.navigate(Routes.AiAssistant.route)
                },
                onLogoutClick = {

                    authViewModel.logout()

                    incomeViewModel.clearData()

                    expenseViewModel.clearData()

                    goalViewModel.clearData()

                    savingsViewModel.clearData()

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
                goalViewModel = goalViewModel,

                onSaveClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.Goal.route) {
            CreateGoalScreen(
                goalViewModel = goalViewModel,
                existingGoal = goalViewModel.goals.value.firstOrNull(),
                onSaveClick = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.GoalDetail.route) {
            GoalDetailScreen(
                goalViewModel = goalViewModel,
                savingsViewModel = savingsViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = {
                    navController.navigate(Routes.Goal.route)
                }
            )
        }
        composable(Routes.Expenses.route) {}
        composable(Routes.Statistics.route) {}
        composable(Routes.Admin.route) {
            AdminScreen(
                onAiAssistantClick = {
                    navController.navigate(Routes.AiAssistant.route)
                },
                onLogoutClick = {
                    authViewModel.logout()

                    incomeViewModel.clearData()
                    expenseViewModel.clearData()
                    goalViewModel.clearData()
                    savingsViewModel.clearData()

                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.AiAssistant.route) {
            val incomes = incomeViewModel.incomes.value
            val expenses = expenseViewModel.expenses.value
            val goals = goalViewModel.goals.value
            val savings = savingsViewModel.savings.value

            AiAssistantScreen(
                viewModel = aiAssistantViewModel,
                totalIncome = incomes.sumOf { it.amount },
                totalExpenses = expenses.sumOf { it.amount },
                totalSavings = savings.sumOf { it.amount },
                goals = goals
            )
        }
    }
}
