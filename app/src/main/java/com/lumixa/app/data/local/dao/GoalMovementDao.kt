package com.lumixa.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumixa.app.data.local.entity.GoalMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalMovementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: GoalMovementEntity): Long

    @Query(
        "SELECT * FROM goal_movements WHERE userId = :userId ORDER BY id DESC"
    )
    fun getAllMovements(userId: String): Flow<List<GoalMovementEntity>>

    @Query(
        "SELECT * FROM goal_movements WHERE userId = :userId AND goalId = :goalId ORDER BY id DESC"
    )
    fun getMovementsForGoal(userId: String, goalId: Int): Flow<List<GoalMovementEntity>>
}
