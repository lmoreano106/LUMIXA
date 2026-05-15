package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lumixa.app.data.repository.GoalRepository

class GoalViewModelFactory(
    private val repository: GoalRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(GoalViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return GoalViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}