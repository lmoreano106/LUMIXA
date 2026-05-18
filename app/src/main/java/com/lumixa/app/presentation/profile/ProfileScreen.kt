package com.lumixa.app.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.lumixa.app.data.preferences.CurrencyPreferences

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser

    val username =
        user?.displayName
            ?: user?.email?.substringBefore("@")
            ?: "Usuario"

    val email =
        user?.email ?: "Sin correo"
    val context = LocalContext.current

    val currencyPreferences = remember {
        CurrencyPreferences(context)
    }

    val currencyCode =
        currencyPreferences.getCurrencyCode()

    val currencyName =
        currencyPreferences.getCurrencyName()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
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

        Spacer(modifier = Modifier.height(18.dp))

        ProfileInfoCard(
            title = "Moneda",
            value = "$currencyCode - $currencyName"
        )

        Spacer(modifier = Modifier.height(12.dp))

        ProfileInfoCard(
            title = "Seguridad",
            value = "Inicio de sesión con Firebase"
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE11D48)
            )
        ) {
            Text(
                text = "Cerrar sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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