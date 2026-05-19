package com.lumixa.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lumixa.app.data.local.entity.SavingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaving(
        saving: SavingsEntity
    ): Long

    @Query(
        "SELECT * FROM savings WHERE userId = :userId ORDER BY id DESC"
    )
    fun getAllSavings(
        userId: String
    ): Flow<List<SavingsEntity>>

    @Query(
        "SELECT SUM(amount) FROM savings WHERE userId = :userId"
    )
    fun getTotalSavings(
        userId: String
    ): Flow<Double?>

    @Query(
        "SELECT * FROM savings WHERE userId = :userId AND date = :date LIMIT 1"
    )
    suspend fun getSavingByDate(
        userId: String,
        date: String
    ): SavingsEntity?

    @Query(
        "UPDATE savings SET amount = :amount WHERE userId = :userId AND date = :date"
    )
    suspend fun updateSavingByDate(
        userId: String,
        amount: Double,
        date: String
    )

    @Query(
        "DELETE FROM savings WHERE id = :savingId"
    )
    suspend fun deleteSavingById(
        savingId: Int
    )
}
