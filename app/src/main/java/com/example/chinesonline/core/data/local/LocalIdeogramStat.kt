package com.example.chinesonline.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ideogram_stats")
data class LocalIdeogramStat(
    @PrimaryKey val id: String, // combinação de "${ideogramId}_${gameType}"
    val ideogramId: Int,
    val gameType: String,
    // Cache do Dicionário Orgânico
    val character: String,
    val pinyin: String,
    val translation: String,
    val salt: String,
    val hash: String,

    // Histórico
    var correctAttempts: Int,
    var wrongAttempts: Int,
    var lastReviewed: Long,
    
    // Algoritmo SM-2
    var interval: Int = 0,
    var easeFactor: Float = 2.5f,
    var nextReviewAt: Long = 0L
)
