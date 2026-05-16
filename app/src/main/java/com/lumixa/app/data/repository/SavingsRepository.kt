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

    fun getAllSavings(
        userId: String
    ): Flow<List<SavingsEntity>> {

        return savingsDao.getAllSavings(
            userId = userId
        )
    }

    fun getTotalSavings(
        userId: String
    ): Flow<Double?> {

        return savingsDao.getTotalSavings(
            userId = userId
        )
    }

    suspend fun getSavingByDate(
        userId: String,
        date: String
    ): SavingsEntity? {

        return savingsDao.getSavingByDate(
            userId = userId,
            date = date
        )
    }

    suspend fun updateSavingByDate(
        userId: String,
        amount: Double,
        date: String
    ) {

        savingsDao.updateSavingByDate(
            userId = userId,
            amount = amount,
            date = date
        )
    }
}