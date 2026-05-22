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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
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
import com.lumixa.app.presentation.components.LumixaColors
import com.lumixa.app.presentation.components.LumixaEmptyStateCard
import com.lumixa.app.presentation.viewmodel.ExpenseViewModel
import com.lumixa.app.presentation.viewmodel.GoalViewModel
import com.lumixa.app.presentation.viewmodel.IncomeViewModel
import com.lumixa.app.presentation.viewmodel.ExpensePredictionViewModel
import com.lumixa.app.presentation.viewmodel.SavingsViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun StatisticsScreen(
    expenseViewModel: ExpenseViewModel,
    goalViewModel: GoalViewModel,
    savingsViewModel: SavingsViewModel,
    incomeViewModel: IncomeViewModel,
    expensePredictionViewModel: ExpensePredictionViewModel
) {
    val expenses by expenseViewModel.expenses.collectAsState()
    val goals by goalViewModel.goals.collectAsState()
    val savings by savingsViewModel.savings.collectAsState()
    val incomes by incomeViewModel.incomes.collectAsState()
    val predictionState by expensePredictionViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val currencyPrefs = remember(context) { CurrencyPreferences(context) }
    val currencySymbol = remember { currencyPrefs.getCurrencySymbol() }

    val totalSavings = savings.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LumixaColors.Surface),
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
            if (expenses.isEmpty()) {
                LumixaEmptyStateCard("Sin movimientos", "Cuando registres gastos podrás ver tendencias y distribución.")
                Spacer(modifier = Modifier.height(16.dp))
            }

            ExpenseTrendCard(expenses = expenses, currencySymbol = currencySymbol)

            Spacer(modifier = Modifier.height(16.dp))

            CategoryDistributionCard(expenses = expenses, currencySymbol = currencySymbol)

            Spacer(modifier = Modifier.height(16.dp))

            SmartAnalysisCard(expenses = expenses, currencySymbol = currencySymbol)

            Spacer(modifier = Modifier.height(16.dp))

            ExpensePredictionCard(
                state = predictionState,
                onRefresh = {
                    expensePredictionViewModel.loadPrediction(
                        expenses = expenses,
                        incomes = incomes,
                        savings = savings,
                        goals = goals,
                        forceRefresh = true
                    )
                }
            )

            LaunchedEffect(expenses, incomes, savings, goals) {
                expensePredictionViewModel.loadPrediction(
                    expenses = expenses,
                    incomes = incomes,
                    savings = savings,
                    goals = goals
                )
            }

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
    var selectedPeriod by remember { mutableStateOf(StatisticsPeriod.WEEK) }
    var periodOffset by remember { mutableIntStateOf(0) }

    val periodData = remember(expenses, selectedPeriod, periodOffset) {
        buildExpensePeriodData(expenses = expenses, period = selectedPeriod, offset = periodOffset)
    }
    val total = periodData.values.sum()
    val maxValue = (periodData.values.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
    val primaryColor = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("TENDENCIA DE GASTOS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    TextButton(onClick = {
                        selectedPeriod = StatisticsPeriod.WEEK
                        periodOffset = 0
                    }) {
                        Text("Semana", color = if (selectedPeriod == StatisticsPeriod.WEEK) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = {
                        selectedPeriod = StatisticsPeriod.MONTH
                        periodOffset = 0
                    }) {
                        Text("Mes", color = if (selectedPeriod == StatisticsPeriod.MONTH) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { periodOffset-- }) {
                    Text("‹", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = periodData.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { periodOffset++ }) {
                    Text("›", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(formatCurrency(total, currencySymbol), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(14.dp))

            Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                val chartHeight = size.height - 28.dp.toPx()
                val stepX = if (periodData.values.size > 1) size.width / (periodData.values.size - 1) else size.width
                val points = periodData.values.mapIndexed { index, value ->
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
                periodData.labels.forEach { label -> Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

private enum class StatisticsPeriod { WEEK, MONTH }

private data class PeriodChartData(
    val labels: List<String>,
    val values: List<Double>,
    val title: String
)

private fun buildExpensePeriodData(expenses: List<ExpenseEntity>, period: StatisticsPeriod, offset: Int): PeriodChartData {
    val locale = Locale("es", "ES")
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", locale)
    val today = LocalDate.now()
    val parsed = expenses.mapNotNull { expense ->
        parseExpenseDate(expense.date, formatter)?.let { it to expense.amount }
    }

    return when (period) {
        StatisticsPeriod.WEEK -> {
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(offset.toLong())
            val endOfWeek = startOfWeek.plusDays(6)
            val labels = listOf("L", "M", "X", "J", "V", "S", "D")
            val values = (0..6).map { index ->
                val targetDate = startOfWeek.plusDays(index.toLong())
                parsed.filter { (date, _) -> date == targetDate }.sumOf { it.second }
            }
            PeriodChartData(
                labels = labels,
                values = values,
                title = "${startOfWeek.format(DateTimeFormatter.ofPattern("dd MMM", locale))} - ${endOfWeek.format(DateTimeFormatter.ofPattern("dd MMM yyyy", locale))}"
            )
        }

        StatisticsPeriod.MONTH -> {
            val targetMonth = YearMonth.from(today).plusMonths(offset.toLong())
            val monthStart = targetMonth.atDay(1)
            val monthEnd = targetMonth.atEndOfMonth()
            val labels = listOf("S1", "S2", "S3", "S4", "S5")
            val values = MutableList(5) { 0.0 }

            parsed.forEach { (date, amount) ->
                if (date in monthStart..monthEnd) {
                    val weekIndex = ((date.dayOfMonth - 1) / 7).coerceIn(0, 4)
                    values[weekIndex] += amount
                }
            }

            PeriodChartData(
                labels = labels,
                values = values,
                title = targetMonth.month.getDisplayName(java.time.format.TextStyle.FULL, locale).replaceFirstChar { it.uppercaseChar() } + " ${targetMonth.year}"
            )
        }
    }
}

private fun parseExpenseDate(date: String, formatter: DateTimeFormatter): LocalDate? {
    return try {
        LocalDate.parse(date.lowercase(Locale("es", "ES")), formatter)
    } catch (_: DateTimeParseException) {
        null
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


@Composable
fun ExpensePredictionCard(
    state: com.lumixa.app.presentation.viewmodel.ExpensePredictionUiState,
    onRefresh: () -> Unit
) {
    val riskColor = when (state.riskLevel.lowercase()) {
        "alto" -> Color(0xFFD64B4B)
        "bajo" -> Color(0xFF1FBF9F)
        else -> Color(0xFF2D6CDF)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F6F8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "PREDICCIÓN DE GASTOS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2A44)
                )
                Text(
                    text = "Riesgo: ${state.riskLevel}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = riskColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                if (state.isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircularProgressIndicator(color = Color(0xFF2D6CDF), strokeWidth = 3.dp, modifier = Modifier.size(20.dp))
                        Text("Analizando tus hábitos de gasto...", color = Color(0xFF2B2B2B))
                    }
                } else {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(state.prediction, color = Color(0xFF2B2B2B), style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB)))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.recommendation, color = Color(0xFF0F2A44), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Actualizar predicción",
                color = Color(0xFF2D6CDF),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onRefresh() }
            )
        }
    }
}
