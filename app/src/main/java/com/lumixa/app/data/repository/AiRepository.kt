package com.lumixa.app.data.repository

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

data class FinancialContext(
    val totalIncome: Double,
    val totalExpenses: Double,
    val totalSavings: Double,
    val goalsSummary: String
)

class AiRepository(
    private val apiKey: String
) {

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    suspend fun askFinancialAssistant(
        question: String,
        context: FinancialContext
    ): String {
        return try {
            Log.d("AiRepository", "API key length: ${apiKey.length}")

            val systemPrompt = """
                Eres LUMIXA IA, asistente financiero para estudiantes.
                Responde SIEMPRE en español, tono amigable y profesional.
                Reglas de estilo obligatorias:
                1) Respuesta corta y clara (máximo 4 puntos).
                2) No uses markdown ni símbolos como **texto**.
                3) Usa emojis suaves cuando ayuden (por ejemplo: 🙂, 💡, 📊, ✅).
                4) Si hay datos financieros del usuario, menciónalos con números concretos.
                5) Si faltan datos suficientes, indícalo brevemente y da consejo general útil.
                6) No des recomendaciones riesgosas ni promesas de rentabilidad.
            """.trimIndent()

            val userPrompt = """
                Pregunta del usuario:
                $question

                Contexto financiero local del usuario:
                - Ingresos totales: ${context.totalIncome}
                - Gastos totales: ${context.totalExpenses}
                - Ahorros totales: ${context.totalSavings}
                - Metas: ${context.goalsSummary}

                Entrega una respuesta accionable y breve respetando todas las reglas.
            """.trimIndent()

            val response = model.generateContent(
                content {
                    text(systemPrompt)
                    text(userPrompt)
                }
            )

            response.text?.trim().orEmpty().ifBlank {
                "No pude generar una respuesta en este momento. Intenta de nuevo."
            }
        } catch (e: Exception) {
            Log.e("AiRepository", "Error llamando a Gemini", e)
            "No se pudo obtener respuesta de IA. Verifica tu conexión e intenta de nuevo."
        }
    }
}
