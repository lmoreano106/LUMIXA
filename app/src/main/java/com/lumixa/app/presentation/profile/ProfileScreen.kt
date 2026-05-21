package com.lumixa.app.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.lumixa.app.data.preferences.CurrencyPreferences
import com.lumixa.app.data.repository.FirestoreRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.launch

private data class CurrencyItem(
    val code: String,
    val name: String,
    val symbol: String
)

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val firestoreRepository = remember { FirestoreRepository() }
    val currencyPreferences = remember { CurrencyPreferences(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val availableCurrencies = remember {
        listOf(
            CurrencyItem("COP", "Pesos colombianos", "$"),
            CurrencyItem("PEN", "Soles peruanos", "S/")
        )
    }

    val initialName = user?.displayName?.takeIf { it.isNotBlank() }
        ?: user?.email?.substringBefore("@")
        ?: ""
    var editableName by rememberSaveable { mutableStateOf(initialName) }

    val savedCurrencyCode = currencyPreferences.getCurrencyCode()
    var selectedCurrencyCode by rememberSaveable { mutableStateOf(savedCurrencyCode) }

    val selectedCurrency = availableCurrencies.firstOrNull { it.code == selectedCurrencyCode }
        ?: availableCurrencies.first()

    var isSaving by rememberSaveable { mutableStateOf(false) }

    val username = editableName.ifBlank { "Usuario" }
    val email = user?.email ?: "Sin correo"
    val uid = user?.uid ?: "Sin UID"
    val creationDate = user?.metadata?.creationTimestamp?.let { timestamp ->
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
    } ?: "No disponible"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(22.dp)
        ) {
            Text(
                text = "Perfil",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )

            Spacer(modifier = Modifier.height(22.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(Color(0xFF2D6CDF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = username.take(1).uppercase(),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Avatar",
                            fontSize = 12.sp,
                            color = Color(0xFF2B2B2B)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar avatar",
                            tint = Color(0xFF1FBF9F)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = username,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2A44)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = email,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = editableName,
                onValueChange = { editableName = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(
                title = "UID",
                value = uid
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(
                title = "Cuenta creada",
                value = creationDate
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(
                title = "Moneda actual",
                value = "${selectedCurrency.code} - ${selectedCurrency.name} (${selectedCurrency.symbol})"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "SELECCIONAR MONEDA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    availableCurrencies.forEach { item ->
                        CurrencySelectorItem(
                            item = item,
                            selected = selectedCurrencyCode == item.code,
                            enabled = !isSaving,
                            onClick = { selectedCurrencyCode = item.code }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoCard(
                title = "Seguridad",
                value = "Inicio de sesión con Firebase"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (user == null) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("No hay usuario autenticado")
                        }
                        return@Button
                    }

                    isSaving = true
                    coroutineScope.launch {
                        val selected = availableCurrencies.first { it.code == selectedCurrencyCode }
                        runCatching {
                            val cleanName = editableName.trim()
                            if (cleanName.isNotBlank() && cleanName != user.displayName) {
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(cleanName)
                                    .build()
                                user.updateProfile(profileUpdates).awaitCompletion()
                            }

                            currencyPreferences.saveCurrency(
                                code = selected.code,
                                name = selected.name,
                                symbol = selected.symbol
                            )

                            firestoreRepository.upsertUserProfile(
                                userId = user.uid,
                                uid = user.uid,
                                email = user.email,
                                displayName = editableName.trim().ifBlank { user.displayName },
                                currencySymbol = selected.symbol,
                                currencyCode = selected.code
                            )
                        }.onSuccess {
                            snackbarHostState.showSnackbar("Cambios guardados correctamente")
                        }.onFailure {
                            snackbarHostState.showSnackbar("Error al guardar cambios")
                        }
                        isSaving = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1FBF9F)),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Guardando...", color = Color.White)
                } else {
                    Text(
                        text = "Guardar cambios",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))



            Button(
                onClick = onAdminClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D6CDF)),
                enabled = !isSaving
            ) {
                Text(
                    text = "Panel Administrador",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                enabled = !isSaving
            ) {
                Text(
                    text = "Cerrar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

private suspend fun com.google.android.gms.tasks.Task<Void>.awaitCompletion() {
    suspendCoroutine<Unit> { continuation ->
        addOnSuccessListener { continuation.resume(Unit) }
            .addOnFailureListener { continuation.resumeWithException(it) }
    }
}

@Composable
private fun CurrencySelectorItem(
    item: CurrencyItem,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .background(
                color = if (selected) Color(0xFFEAF1FF) else Color(0xFFF4F6F8),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${item.code} · ${item.name} (${item.symbol})",
            fontSize = 14.sp,
            color = Color(0xFF0F2A44),
            fontWeight = FontWeight.Medium
        )

        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2D6CDF))
        )
    }
}

@Composable
fun ProfileInfoCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F2A44)
            )
        }
    }
}
