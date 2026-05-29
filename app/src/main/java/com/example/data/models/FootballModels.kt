package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Team(
    val id: String,
    val name: String,
    val logoUrl: String = ""
)

data class MatchStats(
    val possessionHome: Int = 50,
    val possessionAway: Int = 50,
    val shotsHome: Int = 0,
    val shotsAway: Int = 0,
    val shotsOnTargetHome: Int = 0,
    val shotsOnTargetAway: Int = 0,
    val cornersHome: Int = 0,
    val cornersAway: Int = 0,
    val foulsHome: Int = 0,
    val foulsAway: Int = 0,
    val yellowCardsHome: Int = 0,
    val yellowCardsAway: Int = 0,
    val redCardsHome: Int = 0,
    val redCardsAway: Int = 0
)

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey val id: String,
    val homeTeamName: String,
    val awayTeamName: String,
    val homeScore: Int,
    val awayScore: Int,
    val status: String, // "LIVE", "FT", "SCHEDULED"
    val timeMinutes: Int,
    val possessionHome: Int = 50,
    val possessionAway: Int = 50,
    val shotsHome: Int = 12,
    val shotsAway: Int = 8,
    val shotsOnTargetHome: Int = 5,
    val shotsOnTargetAway: Int = 3,
    val cornersHome: Int = 4,
    val cornersAway: Int = 2,
    val foulsHome: Int = 9,
    val foulsAway: Int = 11,
    val yellowCardsHome: Int = 1,
    val yellowCardsAway: Int = 2,
    val redCardsHome: Int = 0,
    val redCardsAway: Int = 0,
    val formationHome: String = "4-3-3",
    val formationAway: String = "4-2-3-1"
) {
    val homeTeam: Team get() = Team(homeTeamName.lowercase().replace(" ", ""), homeTeamName)
    val awayTeam: Team get() = Team(awayTeamName.lowercase().replace(" ", ""), awayTeamName)
    val stats: MatchStats get() = MatchStats(
        possessionHome = possessionHome,
        possessionAway = possessionAway,
        shotsHome = shotsHome,
        shotsAway = shotsAway,
        shotsOnTargetHome = shotsOnTargetHome,
        shotsOnTargetAway = shotsOnTargetAway,
        cornersHome = cornersHome,
        cornersAway = cornersAway,
        foulsHome = foulsHome,
        foulsAway = foulsAway,
        yellowCardsHome = yellowCardsHome,
        yellowCardsAway = yellowCardsAway,
        redCardsHome = redCardsHome,
        redCardsAway = redCardsAway
    )
}

@Entity(tableName = "standings")
data class StandingItem(
    @PrimaryKey val position: Int,
    val teamName: String,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val points: Int,
    val goalsFor: Int,
    val goalsAgainst: Int
) {
    val team: Team get() = Team(teamName.lowercase().replace(" ", ""), teamName)
    val goalDifference: Int get() = goalsFor - goalsAgainst
}

@Entity(tableName = "news_articles")
data class NewsArticle(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val summary: String,
    val source: String,
    val date: String,
    val imageUrl: String = ""
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val message: String,
    val isAi: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
