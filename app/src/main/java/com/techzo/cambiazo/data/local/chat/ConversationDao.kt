package com.techzo.cambiazo.data.local.chat

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Query("SELECT * FROM conversations ORDER BY lastUpdatedAt DESC")
    fun observeAll(): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ConversationEntity)

    @Query("""
        UPDATE conversations
        SET lastMessagePreview = :preview,
            lastUpdatedAt      = :updatedAt,
            unreadCount        = unreadCount + :unreadInc,
            exchangeId         = COALESCE(:exchangeId, exchangeId)
        WHERE conversationId   = :cid
    """)
    suspend fun bumpConversation(
        cid: String,
        preview: String,
        updatedAt: Long,
        unreadInc: Int,
        exchangeId: String? // ← NUEVO
    )
}
