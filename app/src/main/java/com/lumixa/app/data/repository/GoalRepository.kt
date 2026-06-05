package com.lumixa.app.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.lumixa.app.data.local.dao.GoalDao
import com.lumixa.app.data.local.dao.GoalMovementDao
import com.lumixa.app.data.local.entity.GOAL_MOVEMENT_WITHDRAWAL
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.data.local.entity.GoalMovementEntity
import com.lumixa.app.data.remote.firebase.FirestoreRepository as RemoteFirestoreRepository
import kotlinx.coroutines.flow.Flow

class GoalRepository(
    private val goalDao: GoalDao,
    private val goalMovementDao: GoalMovementDao,
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

    suspend fun registerGoalWithdrawal(
        goal: GoalEntity,
        availableAmount: Double,
        amount: Double,
        description: String,
        date: String,
        time: String
    ): Result<Unit> {
        if (amount <= 0.0) {
            return Result.failure(IllegalArgumentException("El monto debe ser mayor a 0."))
        }

        if (amount > availableAmount) {
            return Result.failure(IllegalArgumentException("El monto supera el ahorro disponible de la meta."))
        }

        val updatedGoal = goal.copy(
            savedAmount = (availableAmount - amount).coerceAtLeast(0.0)
        )
        updateGoal(updatedGoal)

        val movement = GoalMovementEntity(
            goalId = goal.id,
            userId = goal.userId,
            amount = amount,
            description = description.ifBlank { "Uso de dinero de la meta" },
            type = GOAL_MOVEMENT_WITHDRAWAL,
            date = date,
            time = time
        )
        val insertedId = goalMovementDao.insertMovement(movement).toInt()
        val movementWithId = movement.copy(id = insertedId)

        firestoreRepository.upsertGoalMovement(
            userId = movementWithId.userId,
            movementId = movementWithId.id,
            goalId = movementWithId.goalId,
            amount = movementWithId.amount,
            description = movementWithId.description,
            type = movementWithId.type,
            date = movementWithId.date,
            time = movementWithId.time
        )

        return Result.success(Unit)
    }

    fun getAllGoals(
        userId: String
    ): Flow<List<GoalEntity>> {

        return goalDao.getAllGoals(
            userId = userId
        )
    }

    fun getGoalMovements(
        userId: String
    ): Flow<List<GoalMovementEntity>> {
        return goalMovementDao.getAllMovements(userId)
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

        val movements = remoteFirestoreRepository.fetchGoalMovements(uid)
        Log.d("SYNC", "Movimientos de meta descargados: ${movements.size}")

        movements.forEach { goalMovementDao.insertMovement(it.copy(userId = uid)) }
    }
}
