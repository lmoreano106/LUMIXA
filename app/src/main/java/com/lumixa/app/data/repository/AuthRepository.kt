package com.lumixa.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.userProfileChangeRequest
class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository()
) {

    suspend fun register(
        email: String,
        password: String,
        fullName: String
    ): Result<Unit> {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val profileUpdates = userProfileChangeRequest {
                displayName = fullName
            }

            result.user?.updateProfile(profileUpdates)?.await()
            syncUserProfile()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            syncUserProfile()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginAdmin(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            syncUserProfile()

            val uid = firebaseAuth.currentUser?.uid
                ?: return Result.failure(
                    IllegalStateException("No se encontró el usuario autenticado.")
                )

            val profileSnapshot = firestore
                .collection("users")
                .document(uid)
                .collection("profile")
                .document("main")
                .get()
                .await()

            val role = profileSnapshot.getString("role")?.trim()?.lowercase()

            if (role == "admin") {
                Result.success(Unit)
            } else {
                firebaseAuth.signOut()
                Result.failure(IllegalAccessException("Acceso no autorizado."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(
        email: String
    ): Result<Unit> {
        return try {
            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    private suspend fun syncUserProfile() {
        val user = firebaseAuth.currentUser ?: return

        firestoreRepository.upsertUserProfile(
            userId = user.uid,
            uid = user.uid,
            email = user.email,
            displayName = user.displayName,
            currencySymbol = null,
            currencyCode = null
        )
    }
}
