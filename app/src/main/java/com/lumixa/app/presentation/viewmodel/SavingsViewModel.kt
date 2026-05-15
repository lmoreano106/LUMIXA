package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumixa.app.data.local.entity.SavingsEntity
import com.lumixa.app.data.repository.SavingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavingsViewModel(
    private val repository: SavingsRepository
) : ViewModel() {

    private val _savings =
        MutableStateFlow<List<SavingsEntity>>(emptyList())

    val savings: StateFlow<List<SavingsEntity>> =
        _savings.asStateFlow()

    init {
        getSavings()
    }

    private fun getSavings() {
        viewModelScope.launch {
            repository.getAllSavings().collect { savingsList ->
                _savings.value = savingsList
            }
        }
    }

    fun saveDailySaving(
        amount: Double,
        date: String
    ) {
        viewModelScope.launch {
            val validAmount = amount.coerceAtLeast(0.0)

            val existingSaving = repository.getSavingByDate(date)

            if (existingSaving == null) {
                if (validAmount > 0) {
                    repository.insertSaving(
                        SavingsEntity(
                            amount = validAmount,
                            date = date
                        )
                    )
                }
            } else {
                repository.updateSavingByDate(
                    amount = validAmount,
                    date = date
                )
            }
        }
    }
}