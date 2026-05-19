package com.lumixa.app.data.repository

import com.lumixa.app.data.local.dao.GoalDao
import com.lumixa.app.data.local.entity.GoalEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

class GoalRepository(
    private val goalDao: GoalDao,
    private val firestoreRepository: FirestoreRepository,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun insertGoal(
        goal: GoalEntity
    ) {
        val insertedId = goalDao.insertGoal(goal).toInt()
        val currentUserId = firebaseAuth.currentUser?.uid ?: goal.userId
        val goalWithId = goal.copy(id = insertedId, userId = currentUserId)

        firestoreRepository.upsertGoal(
            userId = goalWithId.userId,
            goalId = goalWithId.id,
            name = goalWithId.name,
            targetAmount = goalWithId.targetAmount,
            savedAmount = goalWithId.savedAmount,
            targetDate = goalWithId.targetDate
        )
    }

    suspend fun updateGoal(
        goal: GoalEntity
    ) {
        goalDao.updateGoal(goal)
        val currentUserId = firebaseAuth.currentUser?.uid ?: goal.userId
        firestoreRepository.upsertGoal(
            userId = currentUserId,
            goalId = goal.id,
            name = goal.name,
            targetAmount = goal.targetAmount,
            savedAmount = goal.savedAmount,
            targetDate = goal.targetDate
        )
    }

    fun getAllGoals(
        userId: String
    ): Flow<List<GoalEntity>> {

        return goalDao.getAllGoals(
            userId = userId
        )
    }

    suspend fun deleteGoal(
        goalId: Int
    ) {
        val userId = firebaseAuth.currentUser?.uid ?: ""

        goalDao.deleteGoal(
            goalId = goalId
        )

        if (userId.isNotBlank()) {
            firestoreRepository.deleteGoal(
                userId = userId,
                goalId = goalId
            )
        }
    }
}
