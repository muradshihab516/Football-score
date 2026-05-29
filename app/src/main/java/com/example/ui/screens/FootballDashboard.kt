package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.*
import com.example.data.database.FanChatMessageEntity
import com.example.ui.components.FootballPitchTracker
import com.example.ui.components.LiveStreamViewer
import com.example.ui.theme.*
import com.example.ui.viewmodels.FootballViewModel
import kotlinx.coroutines.launch

@Composable
fun FootballDashboard(
    viewModel: FootballViewModel,
    modifier: Modifier = Modifier
) {
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val selectedMatch by viewModel.selectedMatch.collectAsStateWithLifecycle()
    val activeChats by viewModel.activeChats.collectAsStateWithLifecycle()
    val bookmarkedIds by viewModel.bookmarkedMatchIds.collectAsStateWithLifecycle()
    val aiAnalyses by viewModel.aiAnalyses.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()

    val isPlayingStream by viewModel.isPlayingStream.collectAsStateWithLifecycle()
    val streamQuality by viewModel.streamQuality.collectAsStateWithLifecycle()
    val audioEnabled by viewModel.audioEnabled.collectAsStateWithLifecycle()

    val ballX by viewModel.ballX.collectAsStateWithLifecycle()
    val ballY by viewModel.ballY.collectAsStateWithLifecycle()
    val currentPlayPhase by viewModel.currentPlayPhase.collectAsStateWithLifecycle()

    val standings by viewModel.leagueStandings.collectAsStateWithLifecycle()
    val newsList by viewModel.soccerNews.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) } // 0: Live, 1: Match Center, 2: Standings, 3: News

    Scaffold(
        modifier = modifier.fillMaxSize().background(StadiumBlack),
        bottomBar = {
            NavigationBar(
                containerColor = FieldEmerald,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .border(1.dp, PitchDarkLine, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.SportsSoccer, contentDescription = "Scores") },
                    label = { Text("Scores", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LightGrey,
                        selectedTextColor = LightGrey,
                        indicatorColor = NeonLime,
                        unselectedIconColor = MutedGrey,
                        unselectedTextColor = MutedGrey
                    )
                )

                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.LiveTv, contentDescription = "Live Stream") },
                    label = { Text("Stream Box", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LightGrey,
                        selectedTextColor = LightGrey,
                        indicatorColor = NeonLime,
                        unselectedIconColor = MutedGrey,
                        unselectedTextColor = MutedGrey
                    )
                )

                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.FormatListNumbered, contentDescription = "Standings") },
                    label = { Text("Table", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LightGrey,
                        selectedTextColor = LightGrey,
                        indicatorColor = NeonLime,
                        unselectedIconColor = MutedGrey,
                        unselectedTextColor = MutedGrey
                    )
                )

                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Default.Newspaper, contentDescription = "News") },
                    label = { Text("News", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = LightGrey,
                        selectedTextColor = LightGrey,
                        indicatorColor = NeonLime,
                        unselectedIconColor = MutedGrey,
                        unselectedTextColor = MutedGrey
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(StadiumBlack)
                .padding(innerPadding)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FieldEmerald)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.KeepForComposeScope()
            ) {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = "App logo symbol",
                    tint = PitchGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ScoreStream",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = LightGrey
                    )
                    Text(
                        text = "HIGH-DENSITY SPORTS BROADCAST & MATCH NETWORK",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MutedGrey,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            Divider(color = PitchDarkLine, thickness = 1.dp)

            // Tabs switching viewport with fade animation
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_animation"
            ) { targetTab ->
                when (targetTab) {
                    0 -> ScoresScreen(
                        matches = matches,
                        bookmarkedIds = bookmarkedIds,
                        onMatchSelected = { matchId ->
                            viewModel.selectMatch(matchId)
                            activeTab = 1 // Switch to Match Live center
                        },
                        onBookmarkToggled = { match -> viewModel.toggleBookmark(match) }
                    )
                    1 -> MatchCenterScreen(
                        match = selectedMatch,
                        activeChats = activeChats,
                        isAnalyzing = isAnalyzing,
                        analyses = aiAnalyses,
                        isPlayingStream = isPlayingStream,
                        streamQuality = streamQuality,
                        audioEnabled = audioEnabled,
                        ballX = ballX,
                        ballY = ballY,
                        playPhase = currentPlayPhase,
                        onSendMessage = { txt -> selectedMatch?.let { viewModel.sendChatMessage(it.id, "You", txt) } },
                        onAnalyseAI = { selectedMatch?.let { viewModel.generateAIPrediction(it) } },
                        onTogglePlay = { viewModel.toggleStreamPlayback() },
                        onToggleAudio = { viewModel.toggleAudio() },
                        onChangeQuality = { q -> viewModel.changeStreamQuality(q) }
                    )
                    2 -> StandingsScreen(standings = standings)
                    3 -> NewsScreen(newsList = newsList)
                }
            }
        }
    }
}

