package com.lumixa.app.data.repository

import com.lumixa.app.data.local.dao.IncomeDao
import com.lumixa.app.data.local.entity.IncomeEntity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.lumixa.app.data.remote.firebase.FirestoreRepository as RemoteFirestoreRepository
import kotlinx.coroutines.flow.Flow

class IncomeRepository(
    private val incomeDao: IncomeDao,
    private val firestoreRepository: FirestoreRepository,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val remoteFirestoreRepository: RemoteFirestoreRepository = RemoteFirestoreRepository()
) {

    suspend fun insertIncome(
        income: IncomeEntity
    ) {
        val insertedId = incomeDao.insertIncome(income).toInt()

        firestoreRepository.upsertIncome(
            userId = income.userId,
            incomeId = insertedId,
            amount = income.amount,
            type = income.type,
            description = income.description,
            date = income.date,
            time = income.time
        )
    }

    fun getAllIncomes(
        userId: String
    ): Flow<List<IncomeEntity>> {

        return incomeDao.getAllIncomes(
            userId = userId
        )
    }

    suspend fun deleteIncome(
        userId: String,
        incomeId: Int
    ) {
        incomeDao.deleteIncome(
            incomeId = incomeId
        )

        firestoreRepository.deleteIncome(
            userId = userId,
            incomeId = incomeId
        )
    }

    suspend fun syncIncomesFromFirestore() {
        val uid = firebaseAuth.currentUser?.uid ?: ""
        Log.d("SYNC", "UID actual: $uid")
        if (uid.isBlank()) return

        val items = remoteFirestoreRepository.fetchIncomes(uid)
        Log.d("SYNC", "Ingresos descargados: ${items.size}")

        items.forEach { incomeDao.insertIncome(it.copy(userId = uid)) }
    }
}
