package com.lumixa.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun resolveUserId(
        userId: String
    ): String {
        return auth.currentUser?.uid ?: userId
    }

    suspend fun upsertUserProfile(
        userId: String,
        uid: String,
        email: String?,
        displayName: String?,
        currencySymbol: String?,
        currencyCode: String?
    ) {
        val profileData = mutableMapOf<String, Any>(
            "uid" to uid
        )

        if (!email.isNullOrBlank()) {
            profileData["email"] = email
        }

        if (!displayName.isNullOrBlank()) {
            profileData["displayName"] = displayName
        }

        if (!currencySymbol.isNullOrBlank()) {
            profileData["currencySymbol"] = currencySymbol
        }

        if (!currencyCode.isNullOrBlank()) {
            profileData["currencyCode"] = currencyCode
        }

        suspendCoroutine<Unit> { continuation ->
            firestore
                .collection("users")
                .document(userId)
                .collection("profile")
                .document("main")
                .set(profileData)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

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

        val resolvedUserId = resolveUserId(userId)

        suspendCoroutine<Unit> { continuation ->
            firestore
                .collection("users")
                .document(resolvedUserId)
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

        val resolvedUserId = resolveUserId(userId)

        suspendCoroutine<Unit> { continuation ->
            firestore
                .collection("users")
                .document(resolvedUserId)
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

    suspend fun upsertSaving(
        userId: String,
        savingId: Int,
        amount: Double,
        date: String
    ) {
        val savingData = mapOf(
            "id" to savingId,
            "userId" to userId,
            "amount" to amount,
            "date" to date
        )

        val resolvedUserId = resolveUserId(userId)

        suspendCoroutine<Unit> { continuation ->
            firestore
                .collection("users")
                .document(resolvedUserId)
                .collection("savings")
                .document(savingId.toString())
                .set(savingData)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    suspend fun deleteSaving(
        userId: String,
        savingId: Int
    ) {
        suspendCoroutine<Unit> { continuation ->
            firestore
                .collection("users")
                .document(userId)
                .collection("savings")
                .document(savingId.toString())
                .delete()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    suspend fun getDocumentIds(
        userId: String,
        collection: String
    ): Set<String> {
        val snapshot = firestore
            .collection("users")
            .document(userId)
            .collection(collection)
            .get()
            .await()

        return snapshot.documents
            .map { document -> document.id }
            .toSet()
    }
}
