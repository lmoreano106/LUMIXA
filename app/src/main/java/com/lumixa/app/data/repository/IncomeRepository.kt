package com.lumixa.app.data.repository

import com.lumixa.app.data.local.dao.IncomeDao
import com.lumixa.app.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

class IncomeRepository(
    private val incomeDao: IncomeDao,
    private val firestoreRepository: FirestoreRepository
) {

    suspend fun insertIncome(
        income: IncomeEntity
    ) {
        val insertedId = incomeDao.insertIncome(income).toInt()

        firestoreRepository.upsertIncome(
            userId = income.userId,
            incomeId = insertedId,
            amount = income.amount,
            type = income.type,
            description = income.description,
            date = income.date,
            time = income.time
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

        firestoreRepository.deleteIncome(
            userId = userId,
            incomeId = incomeId
        )
    }
}
