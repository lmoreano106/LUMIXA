package com.lumixa.app.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.lumixa.app.data.preferences.CurrencyPreferences
import com.lumixa.app.presentation.components.LumixaColors
import com.lumixa.app.presentation.viewmodel.ExpenseViewModel
import com.lumixa.app.presentation.viewmodel.GoalViewModel
import com.lumixa.app.presentation.viewmodel.IncomeViewModel
import com.lumixa.app.presentation.viewmodel.SavingsViewModel
import com.lumixa.app.presentation.viewmodel.DashboardAiInsightViewModel
import com.lumixa.app.utils.FinancialCalculator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun DashboardScreen(
    expenseViewModel: ExpenseViewModel,
    incomeViewModel: IncomeViewModel,
    goalViewModel: GoalViewModel,
    savingsViewModel: SavingsViewModel,
    dashboardAiInsightViewModel: DashboardAiInsightViewModel,
    modifier: Modifier = Modifier,
    onAddExpenseClick: () -> Unit = {},
    onAddIncomeClick: () -> Unit = {},
    onGoalClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onAiAssistantClick: () -> Unit = {}

) {
    val expenses by expenseViewModel.expenses.collectAsState()
    val incomes by incomeViewModel.incomes.collectAsState()
    val goals by goalViewModel.goals.collectAsState()
    val savings by savingsViewModel.savings.collectAsState()
    val aiInsightUiState by dashboardAiInsightViewModel.uiState.collectAsState()

    val context = LocalContext.current

    val currencyPreferences = remember {
        CurrencyPreferences(context)
    }

    val currencySymbol =
        currencyPreferences.getCurrencySymbol()

    val todayDate =
        SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES")).format(Date())

    val totalIncome = incomes.sumOf { it.amount }

    val todayExpenses =
        expenses
            .filter { it.date == todayDate }
            .sumOf { it.amount }

    val totalExpenses = expenses.sumOf { it.amount }

    val totalSavings = savings.sumOf { it.amount }

    val monthlyIncome =
        FinancialCalculator.calculateMonthlyIncome(totalIncome)

    val dailyBudget =
        FinancialCalculator.calculateDailyBudget(monthlyIncome)

    val spentToday =
        FinancialCalculator.calculateSpent(todayExpenses)

    val availableToday =
        FinancialCalculator.calculateAvailableToday(
            dailyBudget,
            spentToday
        )

    val savingsToday =
        FinancialCalculator.calculateSavings(availableToday)

    LaunchedEffect(
        todayDate,
        savingsToday,
        totalIncome,
        spentToday
    ) {
        savingsViewModel.saveDailySaving(
            amount = savingsToday,
            date = todayDate
        )
    }

    LaunchedEffect(totalIncome, totalExpenses, totalSavings, goals) {
        dashboardAiInsightViewModel.loadInsight(
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            totalSavings = totalSavings,
            goals = goals
        )
    }

    val userName =
        FirebaseAuth.getInstance().currentUser?.displayName
            ?: FirebaseAuth.getInstance().currentUser?.email?.substringBefore("@")
            ?: "Usuario"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LumixaColors.Surface),
        contentPadding = PaddingValues(
            start = 22.dp,
            end = 22.dp,
            top = 24.dp,
            bottom = 105.dp
        )
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Bienvenido de vuelta",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Hola, $userName 👋",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Card(
                    modifier = Modifier.clickable {
                        onLogoutClick()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Salir",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            BalanceCard(
                monthlyIncome = monthlyIncome,
                totalExpenses = totalExpenses,
                currencySymbol = currencySymbol,
                onAddIncomeClick = onAddIncomeClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            DailyControlCard(
                dailyBudget = dailyBudget,
                spentToday = spentToday,
                availableToday = availableToday,
                savingsToday = savingsToday,
                currencySymbol = currencySymbol
            )

            Spacer(modifier = Modifier.height(14.dp))

            SmartDailyMessage(
                spentToday = spentToday,
                availableToday = availableToday,
                savingsToday = savingsToday,
                totalSavings = totalSavings,
                currencySymbol = currencySymbol
            )

            Spacer(modifier = Modifier.height(16.dp))

            DashboardAiInsightCard(
                insight = aiInsightUiState.insight,
                isLoading = aiInsightUiState.isLoading,
                onRefreshClick = {
                    dashboardAiInsightViewModel.loadInsight(
                        totalIncome = totalIncome,
                        totalExpenses = totalExpenses,
                        totalSavings = totalSavings,
                        goals = goals,
                        forceRefresh = true
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onAddIncomeClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1FBF9F)
                    )
                ) {
                    Text(
                        text = "+ Ingreso",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onAddExpenseClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D6CDF)
                    )
                ) {
                    Text(
                        text = "+ Gasto",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }


            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAiAssistantClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(
                    text = "Asistente IA",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            GoalCard(
                goals = goals,
                savingsToday = totalSavings,
                onClick = onGoalClick
            )
        }
    }
}

