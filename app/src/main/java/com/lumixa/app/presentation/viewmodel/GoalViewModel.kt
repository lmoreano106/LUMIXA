package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    private val _goals =
        MutableStateFlow<List<GoalEntity>>(emptyList())

    val goals: StateFlow<List<GoalEntity>> =
        _goals.asStateFlow()

    init {
        getGoals()
    }

    private fun getGoals() {
        viewModelScope.launch {
            repository.getAllGoals().collect {
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
            repository.insertGoal(
                GoalEntity(
                    name = name,
                    targetAmount = targetAmount,
                    savedAmount = savedAmount,
                    targetDate = targetDate
                )
            )
        }
    }

    fun deleteGoal(id: Int) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }
}