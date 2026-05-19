package com.lumixa.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumixa.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(
        expense: ExpenseEntity
    ): Long

    @Query(
        "SELECT * FROM expenses WHERE userId = :userId ORDER BY id DESC"
    )
    fun getAllExpenses(
        userId: String
    ): Flow<List<ExpenseEntity>>

    @Query(
        "DELETE FROM expenses WHERE id = :expenseId"
    )
    suspend fun deleteExpense(
        expenseId: Int
    )
}
