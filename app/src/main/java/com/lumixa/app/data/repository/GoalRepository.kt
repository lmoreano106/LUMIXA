package com.lumixa.app.data.repository

import com.lumixa.app.data.local.dao.GoalDao
import com.lumixa.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

class GoalRepository(
    private val goalDao: GoalDao
) {
    suspend fun insertGoal(goal: GoalEntity) {
        goalDao.insertGoal(goal)
    }

    fun getAllGoals(): Flow<List<GoalEntity>> {
        return goalDao.getAllGoals()
    }

    suspend fun deleteGoal(goalId: Int) {
        goalDao.deleteGoal(goalId)
    }
}