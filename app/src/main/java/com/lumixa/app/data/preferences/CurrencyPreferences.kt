package com.lumixa.app.data.preferences

import android.content.Context
import com.google.firebase.auth.FirebaseAuth

class CurrencyPreferences(
    private val context: Context
) {
    private val prefs = context.getSharedPreferences(
        "lumixa_currency_preferences",
        Context.MODE_PRIVATE
    )

    private fun currentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
    }

    fun saveCurrency(
        code: String,
        name: String,
        symbol: String
    ) {
        val userId = currentUserId()

        prefs.edit()
            .putString("${userId}_currency_code", code)
            .putString("${userId}_currency_name", name)
            .putString("${userId}_currency_symbol", symbol)
            .apply()
    }

    fun getCurrencyCode(): String {
        val userId = currentUserId()
        return prefs.getString("${userId}_currency_code", "COP") ?: "COP"
    }

    fun getCurrencyName(): String {
        val userId = currentUserId()
        return prefs.getString("${userId}_currency_name", "Pesos colombianos") ?: "Pesos colombianos"
    }

    fun getCurrencySymbol(): String {
        val userId = currentUserId()
        return prefs.getString("${userId}_currency_symbol", "$") ?: "$"
    }
}