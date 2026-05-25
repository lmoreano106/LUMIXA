package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lumixa.app.data.local.entity.ExpenseEntity
import com.lumixa.app.data.repository.ExpenseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private var expensesJob: Job? = null

    private val _expenses =
        MutableStateFlow<List<ExpenseEntity>>(emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> =
        _expenses.asStateFlow()

    init {
        refreshExpenses()
    }

    private fun getCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid ?: ""
    }

    fun refreshExpenses() {
        expensesJob?.cancel()

        val userId = getCurrentUserId()

        if (userId.isBlank()) {
            _expenses.value = emptyList()
            return
        }

        expensesJob = viewModelScope.launch {
            repository.getAllExpenses(userId).collect { expensesList ->
                _expenses.value = expensesList
            }
        }
    }

    fun addExpense(
        category: String,
        description: String,
        amount: Double,
        date: String,
        time: String,
        dayOfWeek: Int
    ) {
        viewModelScope.launch {
            val userId = getCurrentUserId()

            if (userId.isNotBlank()) {
                repository.insertExpense(
                    ExpenseEntity(
                        userId = userId,
                        category = category,
                        description = description,
                        amount = amount,
                        date = date,
                        time = time,
                        dayOfWeek = dayOfWeek
                    )
                )

                refreshExpenses()
            }
        }
    }

    fun deleteExpense(id: Int) {
        viewModelScope.launch {
            val userId = getCurrentUserId()

            if (userId.isNotBlank()) {
                repository.deleteExpense(userId = userId, expenseId = id)
            }
            refreshExpenses()
        }
    }
    fun syncExpensesFromFirestore(onFinished: () -> Unit = {}) {
        viewModelScope.launch {
            repository.syncExpensesFromFirestore()
            refreshExpenses()
            onFinished()
        }
    }

    fun clearData() {
        expensesJob?.cancel()
        _expenses.value = emptyList()
    }
}