package com.lumixa.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.data.local.entity.GoalMovementEntity
import com.lumixa.app.data.repository.GoalRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
    private var movementsJob: Job? = null

    private val _goals =
        MutableStateFlow<List<GoalEntity>>(emptyList())

    val goals: StateFlow<List<GoalEntity>> =
        _goals.asStateFlow()

    private val _goalMovements =
        MutableStateFlow<List<GoalMovementEntity>>(emptyList())

    val goalMovements: StateFlow<List<GoalMovementEntity>> =
        _goalMovements.asStateFlow()

    private val _goalWithdrawalError =
        MutableStateFlow<String?>(null)

    val goalWithdrawalError: StateFlow<String?> =
        _goalWithdrawalError.asStateFlow()

    init {
        refreshGoals()
        refreshGoalMovements()
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

    fun refreshGoalMovements() {
        movementsJob?.cancel()

        val userId = getCurrentUserId()

        if (userId.isBlank()) {
            _goalMovements.value = emptyList()
            return
        }

        movementsJob = viewModelScope.launch {
            repository.getGoalMovements(userId).collect {
                _goalMovements.value = it
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

    fun useGoalMoney(
        goal: GoalEntity,
        availableAmount: Double,
        amount: Double,
        description: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _goalWithdrawalError.value = null

            val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES"))
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val now = Date()

            val result = repository.registerGoalWithdrawal(
                goal = goal,
                availableAmount = availableAmount,
                amount = amount,
                description = description,
                date = dateFormatter.format(now),
                time = timeFormatter.format(now)
            )

            result.fold(
                onSuccess = {
                    refreshGoals()
                    refreshGoalMovements()
                    onSuccess()
                },
                onFailure = { error ->
                    _goalWithdrawalError.value = error.message ?: "No se pudo registrar el uso de la meta."
                }
            )
        }
    }

    fun clearGoalWithdrawalError() {
        _goalWithdrawalError.value = null
    }

    fun deleteGoal(id: Int) {

        viewModelScope.launch {

            repository.deleteGoal(id)

            refreshGoals()
        }
    }

    fun syncGoalsFromFirestore(onFinished: () -> Unit = {}) {
        viewModelScope.launch {
            repository.syncGoalsFromFirestore()
            refreshGoals()
            refreshGoalMovements()
            onFinished()
        }
    }

    fun clearData() {

        goalsJob?.cancel()
        movementsJob?.cancel()

        _goals.value = emptyList()
        _goalMovements.value = emptyList()
        _goalWithdrawalError.value = null
    }
}
