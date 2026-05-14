package com.lumixa.app.presentation.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.data.local.entity.ExpenseEntity
import com.lumixa.app.presentation.viewmodel.ExpenseViewModel

@Composable
fun ExpensesScreen(
    expenseViewModel: ExpenseViewModel
) {

    val expenses by expenseViewModel.expenses.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    var selectedExpense by remember {
        mutableStateOf<ExpenseEntity?>(null)
    }

    val totalExpenses =
        expenses.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(20.dp)
    ) {

        Text(
            text = "Gastos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2A44)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedTab) {

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
                    title = "Todos los gastos",
                    total = "$${totalExpenses.toInt()}",
                    expenses = expenses,
                    onExpenseClick = {
                        selectedExpense = it
                    }
                )
            }

            1 -> {
                RealExpensesList(
                    title = "Últimos gastos",
                    total = "$${totalExpenses.toInt()}",
                    expenses = expenses,
                    onExpenseClick = {
                        selectedExpense = it
                    }
                )
            }

            2 -> {
                RealExpensesList(
                    title = "Gastos registrados",
                    total = "$${totalExpenses.toInt()}",
                    expenses = expenses,
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
                            text = "No tienes gastos registrados",
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp
                        )
                    }
                }
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
                color = Color(0xFF0F2A44)
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
                    color = Color(0xFF0F2A44)
                )

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
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