package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.BookmarkedMatchEntity
import com.example.data.database.FanChatMessageEntity
import com.example.data.database.FootballRepository
import com.example.data.models.*
import com.example.data.api.RetrofitGeminiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class FootballViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FootballRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = FootballRepository(database.footballDao())
    }

    // --- In-Memory State for Soccer Matches ---
    private val _matches = MutableStateFlow<List<FootballMatch>>(emptyList())
    val matches: StateFlow<List<FootballMatch>> = _matches.asStateFlow()

    private val _selectedMatchId = MutableStateFlow<String?>(null)
    val selectedMatchId: StateFlow<String?> = _selectedMatchId.asStateFlow()

    val selectedMatch: StateFlow<FootballMatch?> = combine(matches, selectedMatchId) { list, id ->
        list.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current Chats (combined local Room database persistent chats + fresh live items)
    val activeChats: StateFlow<List<FanChatMessageEntity>> = selectedMatchId.flatMapLatest { matchId ->
        if (matchId != null) {
            repository.getChats(matchId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Bookmarking State ---
    val bookmarkedMatchIds: StateFlow<Set<String>> = repository.bookmarkedMatches
        .map { list -> list.map { it.matchId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // --- AI Analysis State ---
    private val _aiAnalyses = MutableStateFlow<Map<String, String>>(emptyMap())
    val aiAnalyses: StateFlow<Map<String, String>> = _aiAnalyses.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    // --- Live Stream Display Settings ---
    private val _isPlayingStream = MutableStateFlow(true)
    val isPlayingStream: StateFlow<Boolean> = _isPlayingStream.asStateFlow()

    private val _streamQuality = MutableStateFlow("1080p AD-FREE")
    val streamQuality: StateFlow<String> = _streamQuality.asStateFlow()

    private val _audioEnabled = MutableStateFlow(true)
    val audioEnabled: StateFlow<Boolean> = _audioEnabled.asStateFlow()

    // --- Tactical Pitch Ball & Player Tracking Coordinates ---
    // Simulates the physical position of the ball and highlights on a tactical field board
    private val _ballX = MutableStateFlow(0.5f)
    val ballX: StateFlow<Float> = _ballX.asStateFlow()

    private val _ballY = MutableStateFlow(0.5f)
    val ballY: StateFlow<Float> = _ballY.asStateFlow()

    private val _currentPlayPhase = MutableStateFlow("Teams are warming up")
    val currentPlayPhase: StateFlow<String> = _currentPlayPhase.asStateFlow()

    private val _attackingTeamId = MutableStateFlow<String?>(null)
    val attackingTeamId: StateFlow<String?> = _attackingTeamId.asStateFlow()

    // --- Standings Data ---
    private val _leagueStandings = MutableStateFlow<List<StandingEntry>>(emptyList())
    val leagueStandings: StateFlow<List<StandingEntry>> = _leagueStandings.asStateFlow()

    // --- Headlines News ---
    private val _soccerNews = MutableStateFlow<List<SoccerNews>>(emptyList())
    val soccerNews: StateFlow<List<SoccerNews>> = _soccerNews.asStateFlow()

    init {
        initializeData()
        startFootballSimulationEngine()
    }

    private fun initializeData() {
        val utd = Team("utd", "Manchester United", "MUN", "🔴", "#DA291C")
        val city = Team("mci", "Manchester City", "MCI", "🔵", "#6CABDD")
        val madrid = Team("rma", "Real Madrid", "RMA", "⚪", "#FFFFFF")
        val barca = Team("fcb", "FC Barcelona", "FCB", "🔵🔴", "#004D98")
        val juve = Team("juv", "Juventus", "JUV", "⚫⚪", "#000000")
        val inter = Team("int", "Inter Milan", "INT", "🔵⚫", "#001A9C")
        val bayern = Team("bay", "Bayern Munich", "FCB", "🔴⚪", "#DC052D")
        val arsenal = Team("ars", "Arsenal", "ARS", "🔴⚪", "#EF0107")

        // Default lineups
        val homeLineupUtd = listOf(
            PlayerPosition("Onana", 1, "GK", 0.5f, 0.05f),
            PlayerPosition("Mazraoui", 3, "DF", 0.15f, 0.25f),
            PlayerPosition("de Ligt", 4, "DF", 0.38f, 0.18f),
            PlayerPosition("Martinez", 6, "DF", 0.62f, 0.18f),
            PlayerPosition("Dalot", 20, "DF", 0.85f, 0.25f),
            PlayerPosition("Casemiro", 18, "MF", 0.35f, 0.45f),
            PlayerPosition("Mainoo", 37, "MF", 0.65f, 0.45f),
            PlayerPosition("Garnacho", 17, "MF", 0.15f, 0.65f),
            PlayerPosition("Fernandes", 8, "MF", 0.5f, 0.60f),
            PlayerPosition("Rashford", 10, "MF", 0.85f, 0.65f),
            PlayerPosition("Hojlund", 9, "FW", 0.5f, 0.88f)
        )

        val awayLineupCity = listOf(
            PlayerPosition("Ederson", 31, "GK", 0.5f, 0.95f),
            PlayerPosition("Walker", 2, "DF", 0.15f, 0.75f),
            PlayerPosition("Dias", 3, "DF", 0.38f, 0.82f),
            PlayerPosition("Stones", 5, "DF", 0.62f, 0.82f),
            PlayerPosition("Gvardiol", 24, "DF", 0.85f, 0.75f),
            PlayerPosition("Rodri", 16, "MF", 0.5f, 0.60f),
            PlayerPosition("De Bruyne", 17, "MF", 0.35f, 0.50f),
            PlayerPosition("Silva", 20, "MF", 0.65f, 0.50f),
            PlayerPosition("Foden", 47, "FW", 0.15f, 0.35f),
            PlayerPosition("Haaland", 9, "FW", 0.5f, 0.12f),
            PlayerPosition("Grealish", 10, "FW", 0.85f, 0.35f)
        )

        val madridLineup = listOf(
            PlayerPosition("Courtois", 1, "GK", 0.5f, 0.05f),
            PlayerPosition("Carvajal", 2, "DF", 0.15f, 0.22f),
            PlayerPosition("Rudiger", 22, "DF", 0.38f, 0.16f),
            PlayerPosition("Militao", 3, "DF", 0.62f, 0.16f),
            PlayerPosition("Mendy", 23, "DF", 0.85f, 0.22f),
            PlayerPosition("Tchouameni", 14, "MF", 0.5f, 0.40f),
            PlayerPosition("Valverde", 8, "MF", 0.25f, 0.52f),
            PlayerPosition("Bellingham", 5, "MF", 0.75f, 0.52f),
            PlayerPosition("Rodrygo", 11, "FW", 0.2f, 0.75f),
            PlayerPosition("Mbappe", 9, "FW", 0.5f, 0.88f),
            PlayerPosition("Vinicius Jr", 7, "FW", 0.8f, 0.75f)
        )

        val barcaLineup = listOf(
            PlayerPosition("ter Stegen", 1, "GK", 0.5f, 0.95f),
            PlayerPosition("Kounde", 23, "DF", 0.12f, 0.76f),
            PlayerPosition("Cubarsi", 2, "DF", 0.35f, 0.84f),
            PlayerPosition("Araujo", 4, "DF", 0.65f, 0.84f),
            PlayerPosition("Balde", 3, "DF", 0.88f, 0.76f),
            PlayerPosition("de Jong", 21, "MF", 0.3f, 0.58f),
            PlayerPosition("Pedri", 8, "MF", 0.5f, 0.52f),
            PlayerPosition("Gavi", 6, "MF", 0.7f, 0.58f),
            PlayerPosition("Yamal", 19, "FW", 0.15f, 0.32f),
            PlayerPosition("Lewandowski", 9, "FW", 0.5f, 0.15f),
            PlayerPosition("Raphinha", 11, "FW", 0.85f, 0.32f)
        )

        val match1 = FootballMatch(
            id = "m1",
            league = "English Premier League",
            homeTeam = utd,
            awayTeam = city,
            homeScore = 1,
            awayScore = 1,
            status = "LIVE",
            minute = 54,
            lineups = Lineups(homeLineupUtd, awayLineupCity),
            commentary = mutableListOf(
                CommentaryItem(UUID.randomUUID().toString(), 45, "Half-time in Manchester. A competitive, energetic derby match so far with score level at 1-1.", false),
                CommentaryItem(UUID.randomUUID().toString(), 34, "GOAL! Manchester City strikes back! Kevin De Bruyne finds Erling Haaland who fires it home from close range!", true, EventType.GOAL),
                CommentaryItem(UUID.randomUUID().toString(), 12, "GOAL! Manchester United takes the lead! Bruno Fernandes places an absolute rocket past Ederson!", true, EventType.GOAL),
                CommentaryItem(UUID.randomUUID().toString(), 4, "A lively start here. United pushing forward with high pressing, winning early possession.", false)
            )
        )

        val match2 = FootballMatch(
            id = "m2",
            league = "UEFA Champions League",
            homeTeam = madrid,
            awayTeam = barca,
            homeScore = 2,
            awayScore = 2,
            status = "LIVE",
            minute = 74,
            lineups = Lineups(madridLineup, barcaLineup),
            commentary = mutableListOf(
                CommentaryItem(UUID.randomUUID().toString(), 70, "GOAL! FC Barcelona levels again! Raphinha curls a stunning free-kick around the wall into the top corner!", true, EventType.GOAL),
                CommentaryItem(UUID.randomUUID().toString(), 55, "GOAL! Mbappe scores again! Bellingham threads a perfect pass, Mbappe cuts in and curls it past Ter Stegen!", true, EventType.GOAL),
                CommentaryItem(UUID.randomUUID().toString(), 43, "Yellow Card! Antonio Rudiger commits a tactical foul in midfield, stopping Raphinha's break.", true, EventType.YELLOW_CARD),
                CommentaryItem(UUID.randomUUID().toString(), 28, "GOAL! Barcelona equalises! Lamine Yamal slides a gorgeous assist to Robert Lewandowski who tap-ins!", true, EventType.GOAL),
                CommentaryItem(UUID.randomUUID().toString(), 16, "GOAL! Real Madrid takes a dramatic early lead! Kylian Mbappe heads it in from Carvajal's cross!", true, EventType.GOAL)
            )
        )

        val match3 = FootballMatch(
            id = "m3",
            league = "Italian Serie A",
            homeTeam = juve,
            awayTeam = inter,
            homeScore = 0,
            awayScore = 0,
            status = "UPCOMING",
            minute = 0,
            lineups = Lineups(homeLineupUtd, awayLineupCity) // Placeholders
        )

        val match4 = FootballMatch(
            id = "m4",
            league = "UEFA Champions League",
            homeTeam = bayern,
            awayTeam = arsenal,
            homeScore = 1,
            awayScore = 0,
            status = "FT",
            minute = 90,
            lineups = Lineups(homeLineupUtd, awayLineupCity), // Placeholders
            commentary = mutableListOf(
                CommentaryItem(UUID.randomUUID().toString(), 90, "Referee blows the final whistle! Bayern Munich secures a tough 1-0 Champion's league victory over Arsenal.", false),
                CommentaryItem(UUID.randomUUID().toString(), 62, "Bayern dominates possession, keeping Arsenal key attackers at bay.", false),
                CommentaryItem(UUID.randomUUID().toString(), 41, "GOAL! Bayern Munich breaks the deadlock! Kane finishes with clinical poise from a corner deflection.", true, EventType.GOAL)
            )
        )

        _matches.value = listOf(match1, match2, match3, match4)
        _selectedMatchId.value = "m2" // Barça vs Real Madrid by default as it is in second half action!

        // Insert some initial funny chat rooms reactions for Barcelona vs Madrid in Room cache
        viewModelScope.launch(Dispatchers.IO) {
            val dbCount = AppDatabase.getDatabase(getApplication()).footballDao().getChatsForMatch("m2").first().size
            if (dbCount == 0) {
                repository.saveChatMessage("m1", "RedDev19", "Bruno is running the show today, brilliant start!", false, "#DA291C")
                repository.saveChatMessage("m1", "BlueMoonFan", "Haaland looks hungry, he's scoring 2 more", false, "#6CABDD")
                
                repository.saveChatMessage("m2", "CatalanCuler", "Mbappe is too fast, Balde needs support!", false, "#004D98")
                repository.saveChatMessage("m2", "HalaMadrid_99", "Bellingham is everywhere! Golden Boy!", false, "#FFFFFF")
                repository.saveChatMessage("m2", "TikiTakaMaster", "Lewy is so clinical under Hansi Flick. What a match!", false, "#004D98")
                repository.saveChatMessage("m2", "GalacticoBoss", "That Rudiger yellow was soft. Terrible refereeing", false, "#FFFFFF")
            }
        }

        // Initialize Standings
        _leagueStandings.value = listOf(
            StandingEntry(1, madrid, 34, 26, 6, 2, 82, 28, 84),
            StandingEntry(2, barca, 34, 24, 5, 5, 80, 36, 77),
            StandingEntry(3, city, 34, 23, 6, 5, 86, 33, 75),
            StandingEntry(4, arsenal, 34, 22, 6, 6, 78, 29, 72),
            StandingEntry(5, utd, 34, 18, 5, 11, 58, 44, 59),
            StandingEntry(6, bayern, 34, 17, 7, 10, 64, 49, 58),
            StandingEntry(7, inter, 34, 16, 8, 10, 56, 42, 56),
            StandingEntry(8, juve, 34, 15, 9, 10, 51, 41, 54)
        )

        // Initialize News
        _soccerNews.value = listOf(
            SoccerNews("n1", "Erling Haaland on Track to Win Premier League Golden Boot", "League News", "2 hours ago", "With 29 goals already in the bag, Erling Haaland is cruising toward another Premier League Golden Boot. Pep Guardiola expressed delight, praising Haaland's intense focus and team-first playstyle.", "David Jones", "#6CABDD"),
            SoccerNews("n2", "FC Barcelona Targeting Dutch Midfield sensation in €60M transfer swoop", "Transfer Rumour", "4 hours ago", "Reports in Spain suggest Barcelona have opened exploratory talks with €60 Million rated Dutch midfielder as Flick aims to strengthen central distribution capabilities.", "Fabrizio Romero", "#004D98"),
            SoccerNews("n3", "Kylian Mbappe reflects on dramatic goal streak at Santiago Bernabeu", "Match Report", "1 day ago", "Mbappe reached 8 goals in 6 matches, declaring that 'The team chemistry is reaching the level we dreamed of.' Ancelotti highlights Mbappe's tactical flexibility alongside Bellingham.", "Sofia Silva", "#FFFFFF"),
            SoccerNews("n4", "UEFA introduces new dynamic tracker in mobile apps", "Global Football", "3 days ago", "UEFA announced a revolutionary visual digital twin technology integration enabling fans to monitor real-time tacticians metrics right from mobile screens.", "Hans Werner", "#4A148C")
        )
    }

    // --- Football Simulation Tick Engine ---
    private fun startFootballSimulationEngine() {
        viewModelScope.launch {
            while (true) {
                delay(4000) // Trigger an action phase update every 4 seconds!
                val liveList = _matches.value.map { it.copy() }
                
                liveList.forEach { match ->
                    if (match.status == "LIVE") {
                        // 1. Advance Match Clock (Random progress to keep it natural)
                        match.minute += 1
                        if (match.minute >= 95) {
                            match.status = "FT"
                            match.commentary.add(0, CommentaryItem(UUID.randomUUID().toString(), 90, "And that's it! Full-time whistle blows for ${match.homeTeam.name} vs ${match.awayTeam.name}.", false))
                        } else {
                            // 2. Play Phase Simulation Logic
                            val probabilityPercent = Random.nextInt(100)
                            
                            // Let's draw ball coordinates & gameplay updates ONLY for the currently selected active match!
                            // This powers our state-of-the-art Canvas Pitch Visualizer!
                            if (match.id == _selectedMatchId.value) {
                                simulatePlayPhaseInfo(match, probabilityPercent)
                            }

                            // 3. Event Triggering (Goals, Shots, Cards)
                            when {
                                probabilityPercent < 8 -> { // 8% Goal!
                                    val scoringTeamIsHome = Random.nextBoolean()
                                    val scoringTeam = if (scoringTeamIsHome) match.homeTeam else match.awayTeam
                                    val concedingTeam = if (scoringTeamIsHome) match.awayTeam else match.homeTeam
                                    val scorer = getLineupPlayerName(match, scoringTeamIsHome, "FW", "MF")
                                    val assister = getLineupPlayerName(match, scoringTeamIsHome, "MF", "DF")
                                    
                                    if (scoringTeamIsHome) match.homeScore++ else match.awayScore++
                                    
                                    // Record event details
                                    val event = MatchEvent(
                                        id = UUID.randomUUID().toString(),
                                        minute = match.minute,
                                        type = EventType.GOAL,
                                        teamId = scoringTeam.id,
                                        playerName = scorer,
                                        secondaryPlayerName = assister,
                                        details = "What a sensational shot! Bottom corner!"
                                    )
                                    match.events.add(0, event)

                                    // Add commentary
                                    val commentary = CommentaryItem(
                                        id = UUID.randomUUID().toString(),
                                        minute = match.minute,
                                        text = "⚡ GOAL!!! ${scoringTeam.name} scores! $scorer hits a magnificent volley from the edge of the area! Assisted by $assister. Score: ${match.homeScore}-${match.awayScore}!",
                                        isCritical = true,
                                        type = EventType.GOAL
                                    )
                                    match.commentary.add(0, commentary)

                                    // Trigger fans reaction in Chat persistence
                                    generateSimulatedFansReactions(match, true, scoringTeam, concedingTeam)
                                }
                                probabilityPercent in 8..15 -> { // 7% Yellow Card
                                    val cardTeamIsHome = Random.nextBoolean()
                                    val team = if (cardTeamIsHome) match.homeTeam else match.awayTeam
                                    val player = getLineupPlayerName(match, cardTeamIsHome, "DF", "MF")
                                    
                                    val event = MatchEvent(UUID.randomUUID().toString(), match.minute, EventType.YELLOW_CARD, team.id, player, null, "Late sliding challenge")
                                    match.events.add(0, event)
                                    
                                    match.commentary.add(0, CommentaryItem(UUID.randomUUID().toString(), match.minute, "⚠️ Yellow Card! $player is booked for hard tackle on the counter.", true, EventType.YELLOW_CARD))
                                    
                                    // Stats adjustment
                                    if (cardTeamIsHome) {
                                        match.stats = match.stats.copy(yellowCardsHome = match.stats.yellowCardsHome + 1, foulsHome = match.stats.foulsHome + 1)
                                    } else {
                                        match.stats = match.stats.copy(yellowCardsAway = match.stats.yellowCardsAway + 1, foulsAway = match.stats.foulsAway + 1)
                                    }
                                }
                                probabilityPercent in 16..25 -> { // 10% Shot on Target
                                    val shotIsHome = Random.nextBoolean()
                                    val team = if (shotIsHome) match.homeTeam else match.awayTeam
                                    val player = getLineupPlayerName(match, shotIsHome, "FW", "MF")
                                    match.commentary.add(0, CommentaryItem(UUID.randomUUID().toString(), match.minute, "⚡ Shot on target! Brilliant strike by $player, saved masterfully by the keeper!", false, EventType.SHOT_ON_TARGET))
                                    
                                    if (shotIsHome) {
                                        match.stats = match.stats.copy(
                                            shotsHome = match.stats.shotsHome + 1,
                                            shotsOnTargetHome = match.stats.shotsOnTargetHome + 1
                                        )
                                    } else {
                                        match.stats = match.stats.copy(
                                            shotsAway = match.stats.shotsAway + 1,
                                            shotsOnTargetAway = match.stats.shotsOnTargetAway + 1
                                        )
                                    }
                                }
                                probabilityPercent in 26..35 -> { // Just a random fan chat update
                                    generateSimulatedFansReactions(match, false, match.homeTeam, match.awayTeam)
                                }
                            }
                        }
                    }
                }
                _matches.value = liveList
            }
        }
    }

    private fun simulatePlayPhaseInfo(match: FootballMatch, roll: Int) {
        val attackHome = Random.nextBoolean()
        _attackingTeamId.value = if (attackHome) match.homeTeam.id else match.awayTeam.id
        
        when {
            roll < 20 -> {
                _ballX.value = if (attackHome) 0.85f else 0.15f
                _ballY.value = Random.nextFloat() * 0.4f + 0.3f
                _currentPlayPhase.value = "🔥 Danger! ${if (attackHome) match.homeTeam.shortName else match.awayTeam.shortName} overload the penalty box!"
            }
            roll in 20..45 -> {
                _ballX.value = if (attackHome) 0.65f else 0.35f
                _ballY.value = Random.nextFloat() * 0.8f + 0.1f
                _currentPlayPhase.value = "Midfield tussle in ${if (attackHome) match.homeTeam.shortName else match.awayTeam.shortName} half. Fast recycling."
            }
            roll in 46..75 -> {
                _ballX.value = if (attackHome) 0.75f else 0.25f
                _ballY.value = if (Random.nextBoolean()) 0.12f else 0.88f // Wingers
                _currentPlayPhase.value = "Winger makes dynamic run down the ${if (_ballY.value > 0.5) "right" else "left"} flank..."
            }
            else -> {
                _ballX.value = Random.nextFloat() * 0.4f + 0.3f
                _ballY.value = Random.nextFloat() * 0.6f + 0.2f
                _currentPlayPhase.value = "Reflected tactical block. Ball circulating in defense."
            }
        }
    }

    private fun getLineupPlayerName(match: FootballMatch, isHome: Boolean, p1: String, p2: String): String {
        val players = if (isHome) match.lineups.homeLineup else match.lineups.awayLineup
        val subset = players.filter { it.role == p1 || it.role == p2 }
        if (subset.isEmpty()) return if (isHome) match.homeTeam.name else match.awayTeam.name
        return subset[Random.nextInt(subset.size)].name
    }

    private fun generateSimulatedFansReactions(match: FootballMatch, isGoal: Boolean, activeTeam: Team, defenseTeam: Team) {
        viewModelScope.launch(Dispatchers.IO) {
            val fanNames = listOf("ScouseKop", "TrueMadridista", "BarcaGooner", "CulerPower", "RedDevil99", "BlueEra", "GunnerG", "TikiTactician")
            val selectedFan = fanNames[Random.nextInt(fanNames.size)]
            val avatarColors = listOf("#00FF66", "#ADFF2F", "#FF3B30", "#FF9500", "#004D98", "#DC052D", "#6CABDD")
            val chosenColor = avatarColors[Random.nextInt(avatarColors.size)]

            val message = if (isGoal) {
                listOf(
                    "OH MY GOODNESS! What a goals from ${activeTeam.shortName}! Absolute class!",
                    "GG! Completely deserved! We are dominating ${defenseTeam.shortName}!",
                    "Unbelievable assist! Did you see that turn?!",
                    "Goal of the season contender right here!",
                    "Defense is sleeping! How does ${defenseTeam.shortName} let him shoot from there?!"
                )[Random.nextInt(5)]
            } else {
                listOf(
                    "Ref is blind! That was a clear handball!",
                    "This tactical shape is brilliant, Flick is masterclass.",
                    "We need to substitute in a fresh winger, pace is dropping.",
                    "Great battle in midfield. Rodri/Casemiro is holding everything.",
                    "We are playing too defensive... push forward!",
                    "Can see another yellow card coming soon, tempers flaring."
                )[Random.nextInt(6)]
            }

            repository.saveChatMessage(match.id, selectedFan, message, false, chosenColor)
        }
    }

    // --- User Actions ---

    fun selectMatch(matchId: String) {
        _selectedMatchId.value = matchId
    }

    fun toggleStreamPlayback() {
        _isPlayingStream.value = !_isPlayingStream.value
    }

    fun changeStreamQuality(quality: String) {
        _streamQuality.value = quality
    }

    fun toggleAudio() {
        _audioEnabled.value = !_audioEnabled.value
    }

    // Bookmarking toggle via database flow
    fun toggleBookmark(match: FootballMatch) {
        viewModelScope.launch(Dispatchers.IO) {
            val matchesBookmarked = bookmarkedMatchIds.value
            if (matchesBookmarked.contains(match.id)) {
                repository.removeBookmark(match.id)
            } else {
                repository.addBookmark(match.id, match.homeTeam.name, match.awayTeam.name)
            }
        }
    }

    // Post chat message in real-time
    fun sendChatMessage(matchId: String, user: String, messageText: String) {
        if (messageText.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Save User's Msg
            repository.saveChatMessage(matchId, user, messageText, false, "#00FF66")

            // 2. Schedule a simulated witty fan reply!
            delay(1500)
            val index = Random.nextInt(4)
            val name = listOf("SoccerSmarty", "FanaticJoe", "Wazza9", "CampNouKID")[index]
            val replyColors = listOf("#FF9500", "#DC052D", "#6CABDD", "#004D98")
            val replyText = listOf(
                "Interesting point @$user! But honestly, their high-pressing system makes it impossible.",
                "Agreed @$user, simple football does wonders.",
                "I don't know about that @$user... that smells like a biased take!",
                "Valid commentary. Hope the manager is reading this chat haha"
            )[index]

            repository.saveChatMessage(matchId, name, replyText, false, replyColors[index])
        }
    }

    // Trigger AI predictions using actual Gemini service
    fun generateAIPrediction(match: FootballMatch) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            val matchSummary = """
                League: ${match.league}
                Score: ${match.homeTeam.name} ${match.homeScore} - ${match.awayScore} ${match.awayTeam.name}
                Status: ${match.status} at ${match.minute}'
                Key events: ${match.events.take(5).joinToString(", ") { "${it.playerName} (${it.type} at ${it.minute}')" }}
                Stats: Possession (${match.stats.possessionHome}% vs ${match.stats.possessionAway}%), Shots (${match.stats.shotsHome} vs ${match.stats.shotsAway})
            """.trimIndent()

            val analysisText = RetrofitGeminiClient.getAnalyticPrediction(match.id, matchSummary)
            
            val updatedMap = _aiAnalyses.value.toMutableMap()
            updatedMap[match.id] = analysisText
            _aiAnalyses.value = updatedMap
            _isAnalyzing.value = false

            // Append analysis to fan chats as an AI Analyst insight!
            repository.saveChatMessage(match.id, "🎤 AI Sports Analyst", analysisText, true, "#39FF14")
        }
    }
}
