package com.lumixa.app.data.repository

import com.lumixa.app.data.local.dao.GoalDao
import com.lumixa.app.data.local.entity.GoalEntity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.lumixa.app.data.remote.firebase.FirestoreRepository as RemoteFirestoreRepository
import kotlinx.coroutines.flow.Flow

class GoalRepository(
    private val goalDao: GoalDao,
    private val firestoreRepository: FirestoreRepository,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val remoteFirestoreRepository: RemoteFirestoreRepository = RemoteFirestoreRepository()
) {

    suspend fun insertGoal(
        goal: GoalEntity
    ) {
        val insertedId = goalDao.insertGoal(goal).toInt()
        val goalWithId = goal.copy(id = insertedId)

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
        firestoreRepository.upsertGoal(
            userId = goal.userId,
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

    suspend fun syncGoalsFromFirestore() {
        val uid = firebaseAuth.currentUser?.uid ?: ""
        Log.d("SYNC", "UID actual: $uid")
        if (uid.isBlank()) return

        val items = remoteFirestoreRepository.fetchGoals(uid)
        Log.d("SYNC", "Metas descargadas: ${items.size}")

        items.forEach { goalDao.insertGoal(it.copy(userId = uid)) }
    }
}
