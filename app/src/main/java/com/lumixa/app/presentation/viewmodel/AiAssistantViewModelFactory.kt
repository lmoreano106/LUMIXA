package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lumixa.app.data.repository.AiRepository

class AiAssistantViewModelFactory(
    private val repository: AiRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(AiAssistantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AiAssistantViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}