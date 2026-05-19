package com.lumixa.app.data.repository

import com.lumixa.app.data.local.dao.IncomeDao
import com.lumixa.app.data.local.entity.IncomeEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

class IncomeRepository(
    private val incomeDao: IncomeDao,
    private val firestoreRepository: FirestoreRepository,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun insertIncome(
        income: IncomeEntity
    ) {
        val insertedId = incomeDao.insertIncome(income).toInt()

        val currentUserId = firebaseAuth.currentUser?.uid ?: income.userId
        val incomeWithId = income.copy(id = insertedId, userId = currentUserId)

        firestoreRepository.upsertIncome(
            userId = currentUserId,
            incomeId = incomeWithId.id,
            amount = incomeWithId.amount,
            type = incomeWithId.type,
            description = incomeWithId.description,
            date = incomeWithId.date,
            time = incomeWithId.time
        )
    }

    fun getAllIncomes(
        userId: String
    ): Flow<List<IncomeEntity>> {

        return incomeDao.getAllIncomes(
            userId = userId
        )
    }

    suspend fun deleteIncome(
        userId: String,
        incomeId: Int
    ) {
        incomeDao.deleteIncome(
            incomeId = incomeId
        )

        val currentUserId = firebaseAuth.currentUser?.uid ?: userId
        firestoreRepository.deleteIncome(
            userId = currentUserId,
            incomeId = incomeId
        )
    }
}