// Arrangement Helper to maintain imports safety
private fun Arrangement.KeepForComposeScope(): Arrangement.Horizontal {
    return Arrangement.Start
}

// ==================== TAB 0: SCORES LIST VIEW ====================
@Composable
fun ScoresScreen(
    matches: List<FootballMatch>,
    bookmarkedIds: Set<String>,
    onMatchSelected: (String) -> Unit,
    onBookmarkToggled: (FootballMatch) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("scores_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Active Live matches Subheader
        val liveMatches = matches.filter { it.status == "LIVE" }
        if (liveMatches.isNotEmpty()) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(LiveRed, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE MATCHES IN PROGRESS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = LiveRed,
                        letterSpacing = 1.sp
                    )
                }
            }

            items(liveMatches) { match ->
                MatchScoreCard(
                    match = match,
                    isBookmarked = bookmarkedIds.contains(match.id),
                    onClick = { onMatchSelected(match.id) },
                    onBookmarkClick = { onBookmarkToggled(match) }
                )
            }
        }

        // Scheduled / Finished matches Subheader Map
        val remainingMatches = matches.filter { it.status != "LIVE" }
        if (remainingMatches.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "SCHEDULED & RECENT FIXTURES",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MutedGrey,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(remainingMatches) { match ->
                MatchScoreCard(
                    match = match,
                    isBookmarked = bookmarkedIds.contains(match.id),
                    onClick = { onMatchSelected(match.id) },
                    onBookmarkClick = { onBookmarkToggled(match) }
                )
            }
        }
    }
}

@Composable
fun MatchScoreCard(
    match: FootballMatch,
    isBookmarked: Boolean,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .testTag("match_card_${match.id}")
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(14.dp),
        border = if (match.status == "LIVE") borderPitchHighlight() else borderSubtle()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // League and Bookmark bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = match.league,
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGrey,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Match Status badge
                    val badgeColor = when (match.status) {
                        "LIVE" -> LiveRed
                        "FT" -> MutedGrey
                        else -> LiveOrange
                    }
                    val badgeText = when (match.status) {
                        "LIVE" -> "LIVE ${match.minute}'"
                        "FT" -> "FINISHED"
                        else -> "19:45"
                    }

                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .background(badgeColor.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = onBookmarkClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Bookmark match",
                            tint = if (isBookmarked) LiveRed else MutedGrey,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score Display Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team logo box
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = match.homeTeam.logoAsset,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = match.homeTeam.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = LightGrey,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "HOME",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGrey
                    )
                }

                // Numeric Score Box
                Column(
                    modifier = Modifier.weight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (match.status != "UPCOMING") {
                        Text(
                            text = "${match.homeScore}  -  ${match.awayScore}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (match.status == "LIVE") PitchGreen else LightGrey,
                            letterSpacing = 1.sp
                        )
                    } else {
                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = PitchGreen,
                            letterSpacing = 2.sp
                        )
                    }
                }

                // Away Team logo box
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = match.awayTeam.logoAsset,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = match.awayTeam.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = LightGrey,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "AWAY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGrey
                    )
                }
            }

            // Scorers/Events snippet (Only show a quick summary if score is active)
            if (match.events.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = PitchDarkLine, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))
                val scorersText = match.events
                    .filter { it.type == EventType.GOAL }
                    .take(2)
                    .joinToString(" • ") { "${it.playerName} (${it.minute}')" }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "Ball",
                        tint = PitchGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (scorersText.isNotBlank()) scorersText else "Tense dynamic gameplay in progress",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGrey,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun borderPitchHighlight() = androidx.compose.foundation.BorderStroke(
    width = 1.dp,
    brush = Brush.linearGradient(listOf(PitchGreen, PitchDarkLine))
)
@Composable
fun borderSubtle() = androidx.compose.foundation.BorderStroke(
    width = 1.dp,
    color = PitchDarkLine
)


