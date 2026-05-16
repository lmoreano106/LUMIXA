package com.lumixa.app.data.repository

import com.lumixa.app.data.local.dao.IncomeDao
import com.lumixa.app.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

class IncomeRepository(
    private val incomeDao: IncomeDao
) {

    suspend fun insertIncome(
        income: IncomeEntity
    ) {
        incomeDao.insertIncome(income)
    }

    fun getAllIncomes(
        userId: String
    ): Flow<List<IncomeEntity>> {

        return incomeDao.getAllIncomes(
            userId = userId
        )
    }

    suspend fun deleteIncome(
        incomeId: Int
    ) {
        incomeDao.deleteIncome(
            incomeId = incomeId
        )
    }
}