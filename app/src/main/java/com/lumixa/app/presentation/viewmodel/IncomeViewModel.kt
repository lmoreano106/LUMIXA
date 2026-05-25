package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lumixa.app.data.local.entity.IncomeEntity
import com.lumixa.app.data.repository.IncomeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IncomeViewModel(
    private val repository: IncomeRepository
) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private var incomesJob: Job? = null

    private val _incomes =
        MutableStateFlow<List<IncomeEntity>>(emptyList())

    val incomes: StateFlow<List<IncomeEntity>> =
        _incomes.asStateFlow()

    init {
        refreshIncomes()
    }

    private fun getCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid ?: ""
    }

    fun refreshIncomes() {
        incomesJob?.cancel()

        val userId = getCurrentUserId()

        if (userId.isBlank()) {
            _incomes.value = emptyList()
            return
        }

        incomesJob = viewModelScope.launch {
            repository.getAllIncomes(userId).collect { incomesList ->
                _incomes.value = incomesList
            }
        }
    }

    fun addIncome(
        amount: Double,
        type: String,
        description: String,
        date: String,
        time: String
    ) {
        viewModelScope.launch {
            val userId = getCurrentUserId()

            if (userId.isNotBlank()) {
                repository.insertIncome(
                    IncomeEntity(
                        userId = userId,
                        amount = amount,
                        type = type,
                        description = description,
                        date = date,
                        time = time
                    )
                )

                refreshIncomes()
            }
        }
    }

    fun deleteIncome(id: Int) {
        viewModelScope.launch {
            val userId = getCurrentUserId()

            if (userId.isNotBlank()) {
                repository.deleteIncome(
                    userId = userId,
                    incomeId = id
                )
                refreshIncomes()
            }
        }
    }

    fun syncIncomesFromFirestore(onFinished: () -> Unit = {}) {
        viewModelScope.launch {
            repository.syncIncomesFromFirestore()
            refreshIncomes()
            onFinished()
        }
    }

    fun clearData() {
        incomesJob?.cancel()
        _incomes.value = emptyList()
    }
}
