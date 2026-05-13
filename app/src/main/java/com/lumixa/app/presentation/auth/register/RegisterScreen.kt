package com.lumixa.app.presentation.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lumixa.app.R

@Composable
fun RegisterScreen(
    onCreateAccountClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    onBackToHomeClick: () -> Unit
) {

    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(horizontal = 28.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "← Volver",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBackToLoginClick() },
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_user),
            contentDescription = "Registro",
            tint = Color.White,
            modifier = Modifier
                .background(Color(0xFF2D6CDF), RoundedCornerShape(18.dp))
                .padding(18.dp)
                .size(34.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Crear cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2A44)
        )

        Text(
            text = "Regístrate para comenzar con LUMIXA",
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))



        LumixaTextField(
            label = "NOMBRE COMPLETO",
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = "Tu nombre"
        )

        Spacer(modifier = Modifier.height(12.dp))

        LumixaTextField(
            label = "USERNAME",
            value = username,
            onValueChange = { username = it },
            placeholder = "usuario123"
        )

        Spacer(modifier = Modifier.height(12.dp))

        LumixaTextField(
            label = "CORREO",
            value = email,
            onValueChange = { email = it },
            placeholder = "tu@correo.com",
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "CONTRASEÑA",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("••••••••") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                Text(
                    text = if (passwordVisible) "Ocultar" else "Ver",
                    color = Color(0xFF2D6CDF),
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        passwordVisible = !passwordVisible
                    }
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onCreateAccountClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D6CDF)
            )
        ) {
            Text(
                text = "Crear cuenta",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "← Volver al inicio",
            color = Color(0xFF6B7280),
            fontSize = 12.sp,
            modifier = Modifier.clickable { onBackToHomeClick() }
        )
    }
}

@Composable
fun LumixaTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Text(
        text = label,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6B7280)
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        )
    )
}