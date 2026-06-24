package com.example.chinesonline.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IdeogramStatDao {
    @Query("SELECT * FROM ideogram_stats WHERE gameType = :gameType")
    suspend fun getStatsByGameType(gameType: String): List<LocalIdeogramStat>

    @Query("SELECT * FROM ideogram_stats WHERE id = :id")
    suspend fun getStatById(id: String): LocalIdeogramStat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStat(stat: LocalIdeogramStat)
}