// ==================== TAB 1: MATCH CENTRE LIVE BOX ====================
@Composable
fun MatchCenterScreen(
    match: FootballMatch?,
    activeChats: List<FanChatMessageEntity>,
    isAnalyzing: Boolean,
    analyses: Map<String, String>,
    isPlayingStream: Boolean,
    streamQuality: String,
    audioEnabled: Boolean,
    ballX: Float,
    ballY: Float,
    playPhase: String,
    onSendMessage: (String) -> Unit,
    onAnalyseAI: () -> Unit,
    onTogglePlay: () -> Unit,
    onToggleAudio: () -> Unit,
    onChangeQuality: (String) -> Unit
) {
    if (match == null) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = MutedGrey, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "NO MATCH SELECTED",
                    style = MaterialTheme.typography.titleMedium,
                    color = LightGrey,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Head to the Scores tab and click an active live soccer game to access low-latency streaming and high-fidelity visual trackers.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedGrey,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        return
    }

    var liveFeedChoice by remember { mutableStateOf(0) } // 0: Live Video Stream, 1: Tactical Grid Tracker
    var matchSubTab by remember { mutableStateOf(0) } // 0: Chat Room, 1: Commentary, 2: Key Stats, 3: Lineups

    // Score board banner inside centre
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("match_center_screen")
    ) {
        // Video Stream / Tactical Visual Field Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (liveFeedChoice == 0) {
                LiveStreamViewer(
                    match = match,
                    isPlaying = isPlayingStream,
                    quality = streamQuality,
                    audioEnabled = audioEnabled,
                    onTogglePlay = onTogglePlay,
                    onToggleAudio = onToggleAudio,
                    onChangeQuality = onChangeQuality
                )
            } else {
                FootballPitchTracker(
                    match = match,
                    ballX = ballX,
                    ballY = ballY,
                    phaseText = playPhase
                )
            }
        }

        // Toggle Buttons row for Streams
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { liveFeedChoice = 0 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (liveFeedChoice == 0) PitchGreen else FieldEmerald,
                    contentColor = if (liveFeedChoice == 0) Color.White else LightGrey
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(36.dp)
            ) {
                Icon(Icons.Default.LiveTv, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("TV Broadcast", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { liveFeedChoice = 1 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (liveFeedChoice == 1) PitchGreen else FieldEmerald,
                    contentColor = if (liveFeedChoice == 1) Color.White else LightGrey
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(36.dp)
            ) {
                Icon(Icons.Default.SportsBasketball, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("2D Live Tracker", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }

        // Mini scoreboard stats bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = FieldEmerald)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(match.homeTeam.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = LightGrey)
                    Text("Possession: ${match.stats.possessionHome}%", style = MaterialTheme.typography.labelSmall, color = MutedGrey)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${match.homeScore} - ${match.awayScore}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = PitchGreen
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(match.awayTeam.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = LightGrey)
                    Text("Possession: ${match.stats.possessionAway}%", style = MaterialTheme.typography.labelSmall, color = MutedGrey)
                }
            }
        }

        // Sub tab options: [Fan Chat, Commentary, Key Stats, Lineups]
        ScrollableTabRow(
            selectedTabIndex = matchSubTab,
            containerColor = Color.Transparent,
            contentColor = PitchGreen,
            edgePadding = 16.dp,
            divider = { Divider(color = PitchDarkLine) },
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[matchSubTab]),
                    color = PitchGreen
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("Community Fan Speak", "Live Commentary", "Team Stats", "Lineups Grid").forEachIndexed { i, title ->
                Tab(
                    selected = matchSubTab == i,
                    onClick = { matchSubTab = i },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (matchSubTab == i) PitchGreen else MutedGrey
                        )
                    }
                )
            }
        }

        // Sub tab Content Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(StadiumBlack)
        ) {
            when (matchSubTab) {
                0 -> FanChatSubTab(
                    chats = activeChats,
                    matchId = match.id,
                    isAnalyzing = isAnalyzing,
                    analysisText = analyses[match.id],
                    onAnalyseAI = onAnalyseAI,
                    onSend = onSendMessage
                )
                1 -> LiveCommentarySubTab(commentary = match.commentary)
                2 -> TeamStatsSubTab(stats = match.stats)
                3 -> LineupsSubTab(lineups = match.lineups, match = match)
            }
        }
    }
}

// ==================== SUB TABS IMPLEMENTATIONS ====================

