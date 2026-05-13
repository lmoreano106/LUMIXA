package com.lumixa.app.presentation.auth.login

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
fun LoginScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackToHomeClick: () -> Unit
) {
    var isAdminMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(horizontal = 28.dp, vertical = 34.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFF2D6CDF), RoundedCornerShape(18.dp))
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = "Usuario",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = if (isAdminMode) "Acceso administrador" else "Bienvenido",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F2A44)
        )

        Text(
            text = if (isAdminMode) {
                "Ingresa con credenciales administrativas"
            } else {
                "Inicia sesión en LUMIXA"
            },
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "CORREO",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("tu@correo.com") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

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

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF2D6CDF),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAdminMode) {
                    Color(0xFF0F2A44)
                } else {
                    Color(0xFF2D6CDF)
                }
            )
        ) {
            Text(
                text = if (isAdminMode) "Ingresar como administrador" else "Iniciar sesión",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (!isAdminMode) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = Color(0xFF6B7280),
                    fontSize = 13.sp
                )

                Text(
                    text = "Regístrate",
                    color = Color(0xFF2D6CDF),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onRegisterClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Acceso administrativo",
                color = Color(0xFF9CA3AF),
                fontSize = 11.sp,
                modifier = Modifier.clickable {
                    isAdminMode = true
                }
            )
        } else {
            Text(
                text = "← Volver a usuario",
                color = Color(0xFF2D6CDF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    isAdminMode = false
                }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "← Volver al inicio",
            color = Color(0xFF6B7280),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable {
                onBackToHomeClick()
            }
        )
    }
}