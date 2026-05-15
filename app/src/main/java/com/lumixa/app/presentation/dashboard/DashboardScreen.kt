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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.presentation.viewmodel.ExpenseViewModel
import com.lumixa.app.presentation.viewmodel.IncomeViewModel
import kotlin.math.abs

@Composable
fun DashboardScreen(
    expenseViewModel: ExpenseViewModel,
    incomeViewModel: IncomeViewModel,
    modifier: Modifier = Modifier,
    onAddExpenseClick: () -> Unit = {},
    onAddIncomeClick: () -> Unit = {},
    onGoalClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val expenses by expenseViewModel.expenses.collectAsState()
    val incomes by incomeViewModel.incomes.collectAsState()

    val monthlyIncome: Double = incomes.sumOf { it.amount }
    val dailyBudget = monthlyIncome / 30
    val spentToday = expenses.sumOf { it.amount }
    val availableToday = dailyBudget - spentToday
    val savingsToday = availableToday.coerceAtLeast(0.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8)),
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
                        color = Color(0xFF6B7280)
                    )

                    Text(
                        text = "Hola, Usuario 👋",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2A44)
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
                        color = Color(0xFF0F2A44)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            BalanceCard(
                monthlyIncome = monthlyIncome,
                spentToday = spentToday,
                onAddIncomeClick = onAddIncomeClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            DailyControlCard(
                dailyBudget = dailyBudget,
                spentToday = spentToday,
                availableToday = availableToday,
                savingsToday = savingsToday
            )

            Spacer(modifier = Modifier.height(14.dp))

            SmartDailyMessage(
                spentToday = spentToday,
                availableToday = availableToday,
                savingsToday = savingsToday
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

            Spacer(modifier = Modifier.height(18.dp))

            GoalCard(
                onClick = onGoalClick
            )
        }
    }
}

@Composable
fun BalanceCard(
    monthlyIncome: Double,
    spentToday: Double,
    onAddIncomeClick: () -> Unit
) {
    val currentBalance = monthlyIncome - spentToday

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
                                color = Color(0xFFEAF1FF),
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
                        color = Color(0xFF6B7280)
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

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "$${currentBalance.toInt()}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "↑ Ingresos: $${monthlyIncome.toInt()}",
                    fontSize = 12.sp,
                    color = Color(0xFF1FBF9F),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "↓ Gastos: $${spentToday.toInt()}",
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
    savingsToday: Double
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
                            Color(0xFF0F2A44)
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

            Spacer(modifier = Modifier.height(18.dp))

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
                    "$${availableToday.toInt()}"
                } else {
                    "$${abs(availableToday.toInt())}"
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
                    amount = "$${dailyBudget.toInt()}"
                )

                DailyMiniStat(
                    title = "GASTADO",
                    amount = "$${spentToday.toInt()}"
                )

                DailyMiniStat(
                    title = "AHORRO",
                    amount = if (availableToday >= 0) {
                        "+$${savingsToday.toInt()}"
                    } else {
                        "$0"
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
    savingsToday: Double
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
                "👍 Empieza el día sin gastos. Tu ahorro se acumula."
            } else if (availableToday >= 0) {
                "💪 Buen trabajo. Hoy todavía puedes ahorrar $${savingsToday.toInt()}."
            } else {
                "⚠️ Te excediste hoy. Revisa tus gastos para recuperar el control."
            },
            modifier = Modifier.padding(16.dp),
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = if (availableToday >= 0) {
                Color(0xFF0F2A44)
            } else {
                Color(0xFFE11D48)
            },
            fontWeight = FontWeight.Medium
        )
    }
}