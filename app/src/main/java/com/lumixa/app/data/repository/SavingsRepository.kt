package com.lumixa.app.data.repository

import com.lumixa.app.data.local.dao.SavingsDao
import com.lumixa.app.data.local.entity.SavingsEntity
import kotlinx.coroutines.flow.Flow

class SavingsRepository(
    private val savingsDao: SavingsDao,
    private val firestoreRepository: FirestoreRepository
) {

    suspend fun insertSaving(
        saving: SavingsEntity
    ) {
        val insertedId = savingsDao.insertSaving(saving).toInt()

        firestoreRepository.upsertSaving(
            userId = saving.userId,
            savingId = insertedId,
            amount = saving.amount,
            date = saving.date
        )
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

        val updatedSaving = savingsDao.getSavingByDate(
            userId = userId,
            date = date
        )

        if (updatedSaving != null) {
            firestoreRepository.upsertSaving(
                userId = userId,
                savingId = updatedSaving.id,
                amount = updatedSaving.amount,
                date = updatedSaving.date
            )
        }
    }

    suspend fun deleteSaving(
        userId: String,
        savingId: Int
    ) {
        savingsDao.deleteSavingById(
            savingId = savingId
        )

        firestoreRepository.deleteSaving(
            userId = userId,
            savingId = savingId
        )
    }
}
