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
}
