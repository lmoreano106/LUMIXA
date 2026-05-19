package com.lumixa.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirestoreRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun upsertExpense(
        userId: String,
        expenseId: Int,
        category: String,
        description: String,
        amount: Double,
        date: String,
        time: String,
        dayOfWeek: Int
    ) {
        val expenseData = mapOf(
            "id" to expenseId,
            "userId" to userId,
            "category" to category,
            "description" to description,
            "amount" to amount,
            "date" to date,
            "time" to time,
            "dayOfWeek" to dayOfWeek
        )

        suspendCoroutine<Unit> { continuation ->
            firestore
                .collection("users")
                .document(userId)
                .collection("expenses")
                .document(expenseId.toString())
                .set(expenseData)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    suspend fun deleteExpense(
        userId: String,
        expenseId: Int
    ) {
        suspendCoroutine<Unit> { continuation ->
            firestore
                .collection("users")
                .document(userId)
                .collection("expenses")
                .document(expenseId.toString())
                .delete()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    suspend fun upsertIncome(
        userId: String,
        incomeId: Int,
        amount: Double,
        type: String,
        description: String,
        date: String,
        time: String
    ) {
        val incomeData = mapOf(
            "id" to incomeId,
            "userId" to userId,
            "amount" to amount,
            "type" to type,
            "description" to description,
            "date" to date,
            "time" to time
        )

        suspendCoroutine<Unit> { continuation ->
            firestore
                .collection("users")
                .document(userId)
                .collection("incomes")
                .document(incomeId.toString())
                .set(incomeData)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    suspend fun deleteIncome(
        userId: String,
        incomeId: Int
    ) {
        suspendCoroutine<Unit> { continuation ->
            firestore
                .collection("users")
                .document(userId)
                .collection("incomes")
                .document(incomeId.toString())
                .delete()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    suspend fun upsertGoal(
        userId: String,
        goalId: Int,
        name: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDate: String
    ) {
        val goalData = mapOf(
            "id" to goalId,
            "userId" to userId,
            "name" to name,
            "targetAmount" to targetAmount,
            "savedAmount" to savedAmount,
            "targetDate" to targetDate
        )

        suspendCoroutine<Unit> { continuation ->
            firestore
                .collection("users")
                .document(userId)
                .collection("goals")
                .document(goalId.toString())
                .set(goalData)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    suspend fun deleteGoal(
        userId: String,
        goalId: Int
    ) {
        suspendCoroutine<Unit> { continuation ->
            firestore
                .collection("users")
                .document(userId)
                .collection("goals")
                .document(goalId.toString())
                .delete()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
}
