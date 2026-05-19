package com.lumixa.app.data.repository

import com.lumixa.app.data.local.dao.ExpenseDao
import com.lumixa.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val firestoreRepository: FirestoreRepository
) {
    suspend fun insertExpense(
        expense: ExpenseEntity
    ) {
        val insertedId = expenseDao.insertExpense(expense).toInt()

        firestoreRepository.upsertExpense(
            userId = expense.userId,
            expenseId = insertedId,
            category = expense.category,
            description = expense.description,
            amount = expense.amount,
            date = expense.date,
            time = expense.time,
            dayOfWeek = expense.dayOfWeek
        )
    }

    fun getAllExpenses(
        userId: String
    ): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses(
            userId = userId
        )
    }

    suspend fun deleteExpense(
        userId: String,
        expenseId: Int
    ) {
        expenseDao.deleteExpense(
            expenseId = expenseId
        )

        firestoreRepository.deleteExpense(
            userId = userId,
            expenseId = expenseId
        )
    }
}
