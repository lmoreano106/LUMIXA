package com.lumixa.app.presentation.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.data.preferences.CurrencyPreferences
import com.lumixa.app.presentation.components.LumixaColors
import com.lumixa.app.presentation.components.LumixaEmptyStateCard
import com.lumixa.app.data.local.entity.ExpenseEntity
import com.lumixa.app.presentation.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    expenseViewModel: ExpenseViewModel
) {
    val expenses by expenseViewModel.expenses.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val currencyPreferences = remember(context) { CurrencyPreferences(context) }
    val currencySymbol = currencyPreferences.getCurrencySymbol()

    var selectedTab by remember { mutableStateOf(0) }
    var selectedExpense by remember { mutableStateOf<ExpenseEntity?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = androidx.compose.material3.rememberDatePickerState()

    val formatter = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES"))
    }

    val selectedDate = remember(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            formatter.format(Date(millis))
        } ?: formatter.format(Date())
    }

    val todayDate = remember {
        formatter.format(Date())
    }

    val todayExpenses = expenses.filter {
        it.date == todayDate
    }

    val totalToday = todayExpenses.sumOf { it.amount }

    val totalWeek = expenses.sumOf { it.amount }

    val filteredByDate = expenses.filter {
        it.date == selectedDate
    }

    val totalByDate = filteredByDate.sumOf { it.amount }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumixaColors.Surface)
            .padding(20.dp)
    ) {
        Text(
            text = "Gastos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedTab, containerColor = Color.White) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Hoy") }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Semana") }
            )

            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Fecha") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                RealExpensesList(
                    title = "Gastos de hoy",
                    total = "$currencySymbol${totalToday.toInt()}",
                    expenses = todayExpenses,
                    onExpenseClick = {
                        selectedExpense = it
                    }
                )
            }

            1 -> {
                WeeklyExpensesList(
                    total = "$currencySymbol${totalWeek.toInt()}",
                    expenses = expenses,
                    onExpenseClick = {
                        selectedExpense = it
                    }
                )
            }

            2 -> {
                DateExpensesList(
                    selectedDate = selectedDate,
                    total = "$currencySymbol${totalByDate.toInt()}",
                    expenses = filteredByDate,
                    onSelectDateClick = {
                        showDatePicker = true
                    },
                    onExpenseClick = {
                        selectedExpense = it
                    }
                )
            }
        }

        selectedExpense?.let { expense ->
            ExpenseDetailDialog(
                amount = "$${expense.amount.toInt()}",
                category = expense.category,
                description = expense.description,
                date = expense.date,
                time = expense.time,
                onDismiss = {
                    selectedExpense = null
                },
                onDeleteClick = {
                    expenseViewModel.deleteExpense(expense.id)
                    selectedExpense = null
                }
            )
        }
    }
}

@Composable

fun WeeklyExpensesList(
    total: String,
    expenses: List<ExpenseEntity>,
    onExpenseClick: (ExpenseEntity) -> Unit
) {
    val dateFormatter = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES"))
    }

    val dayFormatter = remember {
        SimpleDateFormat("EEEE", Locale("es", "ES"))
    }

    val groupedExpenses = expenses
        .groupBy { it.date }
        .toList()
        .sortedByDescending { (date, _) ->
            try {
                dateFormatter.parse(date)?.time ?: 0L
            } catch (e: Exception) {
                0L
            }
        }

    LazyColumn {
        item {
            SummaryExpenseCard(
                title = "Gasto semanal",
                total = total
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (expenses.isEmpty()) {
            item {
                EmptyExpensesCard(
                    text = "No tienes gastos esta semana"
                )
            }
        }

        groupedExpenses.forEach { (date, dayExpenses) ->

            val parsedDate = try {
                dateFormatter.parse(date)
            } catch (e: Exception) {
                null
            }

            val dayName = parsedDate?.let {
                dayFormatter.format(it).replaceFirstChar { char ->
                    char.uppercase()
                }
            } ?: "Día"

            val dayTotal = dayExpenses.sumOf { it.amount }

            item {
                DayExpenseHeader(
                    dayName = dayName,
                    date = date,
                    total = "$${dayTotal.toInt()}"
                )
            }

            items(dayExpenses.sortedByDescending { it.id }) { expense ->
                ExpenseItem(
                    category = expense.category,
                    description = expense.description,
                    amount = "$${expense.amount.toInt()}",
                    time = expense.time,
                    onClick = {
                        onExpenseClick(expense)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DayExpenseHeader(
    dayName: String,
    date: String,
    total: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = dayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = date,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = total,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D6CDF)
            )
        }
    }
}

@Composable
fun DateExpensesList(
    selectedDate: String,
    total: String,
    expenses: List<ExpenseEntity>,
    onSelectDateClick: () -> Unit,
    onExpenseClick: (ExpenseEntity) -> Unit
) {
    LazyColumn {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelectDateClick()
                    },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Fecha seleccionada",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = selectedDate,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Toca aquí para cambiar la fecha",
                        fontSize = 12.sp,
                        color = Color(0xFF2D6CDF),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SummaryExpenseCard(
                title = "Gastos del día",
                total = total
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (expenses.isEmpty()) {
            item {
                EmptyExpensesCard(
                    text = "No tienes gastos en esta fecha"
                )
            }
        }

        items(expenses.reversed()) { expense ->
            ExpenseItem(
                category = expense.category,
                description = expense.description,
                amount = "$${expense.amount.toInt()}",
                time = expense.time,
                onClick = {
                    onExpenseClick(expense)
                }
            )
        }
    }
}

@Composable
fun RealExpensesList(
    title: String,
    total: String,
    expenses: List<ExpenseEntity>,
    onExpenseClick: (ExpenseEntity) -> Unit
) {
    LazyColumn {
        item {
            SummaryExpenseCard(
                title = title,
                total = total
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (expenses.isEmpty()) {
            item {
                EmptyExpensesCard(
                    text = "No tienes gastos registrados"
                )
            }
        }

        items(expenses.reversed()) { expense ->
            ExpenseItem(
                category = expense.category,
                description = expense.description,
                amount = "$${expense.amount.toInt()}",
                time = expense.time,
                onClick = {
                    onExpenseClick(expense)
                }
            )
        }
    }
}

@Composable
fun EmptyExpensesCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SummaryExpenseCard(
    title: String,
    total: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Total: $total",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D6CDF)
            )
        }
    }
}

@Composable
fun ExpenseItem(
    category: String,
    description: String,
    amount: String,
    time: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable {
                onClick()
            },
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
            Column {
                Text(
                    text = category,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = time,
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            Text(
                text = amount,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE11D48)
            )
        }
    }
}
