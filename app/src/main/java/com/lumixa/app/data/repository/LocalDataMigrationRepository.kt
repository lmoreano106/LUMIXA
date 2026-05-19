package com.lumixa.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.lumixa.app.data.local.dao.ExpenseDao
import com.lumixa.app.data.local.dao.GoalDao
import com.lumixa.app.data.local.dao.IncomeDao
import com.lumixa.app.data.local.dao.SavingsDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.supervisorScope

class LocalDataMigrationRepository(
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao,
    private val goalDao: GoalDao,
    private val savingsDao: SavingsDao,
    private val firestoreRepository: FirestoreRepository,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun migrateCurrentUserDataToFirestore() = supervisorScope {
        val userId = firebaseAuth.currentUser?.uid ?: return@supervisorScope

        runCatching {
            expenseDao.getAllExpenses(userId).first().forEach { expense ->
                firestoreRepository.upsertExpense(
                    userId = userId,
                    expenseId = expense.id,
                    category = expense.category,
                    description = expense.description,
                    amount = expense.amount,
                    date = expense.date,
                    time = expense.time,
                    dayOfWeek = expense.dayOfWeek
                )
            }
        }

        runCatching {
            incomeDao.getAllIncomes(userId).first().forEach { income ->
                firestoreRepository.upsertIncome(
                    userId = userId,
                    incomeId = income.id,
                    amount = income.amount,
                    type = income.type,
                    description = income.description,
                    date = income.date,
                    time = income.time
                )
            }
        }

        runCatching {
            goalDao.getAllGoals(userId).first().forEach { goal ->
                firestoreRepository.upsertGoal(
                    userId = userId,
                    goalId = goal.id,
                    name = goal.name,
                    targetAmount = goal.targetAmount,
                    savedAmount = goal.savedAmount,
                    targetDate = goal.targetDate
                )
            }
        }

        runCatching {
            savingsDao.getAllSavings(userId).first().forEach { saving ->
                firestoreRepository.upsertSaving(
                    userId = userId,
                    savingId = saving.id,
                    amount = saving.amount,
                    date = saving.date
                )
            }
        }
    }
}