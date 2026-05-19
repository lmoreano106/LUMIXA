package com.lumixa.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumixa.app.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(
        income: IncomeEntity
    ): Long

    @Query(
        "SELECT * FROM incomes WHERE userId = :userId ORDER BY id DESC"
    )
    fun getAllIncomes(
        userId: String
    ): Flow<List<IncomeEntity>>

    @Query(
        "DELETE FROM incomes WHERE id = :incomeId"
    )
    suspend fun deleteIncome(
        incomeId: Int
    )
}