// 1. Fan Chat
@Composable
fun FanChatSubTab(
    chats: List<FanChatMessageEntity>,
    matchId: String,
    isAnalyzing: Boolean,
    analysisText: String?,
    onAnalyseAI: () -> Unit,
    onSend: (String) -> Unit
) {
    var userText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        // AI analyst sticky top element
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = borderPitchHighlight()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(PitchGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI STADIUM SPORTS ANALYST",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = PitchGreen
                        )
                    }

                    if (!isAnalyzing && analysisText == null) {
                        Button(
                            onClick = onAnalyseAI,
                            colors = ButtonDefaults.buttonColors(containerColor = PitchGreen, contentColor = Color.White),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("RUN TACTICAL AI", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (isAnalyzing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PitchGreen, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gemini is reading tactical field metrics...", style = MaterialTheme.typography.bodySmall, color = MutedGrey)
                    }
                } else {
                    Text(
                        text = analysisText ?: "Request a high-precision live tactical analysis powered by Gemini API. It analyzes current formations, match stats, and key events.",
                        style = MaterialTheme.typography.bodySmall,
                        color = LightGrey
                    )
                }
            }
        }

        // Group chats lists
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = false
        ) {
            items(chats) { msg ->
                val bg = if (msg.isAi) PitchGreen.copy(alpha = 0.08f) else Color.Transparent
                val border = if (msg.isAi) borderPitchHighlight() else borderSubtle()
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (msg.isAi) CardSurface else Color(0x35101E17)),
                    shape = RoundedCornerShape(8.dp),
                    border = if (msg.isAi) border else null
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // User Avatar color node
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(android.graphics.Color.parseColor(msg.avatarColorHex)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = msg.user.take(1).uppercase(),
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = msg.user,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (msg.isAi) PitchGreen else LightGrey
                                )
                                Text(
                                    text = "Fans Forum",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedGrey
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = msg.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = LightGrey
                            )
                        }
                    }
                }
            }
        }

        Divider(color = PitchDarkLine)

        // Typing box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FieldEmerald)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = userText,
                onValueChange = { userText = it },
                placeholder = { Text("Speak in Community Speak...", color = MutedGrey, fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = StadiumBlack,
                    unfocusedContainerColor = StadiumBlack,
                    disabledContainerColor = StadiumBlack,
                    focusedTextColor = LightGrey,
                    unfocusedTextColor = LightGrey,
                    focusedIndicatorColor = PitchGreen,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (userText.isNotBlank()) {
                        onSend(userText)
                        userText = ""
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier
                    .size(36.dp)
                    .background(PitchGreen, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// 2. Live Commentary Panel
@Composable
fun LiveCommentarySubTab(commentary: List<CommentaryItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (commentary.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pre-game build up beginning shortly.", style = MaterialTheme.typography.bodySmall, color = MutedGrey)
                }
            }
        }

        items(commentary) { item ->
            val iconSelected = when (item.type) {
                EventType.GOAL -> Icons.Default.SportsSoccer
                EventType.YELLOW_CARD -> Icons.Default.Warning
                EventType.RED_CARD -> Icons.Default.DeleteForever
                else -> Icons.Default.ChatBubbleOutline
            }

            val iconColor = when (item.type) {
                EventType.GOAL -> PitchGreen
                EventType.YELLOW_CARD -> YellowCardColor
                EventType.RED_CARD -> LiveRed
                else -> MutedGrey
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if (item.isCritical) CardSurface else Color(0x1A101E17))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${item.minute}'",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = PitchGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(
                            imageVector = iconSelected,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = item.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightGrey
                    )
                }
            }
        }
    }
}

// 3. Team Stats Bars
@Composable
fun TeamStatsSubTab(stats: MatchStats) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("DETAILED MATCH STATS MONITOR", style = MaterialTheme.typography.labelSmall, color = PitchGreen, fontWeight = FontWeight.Bold)

        StatRow(label = "Possession", homeValue = stats.possessionHome, awayValue = stats.possessionAway, isPercentage = true)
        StatRow(label = "Total Shots", homeValue = stats.shotsHome, awayValue = stats.shotsAway)
        StatRow(label = "Shots on Target", homeValue = stats.shotsOnTargetHome, awayValue = stats.shotsOnTargetAway)
        StatRow(label = "Corners", homeValue = stats.cornersHome, awayValue = stats.cornersAway)
        StatRow(label = "Fouls Committed", homeValue = stats.foulsHome, awayValue = stats.foulsAway)
        StatRow(label = "Yellow Cards", homeValue = stats.yellowCardsHome, awayValue = stats.yellowCardsAway)
    }
}

@Composable
fun StatRow(label: String, homeValue: Int, awayValue: Int, isPercentage: Boolean = false) {
    val total = if (homeValue + awayValue == 0) 1 else homeValue + awayValue
    val scaleHome = homeValue.toFloat() / total.toFloat()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$homeValue${if (isPercentage) "%" else ""}", style = MaterialTheme.typography.bodyMedium, color = LightGrey, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MutedGrey, fontWeight = FontWeight.SemiBold)
            Text("$awayValue${if (isPercentage) "%" else ""}", style = MaterialTheme.typography.bodyMedium, color = LightGrey, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Split bar layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(PitchDarkLine)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(if (scaleHome == 0f) 0.001f else scaleHome)
                    .background(PitchGreen)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(if ((1 - scaleHome) == 0f) 0.001f else (1 - scaleHome))
                    .background(LiveRed)
            )
        }
    }
}

