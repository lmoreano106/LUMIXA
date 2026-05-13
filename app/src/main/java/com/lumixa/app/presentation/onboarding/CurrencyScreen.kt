package com.lumixa.app.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CurrencyScreen(
    onContinueClick: () -> Unit
) {
    var selectedCurrency by remember { mutableStateOf("COP") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(horizontal = 28.dp, vertical = 34.dp)
    ) {
        Text(
            text = "Elige tu moneda",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2A44)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Usaremos esta moneda para mostrar tus ingresos y gastos.",
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEAF1FF)
            )
        ) {
            Text(
                text = "Esta configuración se aplicará a todas las pantallas. Podrás cambiarla más adelante.",
                modifier = Modifier.padding(18.dp),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Color(0xFF2D6CDF)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        CurrencyOption(
            code = "COP",
            name = "Pesos colombianos",
            symbol = "$",
            selected = selectedCurrency == "COP",
            onClick = {
                selectedCurrency = "COP"
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        CurrencyOption(
            code = "PEN",
            name = "Soles peruanos",
            symbol = "S/",
            selected = selectedCurrency == "PEN",
            onClick = {
                selectedCurrency = "PEN"
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onContinueClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D6CDF)
            )
        ) {
            Text(
                text = "Continuar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun CurrencyOption(
    code: String,
    name: String,
    symbol: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFFEAF1FF) else Color.White
        ),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(
                width = 1.5.dp,
                color = Color(0xFF2D6CDF)
            )
        } else {
            androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = Color(0xFFE5E7EB)
            )
        }
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = code.take(2),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2A44)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2A44)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$code · Símbolo: $symbol",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }

            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF2D6CDF)
                )
            )
        }
    }
}