package com.lumixa.app.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumixa.app.data.repository.AdminUserSummary
import com.lumixa.app.presentation.viewmodel.AdminViewModel

@Composable
fun AdminScreen(
    onBackClick: () -> Unit,
    adminViewModel: AdminViewModel = viewModel()
) {
    val uiState by adminViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(16.dp)
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator(color = Color(0xFF2D6CDF))
            !uiState.isAuthorized -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("⛔ Acceso no autorizado", fontSize = 22.sp, color = Color(0xFF0F2A44), fontWeight = FontWeight.Bold)
                    Text(uiState.errorMessage ?: "Solo administradores pueden ver este panel.", color = Color(0xFF2B2B2B))
                    Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D6CDF))) {
                        Text("Volver", color = Color.White)
                    }
                }
            }
            else -> {
                val data = uiState.dashboardData
                if (data == null) {
                    Text("No hay datos disponibles", color = Color(0xFF2B2B2B))
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { Text("🛡️ Panel Administrador", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2A44)) }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                MetricCard("Usuarios", data.metrics.totalUsers.toString(), Modifier.weight(1f))
                                MetricCard("Gastos globales", "$${"%.2f".format(data.metrics.totalExpenses)}", Modifier.weight(1f))
                            }
                        }
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                MetricCard("Metas globales", "$${"%.2f".format(data.metrics.totalGoals)}", Modifier.weight(1f))
                                MetricCard("Registros", "${data.metrics.totalExpenseRecords + data.metrics.totalGoalRecords}", Modifier.weight(1f))
                            }
                        }
                        item { Text("👥 Usuarios", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2A44)) }
                        items(data.users) { user -> UserCard(user) }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onBackClick,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1FBF9F)),
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Volver al perfil", color = Color.White) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = Color(0xFF2B2B2B), fontSize = 12.sp)
            Text(value, color = Color(0xFF0F2A44), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
private fun UserCard(user: AdminUserSummary) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("${user.displayName} (${user.role})", color = Color(0xFF0F2A44), fontWeight = FontWeight.Bold)
            Text(user.email, color = Color(0xFF2B2B2B), fontSize = 13.sp)
            Text("uid: ${user.uid}", color = Color(0xFF2B2B2B), fontSize = 11.sp)
        }
    }
}
