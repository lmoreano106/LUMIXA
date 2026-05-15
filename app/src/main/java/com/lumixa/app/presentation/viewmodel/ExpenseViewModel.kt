package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumixa.app.data.local.entity.ExpenseEntity
import com.lumixa.app.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _expenses =
        MutableStateFlow<List<ExpenseEntity>>(emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> =
        _expenses.asStateFlow()

    init {
        getExpenses()
    }

    private fun getExpenses() {

        viewModelScope.launch {

            repository.getAllExpenses().collect {

                _expenses.value = it
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

            repository.insertExpense(
                ExpenseEntity(
                    category = category,
                    description = description,
                    amount = amount,
                    date = date,
                    time = time,
                            dayOfWeek = dayOfWeek
                )
            )
        }
    }

    fun deleteExpense(id: Int) {

        viewModelScope.launch {

            repository.deleteExpense(id)
        }
    }
}