@Composable
private fun DashboardAiInsightCard(
    insight: String,
    isLoading: Boolean,
    onRefreshClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "💡 Consejo inteligente",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF2D6CDF),
                    trackColor = MaterialTheme.colorScheme.background
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = insight,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onRefreshClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Actualizar consejo",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun BalanceCard(
    monthlyIncome: Double,
    totalExpenses: Double,
    currencySymbol: String,
    onAddIncomeClick: () -> Unit
) {
    val currentBalance = monthlyIncome - totalExpenses

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💳",
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "SALDO ACTUAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Card(
                    modifier = Modifier.clickable {
                        onAddIncomeClick()
                    },
                    shape = RoundedCornerShape(99.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE7F8F3)
                    )
                ) {
                    Text(
                        text = "+ Ingreso",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1FBF9F)
                    )
                }
            }


            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "${currencySymbol}${currentBalance.toInt()}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "↑ Ingresos: ${currencySymbol}${monthlyIncome.toInt()}",
                    fontSize = 12.sp,
                    color = Color(0xFF1FBF9F),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "↓ Gastos: ${currencySymbol}${totalExpenses.toInt()}",
                    fontSize = 12.sp,
                    color = Color(0xFFE11D48),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DailyControlCard(
    dailyBudget: Double,
    spentToday: Double,
    availableToday: Double,
    savingsToday: Double,
    currencySymbol: String
) {
    val progress = if (dailyBudget > 0) {
        (spentToday / dailyBudget).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF2D6CDF),
                            MaterialTheme.colorScheme.onSurface
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CONTROL DIARIO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Card(
                    shape = RoundedCornerShape(99.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (availableToday >= 0) {
                            Color(0xFF1FBF9F)
                        } else {
                            Color(0xFFE11D48)
                        }
                    )
                ) {
                    Text(
                        text = if (availableToday >= 0) "En control" else "Excedido",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }


            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (availableToday >= 0) {
                    "Hoy puedes gastar"
                } else {
                    "Te excediste"
                },
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.85f)
            )

            Text(
                text = if (availableToday >= 0) {
                    "${currencySymbol}${availableToday.toInt()}"
                } else {
                    "${currencySymbol}${abs(availableToday.toInt())}"
                },
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (availableToday >= 0) {
                    Color(0xFF1FBF9F)
                } else {
                    Color(0xFFE11D48)
                },
                trackColor = Color.White.copy(alpha = 0.25f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DailyMiniStat(
                    title = "PRESUPUESTO",
                    amount = "${currencySymbol}${dailyBudget.toInt()}"
                )

                DailyMiniStat(
                    title = "GASTADO",
                    amount = "${currencySymbol}${spentToday.toInt()}"
                )

                DailyMiniStat(
                    title = "AHORRO HOY",
                    amount = if (availableToday >= 0) {
                        "+${currencySymbol}${savingsToday.toInt()}"
                    } else {
                        "${currencySymbol}0"
                    }
                )
            }
        }
    }
}

@Composable
fun DailyMiniStat(
    title: String,
    amount: String
) {
    Column {
        Text(
            text = title,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = amount,
            fontSize = 13.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SmartDailyMessage(
    spentToday: Double,
    availableToday: Double,
    savingsToday: Double,
    totalSavings: Double,
    currencySymbol: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (availableToday >= 0) {
                Color(0xFFE7F8F3)
            } else {
                Color(0xFFFFE4E6)
            }
        )
    ) {
        Text(
            text = if (spentToday == 0.0) {
                "👍 Empieza el día sin gastos. Tu ahorro acumulado es ${currencySymbol}${totalSavings.toInt()}."
            } else if (availableToday >= 0) {
                "💪 Buen trabajo. Hoy ahorrarías ${currencySymbol}${savingsToday.toInt()}. Ahorro acumulado: ${currencySymbol}${totalSavings.toInt()}."
            } else {
                "⚠️ Te excediste hoy. No se agregará ahorro automático a tu meta."
            },
            modifier = Modifier.padding(16.dp),
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = if (availableToday >= 0) {
                MaterialTheme.colorScheme.onSurface
            } else {
                Color(0xFFE11D48)
            },
            fontWeight = FontWeight.Medium
        )
    }
}
