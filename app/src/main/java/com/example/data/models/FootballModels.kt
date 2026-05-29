package com.example.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Team(
    val id: String,
    val name: String,
    val shortName: String,
    val logoAsset: String, // String representation or placeholder color hex
    val primaryColorHex: String
)

@JsonClass(generateAdapter = true)
data class MatchEvent(
    val id: String,
    val minute: Int,
    val type: EventType,
    val teamId: String,
    val playerName: String,
    val secondaryPlayerName: String? = null, // Assist or player coming out
    val details: String = ""
)

enum class EventType {
    GOAL,
    YELLOW_CARD,
    RED_CARD,
    SUBSTITUTION,
    PENALTY_GOAL,
    CORNER,
    SHOT_ON_TARGET,
    FOUL
}

@JsonClass(generateAdapter = true)
data class MatchStats(
    val possessionHome: Int = 50,
    val possessionAway: Int = 50,
    val shotsHome: Int = 0,
    val shotsAway: Int = 0,
    val shotsOnTargetHome: Int = 0,
    val shotsOnTargetAway: Int = 0,
    val foulsHome: Int = 0,
    val foulsAway: Int = 0,
    val cornersHome: Int = 0,
    val cornersAway: Int = 0,
    val offsidesHome: Int = 0,
    val offsidesAway: Int = 0,
    val yellowCardsHome: Int = 0,
    val yellowCardsAway: Int = 0,
    val redCardsHome: Int = 0,
    val redCardsAway: Int = 0
)

@JsonClass(generateAdapter = true)
data class PlayerPosition(
    val name: String,
    val number: Int,
    val role: String, // "GK", "DF", "MF", "FW"
    val xGrid: Float, // For tactical line-up visual coordinates (0-1)
    val yGrid: Float
)

@JsonClass(generateAdapter = true)
data class Lineups(
    val homeLineup: List<PlayerPosition>,
    val awayLineup: List<PlayerPosition>
)

@JsonClass(generateAdapter = true)
data class CommentaryItem(
    val id: String,
    val minute: Int,
    val text: String,
    val isCritical: Boolean = false, // True for goals, cards, etc.
    val type: EventType? = null
)

@JsonClass(generateAdapter = true)
data class FootballMatch(
    val id: String,
    val league: String,
    val homeTeam: Team,
    val awayTeam: Team,
    var homeScore: Int,
    var awayScore: Int,
    var status: String, // "LIVE", "FT", "UPCOMING", "HT"
    var minute: Int,
    val events: MutableList<MatchEvent> = mutableListOf(),
    var stats: MatchStats = MatchStats(),
    val lineups: Lineups,
    val commentary: MutableList<CommentaryItem> = mutableListOf(),
    val videoStreamUrl: String = "simulated_stream_uid_01",
    var isBookmarked: Boolean = false
)

@JsonClass(generateAdapter = true)
data class StandingEntry(
    val rank: Int,
    val team: Team,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val points: Int
)

@JsonClass(generateAdapter = true)
data class SoccerNews(
    val id: String,
    val title: String,
    val category: String, // "Transfer", "Match Report", "Injury"
    val timeAgo: String,
    val content: String,
    val author: String,
    val bannerColorHex: String
)
