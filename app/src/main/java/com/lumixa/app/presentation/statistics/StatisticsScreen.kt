package com.lumixa.app.presentation.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lumixa.app.presentation.viewmodel.ExpenseViewModel
import com.lumixa.app.presentation.viewmodel.GoalViewModel
@Composable

fun StatisticsScreen(
    expenseViewModel: ExpenseViewModel,
    goalViewModel: GoalViewModel
) {
    val expenses by expenseViewModel.expenses.collectAsState()
    val goals by goalViewModel.goals.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8)),
        contentPadding = PaddingValues(20.dp)
    ) {
        item {
            Text(
                text = "Estadísticas",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )

            Spacer(modifier = Modifier.height(16.dp))

            GoalProgressStatsCard(
                goals = goals,
                totalExpenses = expenses.sumOf { it.amount }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExpenseChartCard(
                expenses = expenses
            )

            Spacer(modifier = Modifier.height(16.dp))

            CategoryDistributionCard(
                expenses = expenses
            )

            Spacer(modifier = Modifier.height(16.dp))

            SmartAnalysisCard(
                expenses = expenses
            )

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun GoalProgressStatsCard(
    goals: List<com.lumixa.app.data.local.entity.GoalEntity>,
    totalExpenses: Double
) {

    val goal = goals.firstOrNull()

    val smartSavedAmount =
        (totalExpenses * 0.25).coerceAtLeast(0.0)

    val progress =
        if (goal != null && goal.targetAmount > 0) {
            (smartSavedAmount / goal.targetAmount)
                .coerceIn(0.0, 1.0)
                .toFloat()
        } else {
            0f
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Text(
                text = if (goal != null) {
                    "META: ${goal.name.uppercase()}"
                } else {
                    "SIN META ACTIVA"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {

                CircularGoalProgress(progress = progress)

                Spacer(modifier = Modifier.width(20.dp))

                Column {

                    Text(
                        "Ahorrado",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )

                    Text(
                        text = "$${smartSavedAmount.toInt()}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1FBF9F)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Meta",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )

                    Text(
                        text = if (goal != null) {
                            "$${goal.targetAmount.toInt()}"
                        } else {
                            "$0"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2A44)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF1FBF9F),
                trackColor = Color(0xFFE5E7EB)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    "Avance actual",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )

                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1FBF9F)
                )
            }
        }
    }
}

@Composable
fun CircularGoalProgress(progress: Float) {
    Box(
        modifier = Modifier.size(96.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(96.dp)) {
            drawArc(
                color = Color(0xFFE5E7EB),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                color = Color(0xFF1FBF9F),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2A44)
        )
    }
}

@Composable
fun ExpenseChartCard(
    expenses: List<com.lumixa.app.data.local.entity.ExpenseEntity>
) {
    var chartMode by remember { mutableStateOf("Semana") }
    var periodIndex by remember { mutableStateOf(0) }

    val weeklyPeriods = listOf(
        "3 - 9 mayo",
        "10 - 16 mayo",
        "17 - 23 mayo"
    )

    val monthlyPeriods = listOf(
        "Abril 2026",
        "Mayo 2026",
        "Junio 2026"
    )

    val totalExpenses = expenses.sumOf { it.amount }.toInt()

    val weeklyValues = listOf(
        List(7) { dayIndex ->
            expenses
                .filter { it.dayOfWeek == dayIndex }
                .sumOf { it.amount }
                .toInt()
        },
        List(7) { dayIndex ->
            expenses
                .filter { it.dayOfWeek == dayIndex }
                .sumOf { it.amount }
                .toInt()
        },
        List(7) { dayIndex ->
            expenses
                .filter { it.dayOfWeek == dayIndex }
                .sumOf { it.amount }
                .toInt()
        }
    )

    val monthlyValues = listOf(
        listOf(
            totalExpenses / 4,
            totalExpenses / 3,
            totalExpenses / 2,
            totalExpenses / 2
        ),

        listOf(
            totalExpenses / 5,
            totalExpenses / 4,
            totalExpenses / 3,
            totalExpenses / 2
        ),

        listOf(
            totalExpenses / 3,
            totalExpenses / 2,
            totalExpenses / 4,
            totalExpenses / 5
        )
    )

    val currentPeriod = if (chartMode == "Semana") {
        weeklyPeriods[periodIndex]
    } else {
        monthlyPeriods[periodIndex]
    }

    val rawValues = if (chartMode == "Semana") {
        weeklyValues[periodIndex]
    } else {
        monthlyValues[periodIndex]
    }

    val maxValue = rawValues.maxOrNull() ?: 1

    val values = rawValues.map { value ->
        if (maxValue > 0) {
            ((value.toFloat() / maxValue.toFloat()) * 95).toInt().coerceAtLeast(8)
        } else {
            8
        }
    }

    val labels = if (chartMode == "Semana") {
        listOf("D", "L", "M", "M", "J", "V", "S")
    } else {
        listOf("S1", "S2", "S3", "S4")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (chartMode == "Semana") "GASTO SEMANAL" else "GASTO MENSUAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280)
                )

                Row {
                    ModeChip(
                        text = "Semana",
                        selected = chartMode == "Semana",
                        onClick = {
                            chartMode = "Semana"
                            periodIndex = 1
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    ModeChip(
                        text = "Mes",
                        selected = chartMode == "Mes",
                        onClick = {
                            chartMode = "Mes"
                            periodIndex = 1
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PeriodButton(
                    text = "‹",
                    onClick = {
                        if (periodIndex > 0) periodIndex--
                    }
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentPeriod,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2A44)
                    )

                    Text(
                        text = if (chartMode == "Semana") "Esta semana" else "Vista mensual",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                PeriodButton(
                    text = "›",
                    onClick = {
                        if (periodIndex < 2) periodIndex++
                    }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { index, value ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .width(if (chartMode == "Semana") 18.dp else 32.dp)
                                .height(value.dp)
                                .background(
                                    color = Color(0xFF2D6CDF),
                                    shape = RoundedCornerShape(10.dp)
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = labels[index],
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(32.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(99.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF2D6CDF) else Color(0xFFEAF1FF)
        )
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else Color(0xFF2D6CDF)
            )
        }
    }
}

@Composable
fun PeriodButton(
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(34.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(99.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF1FF))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D6CDF)
            )
        }
    }
}

@Composable

fun CategoryDistributionCard(
    expenses: List<com.lumixa.app.data.local.entity.ExpenseEntity>
) {

    val total = expenses.sumOf { it.amount }

    val grouped = expenses.groupBy { it.category }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {

        Column(modifier = Modifier.padding(18.dp)) {

            Text(
                text = "DISTRIBUCIÓN POR CATEGORÍA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(16.dp))

            grouped.forEach { (category, items) ->

                val categoryTotal = items.sumOf { it.amount }

                val progress =
                    if (total > 0) {
                        (categoryTotal / total).toFloat()
                    } else {
                        0f
                    }

                CategoryDistributionRow(
                    category = category,
                    amount = "$${categoryTotal.toInt()}",
                    progress = progress
                )
            }
        }
    }
}

@Composable
fun CategoryDistributionRow(
    category: String,
    amount: String,
    progress: Float
) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )

            Text(
                text = "${(progress * 100).toInt()}% · $amount",
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp),
            color = Color(0xFF2D6CDF),
            trackColor = Color(0xFFE5E7EB)
        )
    }
}

