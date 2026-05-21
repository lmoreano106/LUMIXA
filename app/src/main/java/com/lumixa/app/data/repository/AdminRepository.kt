package com.lumixa.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class AdminUserSummary(
    val uid: String,
    val email: String,
    val displayName: String,
    val role: String
)

data class AdminMetrics(
    val totalUsers: Int,
    val totalExpenses: Double,
    val totalGoals: Double,
    val totalExpenseRecords: Int,
    val totalGoalRecords: Int
)

data class AdminDashboardData(
    val users: List<AdminUserSummary>,
    val metrics: AdminMetrics
)

class AdminRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun isCurrentUserAdmin(uid: String): Boolean {
        val profileSnapshot = suspendCoroutine<com.google.firebase.firestore.DocumentSnapshot> { continuation ->
            firestore.collection("users")
                .document(uid)
                .collection("profile")
                .document("main")
                .get()
                .addOnSuccessListener { continuation.resume(it) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }

        val role = profileSnapshot.getString("role")?.trim()?.lowercase()
        return role == "admin"
    }

    suspend fun getAdminDashboardData(): AdminDashboardData {
        val usersSnapshot = suspendCoroutine<com.google.firebase.firestore.QuerySnapshot> { continuation ->
            firestore.collection("users")
                .get()
                .addOnSuccessListener { continuation.resume(it) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }

        val summaries = mutableListOf<AdminUserSummary>()
        var totalExpenses = 0.0
        var totalGoals = 0.0
        var totalExpenseRecords = 0
        var totalGoalRecords = 0

        for (userDoc in usersSnapshot.documents) {
            val uid = userDoc.id

            val profileSnapshot = suspendCoroutine<com.google.firebase.firestore.DocumentSnapshot> { continuation ->
                firestore.collection("users")
                    .document(uid)
                    .collection("profile")
                    .document("main")
                    .get()
                    .addOnSuccessListener { continuation.resume(it) }
                    .addOnFailureListener { continuation.resumeWithException(it) }
            }

            val email = profileSnapshot.getString("email") ?: "Sin correo"
            val displayName = profileSnapshot.getString("displayName") ?: "Usuario"
            val role = profileSnapshot.getString("role") ?: "user"

            summaries.add(
                AdminUserSummary(
                    uid = uid,
                    email = email,
                    displayName = displayName,
                    role = role
                )
            )

            val expensesSnapshot = suspendCoroutine<com.google.firebase.firestore.QuerySnapshot> { continuation ->
                firestore.collection("users")
                    .document(uid)
                    .collection("expenses")
                    .get()
                    .addOnSuccessListener { continuation.resume(it) }
                    .addOnFailureListener { continuation.resumeWithException(it) }
            }

            expensesSnapshot.documents.forEach { expenseDoc ->
                totalExpenses += expenseDoc.getDouble("amount") ?: 0.0
                totalExpenseRecords += 1
            }

            val goalsSnapshot = suspendCoroutine<com.google.firebase.firestore.QuerySnapshot> { continuation ->
                firestore.collection("users")
                    .document(uid)
                    .collection("goals")
                    .get()
                    .addOnSuccessListener { continuation.resume(it) }
                    .addOnFailureListener { continuation.resumeWithException(it) }
            }

            goalsSnapshot.documents.forEach { goalDoc ->
                totalGoals += goalDoc.getDouble("targetAmount") ?: 0.0
                totalGoalRecords += 1
            }
        }

        return AdminDashboardData(
            users = summaries.sortedBy { it.displayName.lowercase() },
            metrics = AdminMetrics(
                totalUsers = summaries.size,
                totalExpenses = totalExpenses,
                totalGoals = totalGoals,
                totalExpenseRecords = totalExpenseRecords,
                totalGoalRecords = totalGoalRecords
            )
        )
    }
}
