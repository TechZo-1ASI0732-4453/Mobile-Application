package com.techzo.cambiazo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.techzo.cambiazo.data.local.chat.ChatMessageDao
import com.techzo.cambiazo.data.local.chat.ChatMessageEntity
import com.techzo.cambiazo.data.local.chat.ConversationDao
import com.techzo.cambiazo.data.local.chat.ConversationEntity
import com.techzo.cambiazo.domain.MessageType
import com.techzo.cambiazo.domain.SendStatus

class EnumConverters {
    @TypeConverter fun fromMessageType(v: MessageType): String = v.name
    @TypeConverter fun toMessageType(v: String): MessageType = MessageType.valueOf(v)

    @TypeConverter fun fromSendStatus(v: SendStatus): String = v.name
    @TypeConverter fun toSendStatus(v: String): SendStatus = SendStatus.valueOf(v)
}

@TypeConverters(EnumConverters::class)
@Database(
    entities = [
        FavoriteProductEntity::class,
        ChatMessageEntity::class,
        ConversationEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteProductDao(): FavoriteProductDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun conversationDao(): ConversationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cambiazo_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}