@Composable
fun SmartAnalysisCard(
    expenses: List<com.lumixa.app.data.local.entity.ExpenseEntity>
) {
    val total = expenses.sumOf { it.amount }

    val topCategory = expenses
        .groupBy { it.category }
        .maxByOrNull { entry ->
            entry.value.sumOf { it.amount }
        }

    val topCategoryName = topCategory?.key ?: "Sin datos"

    val topCategoryAmount = topCategory?.value?.sumOf { it.amount } ?: 0.0

    val topCategoryPercent =
        if (total > 0) {
            ((topCategoryAmount / total) * 100).toInt()
        } else {
            0
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF1FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "ANÁLISIS INTELIGENTE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(14.dp))

            AnalysisMessage(
                text = if (expenses.isEmpty()) {
                    "Aún no tienes gastos suficientes para generar un análisis."
                } else {
                    "Gastas más en $topCategoryName. Representa el $topCategoryPercent% de tus gastos actuales."
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            AnalysisMessage(
                text = if (total == 0.0) {
                    "Registra tus primeros gastos para que LUMIXA pueda darte recomendaciones."
                } else if (topCategoryPercent >= 60) {
                    "Tu gasto está muy concentrado en $topCategoryName. Intenta definir un límite para esta categoría."
                } else {
                    "Tus gastos están relativamente equilibrados entre varias categorías."
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            AnalysisMessage(
                text = if (total > 0) {
                    "Total analizado: $${total.toInt()}. Sigue registrando tus movimientos para mejorar las recomendaciones."
                } else {
                    "Cuando registres más movimientos, el análisis será más preciso."
                }
            )
        }
    }
}

@Composable
fun AnalysisMessage(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(14.dp),
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = Color(0xFF0F2A44)
        )
    }
}