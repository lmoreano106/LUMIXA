package com.lumixa.app.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lumixa.app.presentation.viewmodel.ExpenseViewModel

@Composable
fun DashboardScreen(
    expenseViewModel: ExpenseViewModel,
    modifier: Modifier = Modifier,
    onAddExpenseClick: () -> Unit = {},
    onAddIncomeClick: () -> Unit = {},
    onGoalClick: () -> Unit = {}
){

    val expenses by expenseViewModel.expenses.collectAsState()

    val monthlyIncome = 1130000.0
    val dailyBudget = monthlyIncome / 30
    val spentToday = expenses.sumOf { it.amount }
    val availableToday = dailyBudget - spentToday
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8)),
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 24.dp,
            bottom = 100.dp
        )
    ) {
        item {
        Text(
            text = "Hola, bienvenido",
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )

        Text(
            text = "LUMIXA",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2A44)
        )
            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                Button(
                    onClick = onAddIncomeClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
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
                    shape = RoundedCornerShape(14.dp),
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
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF0F2A44),
                                Color(0xFF2D6CDF)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Text(
                    text = "Saldo actual",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$1.130.000",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Hoy puedes gastar",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Text(
                    text =
                        if (availableToday >= 0) {
                            "$${availableToday.toInt()}"
                        } else {
                            "Excedido $${kotlin.math.abs(availableToday.toInt())}"
                        },

                    fontSize = 26.sp,

                    fontWeight = FontWeight.Bold,

                    color =
                        if (availableToday >= 0) {
                            Color(0xFF1FBF9F)
                        } else {
                            Color(0xFFE11D48)
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            SummaryCard(
                title = "Ingresos",
                amount = "$1.130.000",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            SummaryCard(
                title = "Gastos",
                amount = "$${spentToday.toInt()}",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Control diario inteligente",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2A44)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (spentToday == 0.0) {
                        "Estás en control. Aún no registras gastos hoy."
                    } else {
                        "Hoy has gastado $${spentToday.toInt()} de tu presupuesto diario."
                    },
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
            Spacer(modifier = Modifier.height(20.dp))

            GoalCard(
                onClick = onGoalClick
            )


    }
}
}


@Composable
fun SummaryCard(
    title: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = amount,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )
        }
    }
}