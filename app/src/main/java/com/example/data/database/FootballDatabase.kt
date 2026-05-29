package com.example.data.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.models.Match
import com.example.data.models.StandingItem
import com.example.data.models.NewsArticle
import com.example.data.models.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface FootballDao {
    // Matches
    @Query("SELECT * FROM matches")
    fun getAllMatchesFlow(): Flow<List<Match>>

    @Query("SELECT * FROM matches")
    suspend fun getAllMatches(): List<Match>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<Match>)

    // Standings
    @Query("SELECT * FROM standings ORDER BY position ASC")
    fun getStandingsFlow(): Flow<List<StandingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStandings(standings: List<StandingItem>)

    // News
    @Query("SELECT * FROM news_articles")
    fun getNewsFlow(): Flow<List<NewsArticle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsArticle>)

    // Chat
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getChatMessagesFlow(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    suspend fun getChatMessages(): List<ChatMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChat()
}

@Database(
    entities = [Match::class, StandingItem::class, NewsArticle::class, ChatMessage::class],
    version = 1,
    exportSchema = false
)
abstract class FootballDatabase : RoomDatabase() {
    abstract fun footballDao(): FootballDao

    companion object {
        @Volatile
        private var INSTANCE: FootballDatabase? = null

        fun getDatabase(context: Context): FootballDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FootballDatabase::class.java,
                    "football_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
