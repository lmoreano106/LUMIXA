package com.lumixa.app.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lumixa.app.data.local.entity.ExpenseEntity
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.data.local.entity.IncomeEntity
import com.lumixa.app.data.local.entity.SavingsEntity
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun userId(): String {
        return auth.currentUser?.uid ?: ""
    }

    private fun userDocument() =
        firestore.collection("users").document(userId())

    suspend fun saveIncome(income: IncomeEntity) {
        val uid = userId()
        if (uid.isBlank()) return

        userDocument()
            .collection("incomes")
            .document(income.id.toString())
            .set(income)
            .await()
    }

    suspend fun saveExpense(expense: ExpenseEntity) {
        val uid = userId()
        if (uid.isBlank()) return

        userDocument()
            .collection("expenses")
            .document(expense.id.toString())
            .set(expense)
            .await()
    }

    suspend fun saveGoal(goal: GoalEntity) {
        val uid = userId()
        if (uid.isBlank()) return

        userDocument()
            .collection("goals")
            .document(goal.id.toString())
            .set(goal)
            .await()
    }

    suspend fun saveSaving(saving: SavingsEntity) {
        val uid = userId()
        if (uid.isBlank()) return

        userDocument()
            .collection("savings")
            .document(saving.id.toString())
            .set(saving)
            .await()
    }

    suspend fun saveCurrency(
        code: String,
        name: String,
        symbol: String
    ) {
        val uid = userId()
        if (uid.isBlank()) return

        userDocument()
            .collection("settings")
            .document("currency")
            .set(
                mapOf(
                    "code" to code,
                    "name" to name,
                    "symbol" to symbol
                )
            )
            .await()
    }

    suspend fun saveProfile(
        username: String,
        email: String
    ) {
        val uid = userId()
        if (uid.isBlank()) return

        userDocument()
            .set(
                mapOf(
                    "userId" to uid,
                    "username" to username,
                    "email" to email
                )
            )
            .await()
    }
}