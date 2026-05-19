package com.lumixa.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.userProfileChangeRequest
class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
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
