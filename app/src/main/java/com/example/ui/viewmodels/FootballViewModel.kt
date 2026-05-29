package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.FootballDatabase
import com.example.data.models.ChatMessage
import com.example.data.models.Match
import com.example.data.models.NewsArticle
import com.example.data.models.StandingItem
import com.example.data.api.GeminiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FootballViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FootballDatabase.getDatabase(application)
    private val dao = db.footballDao()

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches = _matches.asStateFlow()

    private val _standings = MutableStateFlow<List<StandingItem>>(emptyList())
    val standings = _standings.asStateFlow()

    private val _news = MutableStateFlow<List<NewsArticle>>(emptyList())
    val news = _news.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages = _chatMessages.asStateFlow()

    private val _selectedMatch = MutableStateFlow<Match?>(null)
    val selectedMatch = _selectedMatch.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    private val _analysisText = MutableStateFlow<String?>(null)
    val analysisText = _analysisText.asStateFlow()

    init {
        // Load existing data or insert default initial dummy data if database is empty
        viewModelScope.launch {
            // Flow lists from DB to ViewModel State
            launch {
                dao.getAllMatchesFlow().collect {
                    _matches.value = it
                    if (_selectedMatch.value == null && it.isNotEmpty()) {
                        _selectedMatch.value = it.firstOrNull { m -> m.status == "LIVE" } ?: it.first()
                    }
                }
            }
            launch {
                dao.getStandingsFlow().collect { _standings.value = it }
            }
            launch {
                dao.getNewsFlow().collect { _news.value = it }
            }
            launch {
                dao.getChatMessagesFlow().collect { _chatMessages.value = it }
            }

            // Populate mock data if DB empty
            val matchesList = dao.getAllMatches()
            if (matchesList.isEmpty()) {
                populateInitialData()
            }
        }
    }

    private suspend fun populateInitialData() {
        val initialMatches = listOf(
            Match(
                id = "m1",
                homeTeamName = "Arsenal",
                awayTeamName = "Chelsea",
                homeScore = 2,
                awayScore = 1,
                status = "LIVE",
                timeMinutes = 78,
                possessionHome = 54,
                possessionAway = 46,
                shotsHome = 14,
                shotsAway = 9,
                shotsOnTargetHome = 6,
                shotsOnTargetAway = 4,
                cornersHome = 5,
                cornersAway = 3,
                foulsHome = 8,
                foulsAway = 12,
                yellowCardsHome = 1,
                yellowCardsAway = 3,
                formationHome = "4-3-3",
                formationAway = "4-2-3-1"
            ),
            Match(
                id = "m2",
                homeTeamName = "Manchester United",
                awayTeamName = "Liverpool",
                homeScore = 1,
                awayScore = 3,
                status = "LIVE",
                timeMinutes = 62,
                possessionHome = 42,
                possessionAway = 58,
                shotsHome = 8,
                shotsAway = 17,
                shotsOnTargetHome = 3,
                shotsOnTargetAway = 8,
                cornersHome = 2,
                cornersAway = 7,
                foulsHome = 11,
                foulsAway = 9,
                yellowCardsHome = 2,
                yellowCardsAway = 1,
                formationHome = "4-2-3-1",
                formationAway = "4-3-3"
            ),
            Match(
                id = "m3",
                homeTeamName = "Real Madrid",
                awayTeamName = "Manchester City",
                homeScore = 0,
                awayScore = 0,
                status = "SCHEDULED",
                timeMinutes = 0,
                possessionHome = 50,
                possessionAway = 50,
                formationHome = "4-3-1-2",
                formationAway = "3-2-4-1"
            ),
            Match(
                id = "m4",
                homeTeamName = "Bayern Munich",
                awayTeamName = "Dortmund",
                homeScore = 3,
                awayScore = 1,
                status = "FT",
                timeMinutes = 90,
                possessionHome = 57,
                possessionAway = 43,
                shotsHome = 16,
                shotsAway = 11,
                shotsOnTargetHome = 7,
                shotsOnTargetAway = 4,
                cornersHome = 6,
                cornersAway = 4,
                foulsHome = 10,
                foulsAway = 10,
                yellowCardsHome = 1,
                yellowCardsAway = 2,
                formationHome = "4-2-3-1",
                formationAway = "4-1-4-1"
            ),
            Match(
                id = "m5",
                homeTeamName = "Juventus",
                awayTeamName = "AC Milan",
                homeScore = 0,
                awayScore = 2,
                status = "FT",
                timeMinutes = 90,
                possessionHome = 49,
                possessionAway = 51,
                shotsHome = 10,
                shotsAway = 13,
                shotsOnTargetHome = 3,
                shotsOnTargetAway = 5,
                cornersHome = 4,
                cornersAway = 5,
                foulsHome = 13,
                foulsAway = 12,
                yellowCardsHome = 2,
                yellowCardsAway = 2,
                formationHome = "3-5-2",
                formationAway = "4-3-3"
            )
        )

        val initialStandings = listOf(
            StandingItem(1, "Arsenal", 35, 25, 6, 4, 81, 85, 28),
            StandingItem(2, "Manchester City", 35, 24, 7, 4, 79, 89, 32),
            StandingItem(3, "Liverpool", 35, 23, 8, 4, 77, 82, 36),
            StandingItem(4, "Aston Villa", 35, 20, 7, 8, 67, 73, 50),
            StandingItem(5, "Tottenham Spurs", 35, 18, 6, 11, 60, 69, 58),
            StandingItem(6, "Manchester United", 35, 17, 6, 12, 57, 52, 51),
            StandingItem(7, "Newcastle United", 35, 16, 5, 14, 53, 74, 57),
            StandingItem(8, "Chelsea", 35, 14, 9, 12, 51, 65, 59),
            StandingItem(9, "West Ham United", 35, 13, 10, 12, 49, 56, 68),
            StandingItem(10, "Bournemouth", 35, 13, 9, 13, 48, 52, 60)
        )

        val initialNews = listOf(
            NewsArticle(
                id = "n1",
                title = "Tactical Masterclass: How Midfield Shifts Redefined Today's Derby",
                summary = "An in-depth analysis of structural changes that led to the decisive winning goal, featuring heatmaps, full passing networks, and expert visual insight.",
                content = "The modern game is decided in transition. Today's match featured a masterclass in dynamic block shifting. The winning manager shifted from a standard double-pivot to an asymmetrical single pivot during high build-up phases, creating numeric overloads that overloaded the defensive half-spaces.",
                source = "ScoreStream Elite Analytics",
                date = "1 hour ago"
            ),
            NewsArticle(
                id = "n2",
                title = "Gemini AI Engine Predicts Record Break points for the Title Challenger Side",
                summary = "Our integrated sports AI model breaks down final match configurations, player workload constraints, and league momentum parameters to declare a 74% title probability.",
                content = "With only three fixtures remaining, the predictive model has converged. By combining historic team xG coefficients with weather projections and defensive fatigue metrics, the AI declares a high-probability win vector for the leaders in their final stadium visit.",
                source = "ScoreStream Intelligence",
                date = "3 hours ago"
            ),
            NewsArticle(
                id = "n3",
                title = "Transfer Alert: Star Winger Locked in Multi-Million Value Negotiations",
                summary = "Insiders report high-density progress in transfer talks between major English and Spanish clubs with contract term finalization expected by mid-June.",
                content = "Negotiation tables have heated up. The explosive player is reportedly seeking tactical alignment guarantees before putting pen to paper on what promises to be the summers biggest transfer saga.",
                source = "Derby Network",
                date = "5 hours ago"
            )
        )

        dao.insertMatches(initialMatches)
        dao.insertStandings(initialStandings)
        dao.insertNews(initialNews)

        // Add welcome message in chat
        dao.insertChatMessage(ChatMessage(message = "Hello! I am your ScoreStream AI Tactical Assistant. Select any active match and tap 'Analyse AI' or write your custom analytical queries here!", isAi = true))
    }

    fun selectMatch(match: Match) {
        _selectedMatch.value = match
        _analysisText.value = null // clear previous analysis
    }

    fun analyseMatchAI() {
        val currentMatch = _selectedMatch.value ?: return
        viewModelScope.launch {
            _isAnalyzing.value = true
            val prompt = """
                Analyze the following football match in high-density detail:
                Match: ${currentMatch.homeTeamName} vs ${currentMatch.awayTeamName}
                Score: ${currentMatch.homeScore} - ${currentMatch.awayScore}
                Status: ${currentMatch.status} (${currentMatch.timeMinutes}')
                Possession: ${currentMatch.possessionHome}% - ${currentMatch.possessionAway}%
                Formation: ${currentMatch.formationHome} vs ${currentMatch.formationAway}
                Shots: ${currentMatch.shotsHome} - ${currentMatch.shotsAway}
                Shots on Target: ${currentMatch.shotsOnTargetHome} - ${currentMatch.shotsOnTargetAway}
                Fouls: ${currentMatch.foulsHome} - ${currentMatch.foulsAway}
                
                Please provide tactical summaries, coaching recommendations, and critical insights in a sports analyst tone.
            """.trimIndent()

            val response = GeminiClient.generateAnalysis(prompt)
            _analysisText.value = response
            _isAnalyzing.value = false
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            // Save user msg
            val userMsg = ChatMessage(message = text, isAi = false)
            dao.insertChatMessage(userMsg)

            _isAnalyzing.value = true
            // Generate AI match response based on query
            val prompt = """
                You are the ScoreStream AI Tactical Soccer Assistant. Respond to the user's sports query dynamically.
                User Query: "$text"
                
                Provide a short, analytical, insightful response. Limit to 3 visual bullet-points and keep it crisp.
            """.trimIndent()

            val aiResponse = GeminiClient.generateAnalysis(prompt)
            val aiMsg = ChatMessage(message = aiResponse, isAi = true)
            dao.insertChatMessage(aiMsg)
            _isAnalyzing.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            dao.clearChat()
            dao.insertChatMessage(ChatMessage(message = "Chat history cleared. Send a message to start a new soccer analysis!", isAi = true))
        }
    }
}
