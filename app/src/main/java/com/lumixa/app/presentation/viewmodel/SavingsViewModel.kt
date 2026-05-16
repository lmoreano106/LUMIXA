package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lumixa.app.data.local.entity.SavingsEntity
import com.lumixa.app.data.repository.SavingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavingsViewModel(
    private val repository: SavingsRepository
) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private var savingsJob: Job? = null

    private val _savings =
        MutableStateFlow<List<SavingsEntity>>(emptyList())

    val savings: StateFlow<List<SavingsEntity>> =
        _savings.asStateFlow()

    init {
        refreshSavings()
    }

    private fun getCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid ?: ""
    }

    fun refreshSavings() {

        savingsJob?.cancel()

        val userId = getCurrentUserId()

        if (userId.isBlank()) {
            _savings.value = emptyList()
            return
        }

        savingsJob = viewModelScope.launch {

            repository.getAllSavings(userId).collect { savingsList ->

                _savings.value = savingsList
            }
        }
    }

    fun saveDailySaving(
        amount: Double,
        date: String
    ) {

        viewModelScope.launch {

            val userId = getCurrentUserId()

            if (userId.isBlank()) return@launch

            val validAmount =
                amount.coerceAtLeast(0.0)

            val existingSaving =
                repository.getSavingByDate(
                    userId = userId,
                    date = date
                )

            if (existingSaving == null) {

                if (validAmount > 0) {

                    repository.insertSaving(
                        SavingsEntity(
                            userId = userId,
                            amount = validAmount,
                            date = date
                        )
                    )
                }

            } else {

                repository.updateSavingByDate(
                    userId = userId,
                    amount = validAmount,
                    date = date
                )
            }

            refreshSavings()
        }
    }

    fun clearData() {
        savingsJob?.cancel()
        _savings.value = emptyList()
    }
}