package com.example.chinesonline.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ideogram_stats")
data class LocalIdeogramStat(
    @PrimaryKey val id: String, // combinação de "${ideogramId}_${gameType}"
    val ideogramId: Int,
    val gameType: String,
    var correctAttempts: Int,
    var wrongAttempts: Int,
    var lastReviewed: Long
)
