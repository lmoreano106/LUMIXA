package com.lumixa.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lumixa.app.presentation.onboarding.OnboardingScreen

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

        }

        composable(Routes.Register.route) {

        }

        composable(Routes.Currency.route) {

        }

        composable(Routes.Income.route) {

        }

        composable(Routes.Dashboard.route) {

        }

        composable(Routes.Expenses.route) {

        }

        composable(Routes.Statistics.route) {

        }

        composable(Routes.Goal.route) {

        }

        composable(Routes.Admin.route) {

        }
    }
}