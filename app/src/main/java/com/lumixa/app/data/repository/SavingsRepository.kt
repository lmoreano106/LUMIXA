package com.lumixa.app.data.repository

import com.lumixa.app.data.local.dao.SavingsDao
import com.lumixa.app.data.local.entity.SavingsEntity
import kotlinx.coroutines.flow.Flow

class SavingsRepository(
    private val savingsDao: SavingsDao
) {
    suspend fun insertSaving(
        saving: SavingsEntity
    ) {
        savingsDao.insertSaving(saving)
    }

    fun getAllSavings(): Flow<List<SavingsEntity>> {
        return savingsDao.getAllSavings()
    }

    fun getTotalSavings(): Flow<Double?> {
        return savingsDao.getTotalSavings()
    }

    suspend fun getSavingByDate(
        date: String
    ): SavingsEntity? {
        return savingsDao.getSavingByDate(date)
    }
    suspend fun updateSavingByDate(
        amount: Double,
        date: String
    ) {
        savingsDao.updateSavingByDate(
            amount = amount,
            date = date
        )
    }
}