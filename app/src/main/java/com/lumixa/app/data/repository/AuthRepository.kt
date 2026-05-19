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
        fullName: String,
        currencyCode: String = "COP",
        currencySymbol: String = "$"
    ): Result<Unit> {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val profileUpdates = userProfileChangeRequest {
                displayName = fullName
            }

            result.user?.updateProfile(profileUpdates)?.await()
            syncUserProfile(
                currencyCode = currencyCode,
                currencySymbol = currencySymbol
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String,
        currencyCode: String = "COP",
        currencySymbol: String = "$"
    ): Result<Unit> {
        return try {
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            syncUserProfile(
                currencyCode = currencyCode,
                currencySymbol = currencySymbol
            )
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

    suspend fun syncCurrentUserProfile(
        currencyCode: String = "COP",
        currencySymbol: String = "$"
    ) {
        syncUserProfile(currencyCode = currencyCode, currencySymbol = currencySymbol)
    }

    private suspend fun syncUserProfile(
        currencyCode: String,
        currencySymbol: String
    ) {
        val user = firebaseAuth.currentUser ?: return

        firestoreRepository.upsertUserProfile(
            userId = user.uid,
            uid = user.uid,
            email = user.email,
            displayName = user.displayName,
            currencySymbol = currencySymbol,
            currencyCode = currencyCode
        )
    }
}
