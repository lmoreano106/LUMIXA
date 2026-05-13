package com.lumixa.app.presentation.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpensesScreen() {
    var selectedTab by remember { mutableStateOf(0) }

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
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Hoy") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Semana") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Fecha") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> ExpensesList(title = "Gastos de hoy", total = "$45.000")
            1 -> ExpensesList(title = "Últimos 7 días", total = "$182.000")
            2 -> ExpensesList(title = "Gastos por fecha", total = "$23.000")
        }
    }
}

@Composable
fun ExpensesList(
    title: String,
    total: String
) {
    LazyColumn {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF0F2A44))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Total: $total", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D6CDF))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(3) {
            ExpenseItem(
                category = "Comida",
                description = "Almuerzo universidad",
                amount = "$15.000",
                time = "12:40 PM"
            )
        }
    }
}

@Composable
fun ExpenseItem(
    category: String,
    description: String,
    amount: String,
    time: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(category, fontWeight = FontWeight.Bold, color = Color(0xFF0F2A44))
                Text(description, fontSize = 12.sp, color = Color(0xFF6B7280))
                Text(time, fontSize = 11.sp, color = Color(0xFF9CA3AF))
            }

            Text(
                text = amount,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE11D48)
            )
        }
    }
}