package com.lumixa.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumixa.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(
        goal: GoalEntity
    )

    @Query(
        "SELECT * FROM goals WHERE userId = :userId ORDER BY id DESC"
    )
    fun getAllGoals(
        userId: String
    ): Flow<List<GoalEntity>>

    @Query(
        "DELETE FROM goals WHERE id = :goalId"
    )
    suspend fun deleteGoal(
        goalId: Int
    )
}