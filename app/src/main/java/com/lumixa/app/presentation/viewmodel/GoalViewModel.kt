package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.data.repository.GoalRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private var goalsJob: Job? = null

    private val _goals =
        MutableStateFlow<List<GoalEntity>>(emptyList())

    val goals: StateFlow<List<GoalEntity>> =
        _goals.asStateFlow()

    init {
        refreshGoals()
    }

    private fun getCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid ?: ""
    }

    fun refreshGoals() {

        goalsJob?.cancel()

        val userId = getCurrentUserId()

        if (userId.isBlank()) {
            _goals.value = emptyList()
            return
        }

        goalsJob = viewModelScope.launch {

            repository.getAllGoals(userId).collect {

                _goals.value = it
            }
        }
    }

    fun addGoal(
        name: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDate: String
    ) {

        viewModelScope.launch {

            val userId = getCurrentUserId()

            if (userId.isNotBlank()) {

                repository.insertGoal(
                    GoalEntity(
                        userId = userId,
                        name = name,
                        targetAmount = targetAmount,
                        savedAmount = savedAmount,
                        targetDate = targetDate
                    )
                )

                refreshGoals()
            }
        }
    }

    fun updateGoal(
        goal: GoalEntity
    ) {

        viewModelScope.launch {

            repository.updateGoal(goal)

            refreshGoals()
        }
    }

    fun deleteGoal(id: Int) {

        viewModelScope.launch {

            repository.deleteGoal(id)

            refreshGoals()
        }
    }

    fun clearData() {

        goalsJob?.cancel()

        _goals.value = emptyList()
    }
}