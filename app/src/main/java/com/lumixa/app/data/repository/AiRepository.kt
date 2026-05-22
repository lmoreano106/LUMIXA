package com.lumixa.app.data.repository

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

import com.lumixa.app.data.local.entity.ExpenseEntity
import com.lumixa.app.data.local.entity.GoalEntity
import com.lumixa.app.data.local.entity.IncomeEntity
import com.lumixa.app.data.local.entity.SavingsEntity
import kotlin.math.roundToInt

data class ExpensePredictionResult(
    val riskLevel: String,
    val prediction: String,
    val recommendation: String
)

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

    suspend fun generateDashboardInsight(
        context: FinancialContext
    ): String {
        return try {
            val systemPrompt = """
                Eres LUMIXA IA, asistente financiero para estudiantes.
                Responde SIEMPRE en español, en máximo 2 líneas y máximo 140 caracteres.
                Entrega un consejo puntual, accionable y seguro.
                No uses markdown.
            """.trimIndent()

            val userPrompt = """
                Genera 1 consejo financiero breve para el dashboard.
                Datos:
                - Ingresos totales: ${context.totalIncome}
                - Gastos totales: ${context.totalExpenses}
                - Ahorros totales: ${context.totalSavings}
                - Metas: ${context.goalsSummary}
            """.trimIndent()

            val response = model.generateContent(
                content {
                    text(systemPrompt)
                    text(userPrompt)
                }
            )

            response.text?.trim().orEmpty().ifBlank {
                "Controla tus gastos diarios y reserva una parte fija para tus metas."
            }
        } catch (e: Exception) {
            Log.e("AiRepository", "Error generando insight de dashboard", e)
            "Controla tus gastos diarios y reserva una parte fija para tus metas."
        }
    }

    suspend fun generateGoalAnalysis(
        goalName: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDate: String,
        totalIncome: Double,
        totalExpenses: Double,
        totalSavings: Double
    ): String {
        return try {
            val systemPrompt = """
                Eres LUMIXA IA, asistente financiero para estudiantes.
                Responde SIEMPRE en español.
                Entrega exactamente 4 líneas (sin markdown):
                1) Alcanzable: Sí/No + motivo breve.
                2) Faltante: monto exacto.
                3) Ahorro sugerido: diario y semanal.
                4) Recomendación breve y segura.
            """.trimIndent()

            val userPrompt = """
                Analiza esta meta:
                - Nombre: $goalName
                - Monto objetivo: $targetAmount
                - Ahorro acumulado: $savedAmount
                - Fecha objetivo: $targetDate
                - Ingresos totales: $totalIncome
                - Gastos totales: $totalExpenses
                - Ahorros totales: $totalSavings
            """.trimIndent()

            val response = model.generateContent(
                content {
                    text(systemPrompt)
                    text(userPrompt)
                }
            )

            response.text?.trim().orEmpty().ifBlank {
                "No fue posible generar el análisis inteligente en este momento."
            }
        } catch (e: Exception) {
            Log.e("AiRepository", "Error generando análisis de meta", e)
            "No fue posible generar el análisis inteligente en este momento."
        }
    }


    suspend fun generateExpensePrediction(
        expenses: List<ExpenseEntity>,
        incomes: List<IncomeEntity>,
        savings: List<SavingsEntity>,
        goals: List<GoalEntity>
    ): ExpensePredictionResult? {
        return try {
            val totalExpenses = expenses.sumOf { it.amount }
            val totalIncomes = incomes.sumOf { it.amount }
            val totalSavings = savings.sumOf { it.amount }
            val goalsSummary = if (goals.isEmpty()) "Sin metas registradas" else goals.joinToString { "${it.name}: ${it.savedAmount}/${it.targetAmount}" }

            val systemPrompt = """
                Eres LUMIXA IA. Responde SIEMPRE en español con exactamente 3 líneas sin markdown:
                1) Riesgo: bajo/moderado/alto.
                2) Predicción: Si mantienes este ritmo, gastarías aproximadamente X este mes.
                3) Recomendación: Reduce gastos en la categoría más alta.
            """.trimIndent()

            val topCategory = expenses.groupBy { it.category }.maxByOrNull { it.value.sumOf { e -> e.amount } }?.key ?: "Sin categoría"

            val userPrompt = """
                Datos del usuario:
                - Gastos (${expenses.size}): total $totalExpenses
                - Ingresos (${incomes.size}): total $totalIncomes
                - Ahorros (${savings.size}): total $totalSavings
                - Metas: $goalsSummary
                - Categoría de mayor gasto: $topCategory

                Incluye una predicción numérica mensual razonable según el ritmo observado.
            """.trimIndent()

            val response = model.generateContent(
                content {
                    text(systemPrompt)
                    text(userPrompt)
                }
            )

            val text = response.text?.trim().orEmpty()
            if (text.isBlank()) return null

            val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
            val riskLine = lines.firstOrNull { it.contains("riesgo", ignoreCase = true) } ?: return null
            val predictionLine = lines.firstOrNull { it.contains("predicción", ignoreCase = true) || it.contains("gastarías", ignoreCase = true) } ?: return null
            val recommendationLine = lines.firstOrNull { it.contains("recomendación", ignoreCase = true) || it.contains("reduce", ignoreCase = true) } ?: return null

            val risk = when {
                riskLine.contains("alto", ignoreCase = true) -> "alto"
                riskLine.contains("bajo", ignoreCase = true) -> "bajo"
                else -> "moderado"
            }

            ExpensePredictionResult(
                riskLevel = risk,
                prediction = predictionLine.substringAfter(":", predictionLine).trim(),
                recommendation = recommendationLine.substringAfter(":", recommendationLine).trim()
            )
        } catch (e: Exception) {
            Log.e("AiRepository", "Error generando predicción de gastos", e)
            null
        }
    }
}