// 4. Team Lineup Grid
@Composable
fun LineupsSubTab(lineups: Lineups, match: FootballMatch) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "TACTICAL PLAYER LINEUPS",
            style = MaterialTheme.typography.labelSmall,
            color = PitchGreen,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            // Home Lineup
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = match.homeTeam.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = LightGrey,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Formation: 4-3-3", style = MaterialTheme.typography.labelSmall, color = MutedGrey)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                lineups.homeLineup.forEach { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(FieldEmerald, CircleShape)
                                .border(0.5.dp, PitchGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(p.number.toString(), fontSize = 8.sp, color = PitchGreen, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(p.name, style = MaterialTheme.typography.bodySmall, color = LightGrey)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = p.role,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGrey,
                            modifier = Modifier
                                .background(PitchDarkLine, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            Divider(modifier = Modifier.width(1.dp).fillMaxHeight(), color = PitchDarkLine)

            // Away Lineup
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = match.awayTeam.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = LightGrey,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Formation: 4-2-3-1", style = MaterialTheme.typography.labelSmall, color = MutedGrey)

                Spacer(modifier = Modifier.height(8.dp))

                lineups.awayLineup.forEach { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(CardSurface, CircleShape)
                                .border(0.5.dp, LiveRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(p.number.toString(), fontSize = 8.sp, color = LiveRed, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(p.name, style = MaterialTheme.typography.bodySmall, color = LightGrey)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = p.role,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGrey,
                            modifier = Modifier
                                .background(PitchDarkLine, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


// ==================== TAB 2: STANDINGS SCREEN ====================
@Composable
fun StandingsScreen(standings: List<StandingEntry>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("standings_screen")
            .padding(16.dp)
    ) {
        Text("EUROPEAN ELITE RANKINGS (2026/2027)", style = MaterialTheme.typography.labelSmall, color = PitchGreen, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Table header row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#", modifier = Modifier.width(24.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedGrey)
                    Text("CLUB", modifier = Modifier.weight(1.8f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedGrey)
                    Text("P", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedGrey, textAlign = TextAlign.Center)
                    Text("GD", modifier = Modifier.weight(0.6f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedGrey, textAlign = TextAlign.Center)
                    Text("PTS", modifier = Modifier.weight(0.7f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PitchGreen, textAlign = TextAlign.End)
                }

                Divider(color = PitchDarkLine)

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(standings) { item ->
                        val isHighlighted = item.rank <= 3
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rank number
                            Text(
                                text = item.rank.toString(),
                                modifier = Modifier.width(24.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                                color = if (isHighlighted) PitchGreen else LightGrey
                            )

                            // Team info
                            Row(
                                modifier = Modifier.weight(1.8f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.team.logoAsset, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.team.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = LightGrey,
                                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Stats Played
                            Text(
                                text = item.played.toString(),
                                modifier = Modifier.weight(0.5f),
                                style = MaterialTheme.typography.bodyMedium,
                                color = LightGrey,
                                textAlign = TextAlign.Center
                            )

                            // Goal Difference
                            val diff = item.goalsFor - item.goalsAgainst
                            val diffText = if (diff > 0) "+$diff" else "$diff"
                            Text(
                                text = diffText,
                                modifier = Modifier.weight(0.6f),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (diff > 0) PitchGreen else LiveRed,
                                textAlign = TextAlign.Center
                            )

                            // Points
                            Text(
                                text = item.points.toString(),
                                modifier = Modifier.weight(0.7f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isHighlighted) PitchGreen else LightGrey,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==================== TAB 3: NEWS PORTAL ====================
@Composable
fun NewsScreen(newsList: List<SoccerNews>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("news_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("AI CURATED FOOTBALL HEADLINES", style = MaterialTheme.typography.labelSmall, color = PitchGreen, fontWeight = FontWeight.Bold)
        }

        items(newsList) { news ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Category and time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = news.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = PitchGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(PitchGreen.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )

                        Text(
                            text = news.timeAgo,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGrey
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title
                    Text(
                        text = news.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = LightGrey
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Content snippet
                    Text(
                        text = news.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightGrey,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(color = PitchDarkLine)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Author
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reporting by ${news.author}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGrey
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share snippet",
                                tint = MutedGrey,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
