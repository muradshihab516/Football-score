package com.example.data.database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- Room Entities ---

@Entity(tableName = "bookmarked_matches")
data class BookmarkedMatchEntity(
    @PrimaryKey val matchId: String,
    val homeTeamName: String,
    val awayTeamName: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "fan_chats")
data class FanChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val matchId: String,
    val user: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAi: Boolean = false,
    val avatarColorHex: String = "#00FF66"
)

// --- DAO Definitions ---

@Dao
interface FootballDao {
    // Bookmarks
    @Query("SELECT * FROM bookmarked_matches ORDER BY timestamp DESC")
    fun getBookmarkedMatches(): Flow<List<BookmarkedMatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkedMatchEntity)

    @Query("DELETE FROM bookmarked_matches WHERE matchId = :matchId")
    suspend fun removeBookmark(matchId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_matches WHERE matchId = :matchId)")
    suspend fun isBookmarked(matchId: String): Boolean

    // Fan Chats
    @Query("SELECT * FROM fan_chats WHERE matchId = :matchId ORDER BY timestamp ASC")
    fun getChatsForMatch(matchId: String): Flow<List<FanChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: FanChatMessageEntity)

    @Query("DELETE FROM fan_chats WHERE matchId = :matchId")
    suspend fun clearChatsForMatch(matchId: String)
}

// --- App Database Holder ---

@Database(
    entities = [BookmarkedMatchEntity::class, FanChatMessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun footballDao(): FootballDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "football_score_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- Repository Pattern Implementation ---

class FootballRepository(private val dao: FootballDao) {
    val bookmarkedMatches: Flow<List<BookmarkedMatchEntity>> = dao.getBookmarkedMatches()

    suspend fun addBookmark(matchId: String, home: String, away: String) {
        dao.addBookmark(BookmarkedMatchEntity(matchId, home, away))
    }

    suspend fun removeBookmark(matchId: String) {
        dao.removeBookmark(matchId)
    }

    suspend fun isBookmarked(matchId: String): Boolean {
        return dao.isBookmarked(matchId)
    }

    fun getChats(matchId: String): Flow<List<FanChatMessageEntity>> {
        return dao.getChatsForMatch(matchId)
    }

    suspend fun saveChatMessage(matchId: String, user: String, message: String, isAi: Boolean, avatarColor: String) {
        dao.insertChatMessage(
            FanChatMessageEntity(
                matchId = matchId,
                user = user,
                message = message,
                isAi = isAi,
                avatarColorHex = avatarColor
            )
        )
    }
}
