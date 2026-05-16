package com.lumixa.app.data.repository

import com.lumixa.app.data.local.dao.ExpenseDao
import com.lumixa.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao
) {
    suspend fun insertExpense(
        expense: ExpenseEntity
    ) {
        expenseDao.insertExpense(expense)
    }

    fun getAllExpenses(
        userId: String
    ): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses(
            userId = userId
        )
    }

    suspend fun deleteExpense(
        expenseId: Int
    ) {
        expenseDao.deleteExpense(
            expenseId = expenseId
        )
    }
}