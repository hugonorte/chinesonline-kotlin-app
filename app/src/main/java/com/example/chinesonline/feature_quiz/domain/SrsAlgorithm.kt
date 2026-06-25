package com.example.chinesonline.feature_quiz.domain

import kotlin.math.max

data class SrsResult(
    val interval: Int,
    val easeFactor: Float,
    val nextReviewAt: Long
)

object SrsAlgorithm {
    /**
     * Calcula o próximo intervalo de revisão baseado no algoritmo SuperMemo-2 (SM-2).
     * 
     * @param quality O nível de qualidade da resposta (0 a 5).
     *                Neste app: 5 = Acerto Perfeito, 0 = Erro Total.
     * @param previousInterval O intervalo anterior em dias.
     * @param previousEaseFactor O fator de facilidade anterior (padrão 2.5).
     * @return SrsResult contendo o novo intervalo, fator de facilidade e timestamp da próxima revisão.
     */
    fun calculateNextReview(quality: Int, previousInterval: Int, previousEaseFactor: Float): SrsResult {
        var interval: Int
        var easeFactor = previousEaseFactor

        if (quality >= 3) {
            // Resposta correta
            interval = when (previousInterval) {
                0 -> 1
                1 -> 6
                else -> (previousInterval * easeFactor).toInt()
            }
        } else {
            // Resposta incorreta
            interval = 0
        }

        easeFactor += (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
        easeFactor = max(1.3f, easeFactor) // Ease factor nunca pode ser menor que 1.3

        // Calcula o próximo timestamp (em milissegundos)
        val msInDay = 86400000L
        val now = System.currentTimeMillis()
        val nextReviewAt = if (interval == 0) now else now + (interval * msInDay)

        return SrsResult(interval, easeFactor, nextReviewAt)
    }
}
