package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lumixa.app.data.repository.SavingsRepository

class SavingsViewModelFactory(
    private val repository: SavingsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(SavingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SavingsViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}