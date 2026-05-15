package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumixa.app.data.local.entity.IncomeEntity
import com.lumixa.app.data.repository.IncomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IncomeViewModel(
    private val repository: IncomeRepository
) : ViewModel() {

    private val _incomes =
        MutableStateFlow<List<IncomeEntity>>(emptyList())

    val incomes: StateFlow<List<IncomeEntity>> =
        _incomes.asStateFlow()

    init {
        getIncomes()
    }

    private fun getIncomes() {

        viewModelScope.launch {

            repository.getAllIncomes().collect {

                _incomes.value = it
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

            repository.insertIncome(
                IncomeEntity(
                    amount = amount,
                    type = type,
                    description = description,
                    date = date,
                    time = time
                )
            )
        }
    }

    fun deleteIncome(id: Int) {

        viewModelScope.launch {

            repository.deleteIncome(id)
        }
    }
}