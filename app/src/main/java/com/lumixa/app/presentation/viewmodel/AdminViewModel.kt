    package com.lumixa.app.presentation.viewmodel

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.google.firebase.auth.FirebaseAuth
    import com.lumixa.app.data.repository.AdminDashboardData
    import com.lumixa.app.data.repository.AdminRepository
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.launch

    data class AdminUiState(
        val isLoading: Boolean = true,
        val isAuthorized: Boolean = false,
        val dashboardData: AdminDashboardData? = null,
        val errorMessage: String? = null
    )

    class AdminViewModel(
        private val adminRepository: AdminRepository = AdminRepository(),
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    ) : ViewModel() {

        private val _uiState = MutableStateFlow(AdminUiState())
        val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

        init {
            loadAdminData()
        }

        fun loadAdminData() {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                val uid = auth.currentUser?.uid

                if (uid.isNullOrBlank()) {
                    _uiState.value = AdminUiState(
                        isLoading = false,
                        isAuthorized = false,
                        errorMessage = "No hay sesión activa"
                    )
                    return@launch
                }

                runCatching {
                    val isAdmin = adminRepository.isCurrentUserAdmin(uid)
                    if (!isAdmin) {
                        auth.signOut()
                        _uiState.value = AdminUiState(
                            isLoading = false,
                            isAuthorized = false,
                            errorMessage = "Acceso no autorizado para administrador."
                        )
                        return@runCatching
                    }

                    val dashboardData = adminRepository.getAdminDashboardData()
                    _uiState.value = AdminUiState(
                        isLoading = false,
                        isAuthorized = true,
                        dashboardData = dashboardData
                    )
                }.onFailure { exception ->
                    _uiState.value = AdminUiState(
                        isLoading = false,
                        isAuthorized = false,
                        errorMessage = exception.message ?: "No se pudo cargar el panel"
                    )
                }
            }
        }
    }
