package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumixa.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    fun register(
        email: String,
        password: String,
        fullName: String,
        currencyCode: String,
        currencySymbol: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            val result = repository.register(
                email = email,
                password = password,
                fullName = fullName,
                currencyCode = currencyCode,
                currencySymbol = currencySymbol
            )

            _isLoading.value = false

            if (result.isSuccess) {
                onSuccess()
            } else {
                _authError.value = getSpanishAuthError(
                    result.exceptionOrNull()
                )
            }
        }
    }

    fun login(
        email: String,
        password: String,
        currencyCode: String,
        currencySymbol: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            _isLoading.value = true
            _authError.value = null

            val result = repository.login(
                email = email,
                password = password,
                currencyCode = currencyCode,
                currencySymbol = currencySymbol
            )

            _isLoading.value = false

            if (result.isSuccess) {

                onSuccess()

            } else {

                _authError.value = getSpanishAuthError(
                    result.exceptionOrNull()
                )
            }
        }
    }

    fun logout() {
        repository.logout()
    }

    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }

    fun syncCurrentUserProfile(
        currencyCode: String,
        currencySymbol: String
    ) {
        viewModelScope.launch {
            repository.syncCurrentUserProfile(
                currencyCode = currencyCode,
                currencySymbol = currencySymbol
            )
        }
    }

    private fun getSpanishAuthError(
        exception: Throwable?
    ): String {

        val message = exception?.message ?: ""

        return when {

            message.contains(
                "email address is badly formatted",
                ignoreCase = true
            ) -> {
                "El correo electrónico no tiene un formato válido."
            }

            message.contains(
                "password is invalid",
                ignoreCase = true
            ) -> {
                "La contraseña es incorrecta."
            }

            message.contains(
                "no user record",
                ignoreCase = true
            ) -> {
                "No existe una cuenta con este correo."
            }

            message.contains(
                "already in use",
                ignoreCase = true
            ) -> {
                "Este correo ya está registrado."
            }

            message.contains(
                "Password should be at least 6 characters",
                ignoreCase = true
            ) -> {
                "La contraseña debe tener mínimo 6 caracteres."
            }

            message.contains(
                "network error",
                ignoreCase = true
            ) -> {
                "Error de conexión. Revisa tu internet."
            }

            else -> {
                "No se pudo completar la operación. Intenta nuevamente."
            }
        }
    }
}
