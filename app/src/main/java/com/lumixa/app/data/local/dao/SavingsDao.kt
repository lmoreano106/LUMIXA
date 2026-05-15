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
    )

    @Query("SELECT * FROM savings ORDER BY id DESC")
    fun getAllSavings(): Flow<List<SavingsEntity>>

    @Query("SELECT SUM(amount) FROM savings")
    fun getTotalSavings(): Flow<Double?>

    @Query("SELECT * FROM savings WHERE date = :date LIMIT 1")
    suspend fun getSavingByDate(
        date: String
    ): SavingsEntity?
}