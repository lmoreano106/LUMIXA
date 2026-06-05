package com.lumixa.app.presentation.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.presentation.viewmodel.GoalViewModel
import com.lumixa.app.presentation.viewmodel.GoalAiAnalysisViewModel
import com.lumixa.app.presentation.viewmodel.SavingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import androidx.compose.foundation.clickable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.lumixa.app.data.preferences.CurrencyPreferences
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.lumixa.app.presentation.components.LumixaColors
@Composable
fun GoalDetailScreen(
    goalViewModel: GoalViewModel,
    savingsViewModel: SavingsViewModel,
    incomeViewModel: com.lumixa.app.presentation.viewmodel.IncomeViewModel,
    expenseViewModel: com.lumixa.app.presentation.viewmodel.ExpenseViewModel,
    goalAiAnalysisViewModel: GoalAiAnalysisViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
)  {
    val goals by goalViewModel.goals.collectAsState()
    val savings by savingsViewModel.savings.collectAsState()
    val goalMovements by goalViewModel.goalMovements.collectAsState()
    val withdrawalError by goalViewModel.goalWithdrawalError.collectAsState()
    val incomes by incomeViewModel.incomes.collectAsState()
    val expenses by expenseViewModel.expenses.collectAsState()
    val aiUiState by goalAiAnalysisViewModel.uiState.collectAsState()

    val goal = goals.firstOrNull()
    val goalMovementsForGoal = goal?.let { selectedGoal ->
        goalMovements.filter { it.goalId == selectedGoal.id }
    } ?: emptyList()
    val totalGoalWithdrawals = goalMovementsForGoal.sumOf { it.amount }
    val totalSavings = (savings.sumOf { it.amount } - totalGoalWithdrawals).coerceAtLeast(0.0)
    val context = LocalContext.current

    val currencyPreferences = remember {
        CurrencyPreferences(context)
    }

    val currencySymbol = currencyPreferences.getCurrencySymbol()
    var showUseGoalMoneyDialog by remember { mutableStateOf(false) }

    goal?.let { currentGoal ->
        if (showUseGoalMoneyDialog) {
            UseGoalMoneyDialog(
                availableAmount = totalSavings,
                currencySymbol = currencySymbol,
                errorMessage = withdrawalError,
                onDismiss = {
                    showUseGoalMoneyDialog = false
                    goalViewModel.clearGoalWithdrawalError()
                },
                onConfirm = { amount, description ->
                    goalViewModel.useGoalMoney(
                        goal = currentGoal,
                        availableAmount = totalSavings,
                        amount = amount,
                        description = description,
                        onSuccess = { showUseGoalMoneyDialog = false }
                    )
                }
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LumixaColors.Surface),
        contentPadding = PaddingValues(
            start = 22.dp,
            end = 22.dp,
            top = 28.dp,
            bottom = 110.dp
        )
    ) {
        item {
            Text(
                text = "← Volver",

                modifier = Modifier
                    .clickable {
                        onBackClick()
                    }
                    .padding(bottom = 18.dp),

                fontSize = 13.sp,

                color = Color(0xFF6B7280)
            )

            Text(
                text = "Detalle de meta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )

            Spacer(modifier = Modifier.height(18.dp))
        }

        if (goal == null) {
            item {
                EmptyGoalDetailCard()
            }
        } else {
            val progress =
                if (goal.targetAmount > 0) {
                    (totalSavings / goal.targetAmount)
                        .coerceIn(0.0, 1.0)
                        .toFloat()
                } else {
                    0f
                }

            val remainingAmount =
                (goal.targetAmount - totalSavings).coerceAtLeast(0.0)

            val daysLeft =
                calculateDaysLeft(goal.targetDate)

            val recommendedDailySaving =
                if (daysLeft > 0) {
                    ceil(remainingAmount / daysLeft).toInt()
                } else {
                    remainingAmount.toInt()
                }

            val status =
                when {
                    remainingAmount <= 0.0 -> "Meta alcanzada"
                    daysLeft < 0 -> "Meta vencida"
                    daysLeft == 0 -> "Último día"
                    else -> "En progreso"
                }

            item {
                GoalProgressDetailCard(
                    goalName = goal.name,
                    progress = progress,
                    savedAmount = totalSavings,
                    targetAmount = goal.targetAmount,
                    remainingAmount = remainingAmount,
                    currencySymbol = currencySymbol
                )

                Spacer(modifier = Modifier.height(16.dp))

                GoalTimeCard(
                    targetDate = goal.targetDate,
                    daysLeft = daysLeft,
                    status = status
                )

                Spacer(modifier = Modifier.height(16.dp))

                SmartGoalRecommendationCard(
                    status = status,
                    remainingAmount = remainingAmount,
                    recommendedDailySaving = recommendedDailySaving,
                    currencySymbol = currencySymbol
                )

                LaunchedEffect(
                    goal.id,
                    goal.targetAmount,
                    goal.targetDate,
                    totalSavings,
                    incomes.size,
                    expenses.size
                ) {
                    goalAiAnalysisViewModel.loadAnalysis(
                        goalName = goal.name,
                        targetAmount = goal.targetAmount,
                        savedAmount = totalSavings,
                        targetDate = goal.targetDate,
                        totalIncome = incomes.sumOf { it.amount },
                        totalExpenses = expenses.sumOf { it.amount },
                        totalSavings = totalSavings,
                        daysLeft = daysLeft
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                GoalAiAnalysisCard(
                    analysis = aiUiState.analysis,
                    isLoading = aiUiState.isLoading,
                    usedFallback = aiUiState.usedFallback,
                    onRefreshClick = {
                        goalAiAnalysisViewModel.loadAnalysis(
                            goalName = goal.name,
                            targetAmount = goal.targetAmount,
                            savedAmount = totalSavings,
                            targetDate = goal.targetDate,
                            totalIncome = incomes.sumOf { it.amount },
                            totalExpenses = expenses.sumOf { it.amount },
                            totalSavings = totalSavings,
                            daysLeft = daysLeft,
                            forceRefresh = true
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                GoalActionsCard(
                    showUseGoalMoney = progress >= 1f && totalSavings > 0.0,
                    onUseGoalMoneyClick = {
                        goalViewModel.clearGoalWithdrawalError()
                        showUseGoalMoneyDialog = true
                    },
                    onEditClick = onEditClick,
                    onDeleteClick = {
                        goalViewModel.deleteGoal(goal.id)
                        onBackClick()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Historial de meta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2A44)
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            val movementItems = buildList {
                addAll(savings.map { GoalHistoryItem.Saving(it.id, it.date, it.amount) })
                addAll(goalMovementsForGoal.map { GoalHistoryItem.Withdrawal(it.id, it.date, it.amount, it.description) })
            }.sortedByDescending { it.id }

            if (movementItems.isEmpty()) {
                item {
                    Text(
                        text = "Aún no tienes movimientos de meta registrados.",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            } else {
                items(movementItems) { movement ->
                    when (movement) {
                        is GoalHistoryItem.Saving -> SavingHistoryItem(
                            date = movement.date,
                            amount = movement.amount,
                            currencySymbol = currencySymbol
                        )
                        is GoalHistoryItem.Withdrawal -> GoalWithdrawalHistoryItem(
                            date = movement.date,
                            amount = movement.amount,
                            description = movement.description,
                            currencySymbol = currencySymbol
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalAiAnalysisCard(
    analysis: String,
    isLoading: Boolean,
    usedFallback: Boolean,
    onRefreshClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Análisis inteligente de meta",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (isLoading) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color(0xFF2D6CDF),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Analizando tu meta con IA...",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            } else {
                Text(
                    text = analysis,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = Color(0xFF2B2B2B)
                )
            }
            if (usedFallback) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mostrando análisis local por indisponibilidad temporal de IA.",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRefreshClick,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D6CDF))
            ) {
                Text(
                    text = "Actualizar análisis",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EmptyGoalDetailCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Text(
            text = "Aún no tienes una meta creada.",
            modifier = Modifier.padding(20.dp),
            fontSize = 14.sp,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
fun GoalProgressDetailCard(
    goalName: String,
    progress: Float,
    savedAmount: Double,
    targetAmount: Double,
    remainingAmount: Double,
    currencySymbol: String
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = goalName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1FBF9F)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp),
                color = Color(0xFF1FBF9F),
                trackColor = Color(0xFFE5E7EB)
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailMoneyRow(
                title = "Ahorrado",
                amount = "${currencySymbol}${savedAmount.toInt()}"
            )

            DetailMoneyRow(
                title = "Meta",
                amount = "${currencySymbol}${targetAmount.toInt()}"
            )

            DetailMoneyRow(
                title = "Faltante",
                amount = "${currencySymbol}${remainingAmount.toInt()}"
            )
        }
    }
}

@Composable
fun DetailMoneyRow(
    title: String,
    amount: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )

        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2A44)
        )
    }
}

@Composable
fun GoalTimeCard(
    targetDate: String,
    daysLeft: Int,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Tiempo",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )

            Spacer(modifier = Modifier.height(12.dp))

            DetailMoneyRow(
                title = "Fecha objetivo",
                amount = targetDate
            )

            DetailMoneyRow(
                title = "Días restantes",
                amount = if (daysLeft >= 0) "$daysLeft días" else "Vencida"
            )

            DetailMoneyRow(
                title = "Estado",
                amount = status
            )
        }
    }
}

@Composable
fun SmartGoalRecommendationCard(
    status: String,
    remainingAmount: Double,
    recommendedDailySaving: Int,
    currencySymbol: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEAF1FF)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Inteligencia financiera",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )

            Spacer(modifier = Modifier.height(10.dp))

            val message =
                when (status) {
                    "Meta alcanzada" ->
                        "Excelente. Ya alcanzaste esta meta financiera."

                    "Meta vencida" ->
                        "La fecha límite pasó y aún faltan ${currencySymbol}${remainingAmount.toInt()}. Puedes ampliar la fecha o aumentar tu ahorro diario."

                    "Último día" ->
                        "Hoy es el último día. Para completar la meta necesitas ahorrar ${currencySymbol}${remainingAmount.toInt()}."

                    else ->
                        "Para llegar a tiempo, deberías ahorrar aproximadamente ${currencySymbol}${recommendedDailySaving} por día."
                }

            Text(
                text = message,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Color(0xFF0F2A44)
            )
        }
    }
}


private sealed class GoalHistoryItem(open val id: Int) {
    data class Saving(
        override val id: Int,
        val date: String,
        val amount: Double
    ) : GoalHistoryItem(id)

    data class Withdrawal(
        override val id: Int,
        val date: String,
        val amount: Double,
        val description: String
    ) : GoalHistoryItem(id)
}

@Composable
fun UseGoalMoneyDialog(
    availableAmount: Double,
    currencySymbol: String,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Usar dinero de la meta",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )
        },
        text = {
            Column {
                Text(
                    text = "Disponible: ${currencySymbol}${availableAmount.toInt()}. Este movimiento se registrará como uso de meta y no como gasto normal.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { value ->
                        amountText = value.filter { it.isDigit() || it == '.' }
                        localError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Monto a usar") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Descripción") },
                    placeholder = { Text("Ejemplo: Compra de laptop") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                val visibleError = localError ?: errorMessage
                if (!visibleError.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = visibleError,
                        fontSize = 12.sp,
                        color = Color(0xFFE11D48)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    when {
                        amount <= 0.0 -> localError = "Ingresa un monto mayor a 0."
                        amount > availableAmount -> localError = "El monto supera el ahorro disponible de la meta."
                        else -> onConfirm(amount, description)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1FBF9F))
            ) {
                Text(
                    text = "Registrar uso",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF6B7280))
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun SavingHistoryItem(
    date: String,
    amount: Double,
    currencySymbol: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = date,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )

            Text(
                text = "+${currencySymbol}${amount.toInt()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1FBF9F)
            )
        }
    }
}

@Composable
fun GoalWithdrawalHistoryItem(
    date: String,
    amount: Double,
    description: String,
    currencySymbol: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF7ED)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = date,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2A44)
                )
                Text(
                    text = description.ifBlank { "Uso de dinero de la meta" },
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Text(
                text = "-${currencySymbol}${amount.toInt()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF59E0B)
            )
        }
    }
}

@Composable
fun GoalActionsCard(
    showUseGoalMoney: Boolean,
    onUseGoalMoneyClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Acciones de meta",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (showUseGoalMoney) {
                Button(
                    onClick = onUseGoalMoneyClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1FBF9F)
                    )
                ) {
                    Text(
                        text = "Usar dinero de la meta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = onEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D6CDF)
                )
            ) {
                Text(
                    text = "Editar meta",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onDeleteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE11D48)
                )
            ) {
                Text(
                    text = "Eliminar meta",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
fun calculateDaysLeft(
    targetDate: String
): Int {
    return try {
        val formatter = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        val goalDate =
            formatter.parse(targetDate) ?: return 0

        val today =
            Date()

        val diff =
            goalDate.time - today.time

        (diff / (1000 * 60 * 60 * 24)).toInt()

    } catch (e: Exception) {
        0
    }
}
