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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpensesScreen() {

    var selectedTab by remember { mutableStateOf(0) }

    var showDetail by remember {
        mutableStateOf(false)
    }

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
                ExpensesList(
                    title = "Gastos de hoy",
                    total = "$45.000",
                    onExpenseClick = {
                        showDetail = true
                    }
                )
            }

            1 -> {
                WeeklyExpenses(
                    onExpenseClick = {
                        showDetail = true
                    }
                )
            }

            2 -> {
                DateExpenses(
                    onExpenseClick = {
                        showDetail = true
                    }
                )
            }
        }

        if (showDetail) {

            ExpenseDetailDialog(
                amount = "$15.000",
                category = "Comida",
                description = "Almuerzo universidad",
                date = "12 mayo 2026",
                time = "12:40 PM",

                onDismiss = {
                    showDetail = false
                },

                onDeleteClick = {
                    showDetail = false
                }
            )
        }
    }
}

@Composable
fun ExpensesList(
    title: String,
    total: String,
    onExpenseClick: () -> Unit
) {

    LazyColumn {

        item {

            SummaryExpenseCard(
                title = title,
                total = total
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(3) {

            ExpenseItem(
                category = "Comida",
                description = "Almuerzo universidad",
                amount = "$15.000",
                time = "12:40 PM",
                onClick = onExpenseClick
            )
        }
    }
}
@Composable
fun WeeklyExpenses(
    onExpenseClick: () -> Unit
) {

    LazyColumn {

        item {

            SummaryExpenseCard(
                title = "Últimos 7 días",
                total = "$182.000"
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        item {

            DaySection(
                day = "Hoy",
                total = "$45.000",
                onExpenseClick = onExpenseClick
            )
        }

        item {

            DaySection(
                day = "Ayer",
                total = "$27.000",
                onExpenseClick = onExpenseClick
            )
        }

        item {

            DaySection(
                day = "10 mayo",
                total = "$68.000",
                onExpenseClick = onExpenseClick
            )
        }
    }
}
@Composable

fun DateExpenses(
    onExpenseClick: () -> Unit
) {
    var selectedDate by remember { mutableStateOf("12 mayo") }

    LazyColumn {
        item {
            Text(
                text = "Selecciona una fecha",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                DateChip(
                    text = "12 mayo",
                    selected = selectedDate == "12 mayo",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedDate = "12 mayo" }
                )

                Spacer(modifier = Modifier.width(8.dp))

                DateChip(
                    text = "11 mayo",
                    selected = selectedDate == "11 mayo",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedDate = "11 mayo" }
                )

                Spacer(modifier = Modifier.width(8.dp))

                DateChip(
                    text = "10 mayo",
                    selected = selectedDate == "10 mayo",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedDate = "10 mayo" }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SummaryExpenseCard(
                title = "Gastos del $selectedDate",
                total = when (selectedDate) {
                    "12 mayo" -> "$23.000"
                    "11 mayo" -> "$37.000"
                    else -> "$68.000"
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(3) {
            ExpenseItem(
                category = "Comida",
                description = "Gasto registrado el $selectedDate",
                amount = "$15.000",
                time = "12:40 PM",
                onClick = onExpenseClick
            )
        }
    }
}

@Composable
fun DateChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(42.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF2D6CDF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 3.dp else 1.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else Color(0xFF0F2A44)
            )
        }
    }
}

@Composable
fun DaySection(
    day: String,
    total: String,
    onExpenseClick: () -> Unit
) {

    Column {

        Text(
            text = "$day • $total",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2A44)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExpenseItem(
            category = "Comida",
            description = "Almuerzo",
            amount = "$15.000",
            time = "12:40 PM",
            onClick = onExpenseClick
        )

        ExpenseItem(
            category = "Transporte",
            description = "Bus",
            amount = "$7.000",
            time = "08:10 AM",
            onClick = onExpenseClick
        )

        Spacer(modifier = Modifier.height(14.dp))
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