package com.lumixa.app.presentation.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.data.local.entity.ExpenseEntity
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.data.preferences.CurrencyPreferences
import com.lumixa.app.presentation.viewmodel.ExpenseViewModel
import com.lumixa.app.presentation.viewmodel.GoalViewModel
import com.lumixa.app.presentation.viewmodel.SavingsViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.roundToInt

@Composable
fun StatisticsScreen(
    expenseViewModel: ExpenseViewModel,
    goalViewModel: GoalViewModel,
    savingsViewModel: SavingsViewModel
) {
    val expenses by expenseViewModel.expenses.collectAsState()
    val goals by goalViewModel.goals.collectAsState()
    val savings by savingsViewModel.savings.collectAsState()

    val context = LocalContext.current
    val currencyPrefs = remember(context) { CurrencyPreferences(context) }
    val currencySymbol = remember { currencyPrefs.getCurrencySymbol() }

    val totalSavings = savings.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        contentPadding = PaddingValues(20.dp)
    ) {
        item {
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            GoalProgressStatsCard(goals, totalSavings, currencySymbol)

            Spacer(modifier = Modifier.height(16.dp))

            ExpenseTrendCard(expenses = expenses, currencySymbol = currencySymbol)

            Spacer(modifier = Modifier.height(16.dp))

            CategoryDistributionCard(expenses = expenses, currencySymbol = currencySymbol)

            Spacer(modifier = Modifier.height(16.dp))

            SmartAnalysisCard(expenses = expenses, currencySymbol = currencySymbol)

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun GoalProgressStatsCard(goals: List<GoalEntity>, totalSavings: Double, currencySymbol: String) {
    val goal = goals.firstOrNull()
    val progress = if (goal != null && goal.targetAmount > 0) {
        (totalSavings / goal.targetAmount).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = goal?.let { "META: ${it.name.uppercase()}" } ?: "SIN META ACTIVA",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularGoalProgress(progress = progress)
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text("Ahorrado", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatCurrency(totalSavings, currencySymbol), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1FBF9F))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Meta", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatCurrency(goal?.targetAmount ?: 0.0, currencySymbol), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp), color = Color(0xFF1FBF9F), trackColor = Color(0xFFE5E7EB))
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Avance actual", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF1FBF9F))
            }
        }
    }
}

@Composable
fun CircularGoalProgress(progress: Float) {
    Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(96.dp)) {
            drawArc(Color(0xFFE5E7EB), -90f, 360f, false, style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round))
            drawArc(Color(0xFF1FBF9F), -90f, 360f * progress, false, style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round))
        }
        Text("${(progress * 100).toInt()}%", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ExpenseTrendCard(expenses: List<ExpenseEntity>, currencySymbol: String) {
    val last7Days = List(7) { day -> expenses.filter { it.dayOfWeek == day }.sumOf { it.amount } }
    val labels = listOf("D", "L", "M", "M", "J", "V", "S")
    val total = last7Days.sum()
    val maxValue = (last7Days.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
    val primaryColor = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("TENDENCIA SEMANAL", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(formatCurrency(total, currencySymbol), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(14.dp))

            Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                val chartHeight = size.height - 28.dp.toPx()
                val stepX = if (last7Days.size > 1) size.width / (last7Days.size - 1) else size.width
                val points = last7Days.mapIndexed { index, value ->
                    Offset(index * stepX, chartHeight - ((value / maxValue) * chartHeight).toFloat())
                }

                val path = Path().apply {
                    points.forEachIndexed { i, point -> if (i == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y) }
                }
                drawPath(path = path, color = primaryColor, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))

                points.forEach { point ->
                    drawCircle(color = primaryColor, radius = 5.dp.toPx(), center = point)
                    drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = point)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                labels.forEach { label -> Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
fun CategoryDistributionCard(expenses: List<ExpenseEntity>, currencySymbol: String) {
    val total = expenses.sumOf { it.amount }
    val grouped = expenses.groupBy { it.category }.mapValues { (_, list) -> list.sumOf { it.amount } }.toList().sortedByDescending { it.second }
    val colors = listOf(Color(0xFF2D6CDF), Color(0xFF1FBF9F), Color(0xFFF59E0B), Color(0xFF8B5CF6), Color(0xFFEF4444))
    val surfaceColor = MaterialTheme.colorScheme.surface
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("DISTRIBUCIÓN POR CATEGORÍA", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                if (total > 0 && grouped.isNotEmpty()) {
                    var startAngle = -90f
                    grouped.forEachIndexed { index, (_, amount) ->
                        val sweep = ((amount / total) * 360).toFloat()
                        drawArc(color = colors[index % colors.size], startAngle = startAngle, sweepAngle = sweep, useCenter = true, topLeft = Offset(size.width / 2 - 65.dp.toPx(), 10.dp.toPx()), size = Size(130.dp.toPx(), 130.dp.toPx()))
                        startAngle += sweep
                    }
                    drawCircle(color = surfaceColor, radius = 38.dp.toPx(), center = Offset(size.width / 2, 75.dp.toPx()))
                }
            }

            grouped.forEachIndexed { index, (category, amount) ->
                val progress = if (total > 0) (amount / total).toFloat() else 0f
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(colors[index % colors.size], CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(category, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text("${(progress * 100).roundToInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(formatCurrency(amount, currencySymbol), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(7.dp), color = colors[index % colors.size], trackColor = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun SmartAnalysisCard(expenses: List<ExpenseEntity>, currencySymbol: String) {
    val total = expenses.sumOf { it.amount }
    val topCategory = expenses.groupBy { it.category }.maxByOrNull { it.value.sumOf { e -> e.amount } }
    val topCategoryName = topCategory?.key ?: "Sin datos"
    val topCategoryAmount = topCategory?.value?.sumOf { it.amount } ?: 0.0
    val topCategoryPercent = if (total > 0) ((topCategoryAmount / total) * 100).toInt() else 0

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF1FF)), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("ANÁLISIS INTELIGENTE", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(14.dp))

            AnalysisMessage(if (expenses.isEmpty()) "Aún no tienes gastos suficientes para generar un análisis." else "Gastas más en $topCategoryName. Representa el $topCategoryPercent% de tus gastos actuales.")
            Spacer(modifier = Modifier.height(10.dp))
            AnalysisMessage(if (total == 0.0) "Registra tus primeros gastos para que LUMIXA pueda darte recomendaciones." else if (topCategoryPercent >= 60) "Tu gasto está muy concentrado en $topCategoryName. Intenta definir un límite para esta categoría." else "Tus gastos están relativamente equilibrados entre varias categorías.")
            Spacer(modifier = Modifier.height(10.dp))
            AnalysisMessage(if (total > 0) "Total analizado: ${formatCurrency(total, currencySymbol)}. Sigue registrando tus movimientos para mejorar las recomendaciones." else "Cuando registres más movimientos, el análisis será más preciso.")
        }
    }
}

@Composable
fun AnalysisMessage(text: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Text(text = text, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Start)
    }
}

private fun formatCurrency(amount: Double, symbol: String): String {
    val symbols = DecimalFormatSymbols().apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    val formatter = DecimalFormat("#,##0.##", symbols)
    return "$symbol${formatter.format(amount)}"
}
