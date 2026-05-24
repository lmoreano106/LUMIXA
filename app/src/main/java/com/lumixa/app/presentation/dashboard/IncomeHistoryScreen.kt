package com.lumixa.app.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.data.local.entity.IncomeEntity
import com.lumixa.app.presentation.components.LumixaColors
import com.lumixa.app.presentation.viewmodel.IncomeViewModel

@Composable
fun IncomeHistoryScreen(
    incomeViewModel: IncomeViewModel,
    currencySymbol: String,
    onBackClick: () -> Unit
) {
    val incomes by incomeViewModel.incomes.collectAsState()
    var selectedIncome by remember { mutableStateOf<IncomeEntity?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumixaColors.Surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "← Volver",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .clickable { onBackClick() }
            )

            Text(
                text = "Historial de ingresos",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Revisa y corrige tus ingresos registrados.",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            if (incomes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = "Aún no registras ingresos.",
                            color = Color(0xFF2B2B2B),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(incomes, key = { it.id }) { income ->
                        IncomeHistoryItem(
                            income = income,
                            currencySymbol = currencySymbol,
                            onDeleteClick = { selectedIncome = income }
                        )
                    }
                }
            }
        }

        selectedIncome?.let { income ->
            AlertDialog(
                onDismissRequest = { selectedIncome = null },
                title = {
                    Text(
                        text = "Eliminar ingreso",
                        color = Color(0xFF0F2A44),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "¿Seguro que quieres eliminar este ingreso? Esta acción no se puede deshacer.",
                        color = Color(0xFF2B2B2B)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        incomeViewModel.deleteIncome(income.id)
                        selectedIncome = null
                    }) {
                        Text("Eliminar", color = Color(0xFF2D6CDF), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedIncome = null }) {
                        Text("Cancelar", color = Color(0xFF6B7280))
                    }
                },
                containerColor = Color(0xFFF4F6F8)
            )
        }
    }
}

@Composable
private fun IncomeHistoryItem(
    income: IncomeEntity,
    currencySymbol: String,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "$currencySymbol${income.amount}",
                    color = Color(0xFF0F2A44),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = income.type,
                    color = Color(0xFF1FBF9F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = income.description, color = Color(0xFF2B2B2B), fontSize = 13.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Fecha: ${income.date}", color = Color(0xFF6B7280), fontSize = 12.sp)
            Text(text = "Hora: ${income.time}", color = Color(0xFF6B7280), fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDeleteClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D6CDF))
            ) {
                Text(text = "Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
