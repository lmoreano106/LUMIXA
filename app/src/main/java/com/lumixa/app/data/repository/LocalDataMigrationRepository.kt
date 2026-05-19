package com.lumixa.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.lumixa.app.data.local.dao.ExpenseDao
import com.lumixa.app.data.local.dao.GoalDao
import com.lumixa.app.data.local.dao.IncomeDao
import com.lumixa.app.data.local.dao.SavingsDao
import kotlinx.coroutines.flow.first

class LocalDataMigrationRepository(
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao,
    private val goalDao: GoalDao,
    private val savingsDao: SavingsDao,
    private val firestoreRepository: FirestoreRepository,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun migrateCurrentUserDataToFirestore() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        migrateExpenses(userId)
        migrateIncomes(userId)
        migrateGoals(userId)
        migrateSavings(userId)
    }

    private suspend fun migrateExpenses(userId: String) {
        val existingIds = firestoreRepository.getDocumentIds(userId, "expenses")
        val localExpenses = expenseDao.getAllExpenses(userId).first()

        localExpenses
            .filter { expense -> !existingIds.contains(expense.id.toString()) }
            .forEach { expense ->
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

    private suspend fun migrateIncomes(userId: String) {
        val existingIds = firestoreRepository.getDocumentIds(userId, "incomes")
        val localIncomes = incomeDao.getAllIncomes(userId).first()

        localIncomes
            .filter { income -> !existingIds.contains(income.id.toString()) }
            .forEach { income ->
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

    private suspend fun migrateGoals(userId: String) {
        val existingIds = firestoreRepository.getDocumentIds(userId, "goals")
        val localGoals = goalDao.getAllGoals(userId).first()

        localGoals
            .filter { goal -> !existingIds.contains(goal.id.toString()) }
            .forEach { goal ->
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

    private suspend fun migrateSavings(userId: String) {
        val existingIds = firestoreRepository.getDocumentIds(userId, "savings")
        val localSavings = savingsDao.getAllSavings(userId).first()

        localSavings
            .filter { saving -> !existingIds.contains(saving.id.toString()) }
            .forEach { saving ->
                firestoreRepository.upsertSaving(
                    userId = userId,
                    savingId = saving.id,
                    amount = saving.amount,
                    date = saving.date
                )
            }
    }
}
