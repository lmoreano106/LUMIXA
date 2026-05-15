package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lumixa.app.data.repository.IncomeRepository

class IncomeViewModelFactory(
    private val repository: IncomeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(IncomeViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")

            return IncomeViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}