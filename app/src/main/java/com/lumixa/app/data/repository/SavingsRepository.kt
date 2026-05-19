package com.lumixa.app.data.repository

import com.lumixa.app.data.local.dao.SavingsDao
import com.lumixa.app.data.local.entity.SavingsEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

class SavingsRepository(
    private val savingsDao: SavingsDao,
    private val firestoreRepository: FirestoreRepository,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun insertSaving(
        saving: SavingsEntity
    ) {
        val insertedId = savingsDao.insertSaving(saving).toInt()

        val currentUserId = firebaseAuth.currentUser?.uid ?: saving.userId
        val savingWithId = saving.copy(id = insertedId, userId = currentUserId)

        firestoreRepository.upsertSaving(
            userId = currentUserId,
            savingId = savingWithId.id,
            amount = savingWithId.amount,
            date = savingWithId.date
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
            val currentUserId = firebaseAuth.currentUser?.uid ?: userId
            firestoreRepository.upsertSaving(
                userId = currentUserId,
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

        val currentUserId = firebaseAuth.currentUser?.uid ?: userId
        firestoreRepository.deleteSaving(
            userId = currentUserId,
            savingId = savingId
        )
    }
}
