@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ChatMessage
import com.example.data.models.Match
import com.example.data.models.NewsArticle
import com.example.data.models.StandingItem
import com.example.ui.components.FootballPitchTracker
import com.example.ui.components.LiveStreamViewer
import com.example.ui.theme.*
import com.example.ui.viewmodels.FootballViewModel

import androidx.compose.material3.ExperimentalMaterial3Api
import kotlin.OptIn

@Composable
fun FootballDashboard(
    viewModel: FootballViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Scores, 1: Stream Box, 2: Table, 3: News

    val matches by viewModel.matches.collectAsState()
    val standings by viewModel.standings.collectAsState()
    val news by viewModel.news.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val selectedMatch by viewModel.selectedMatch.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analysisText by viewModel.analysisText.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = StadiumBlack,
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSurface)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .border(
                            width = 1.dp,
                            color = PitchDarkLine.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "App Logo",
                        tint = PitchGreen,
                        modifier = Modifier.size(34.dp)
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
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.border(1.dp, PitchDarkLine.copy(alpha = 0.3f))
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
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
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
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
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
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
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> ScoresScreen(
                    matches = matches,
                    selectedMatch = selectedMatch,
                    onMatchSelected = {
                        viewModel.selectMatch(it)
                        selectedTab = 1 // instantly jump to visual stream screen when clicked
                    }
                )
                1 -> StreamBoxScreen(
                    match = selectedMatch,
                    chatMessages = chatMessages,
                    isAnalyzing = isAnalyzing,
                    analysisText = analysisText,
                    onAnalyseAI = { viewModel.analyseMatchAI() },
                    onSendMessage = { viewModel.sendMessage(it) },
                    onClearChat = { viewModel.clearChat() }
                )
                2 -> TableScreen(standings = standings)
                3 -> NewsScreen(news = news)
            }
        }
    }
}

// ---------------------- SCORES TAB SCREEN ----------------------
@Composable
fun ScoresScreen(
    matches: List<Match>,
    selectedMatch: Match?,
    onMatchSelected: (Match) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "TODAY'S MATCHES",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MutedGrey,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        val liveMatches = matches.filter { it.status == "LIVE" }
        val otherMatches = matches.filter { it.status != "LIVE" }

        if (liveMatches.isNotEmpty()) {
            item {
                Text(
                    text = "🔴 LIVE FIXTURES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LiveRed,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            items(liveMatches) { match ->
                MatchCard(
                    match = match,
                    isSelected = selectedMatch?.id == match.id,
                    onClick = { onMatchSelected(match) }
                )
            }
        }

        if (otherMatches.isNotEmpty()) {
            item {
                Text(
                    text = "🗓️ COMPLETED & SCHEDULED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedGrey,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
            items(otherMatches) { match ->
                MatchCard(
                    match = match,
                    isSelected = selectedMatch?.id == match.id,
                    onClick = { onMatchSelected(match) }
                )
            }
        }
    }
}

@Composable
fun MatchCard(
    match: Match,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) PitchGreen else PitchDarkLine.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ).testTag("match_card_${match.id}"),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (match.status == "LIVE") LiveRed.copy(alpha = 0.15f)
                            else if (match.status == "FT") MutedGrey.copy(alpha = 0.15f)
                            else PitchGreen.copy(alpha = 0.1f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (match.status == "LIVE") "LIVE ${match.timeMinutes}'" else match.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (match.status == "LIVE") LiveRed else MutedGrey
                    )
                }

                // AI Ready Tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Ready",
                        tint = PitchGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AI ANALYSIS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PitchGreen,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Teams & Score row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home team
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = match.homeTeam.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = LightGrey,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Score center display
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (match.status == "SCHEDULED") {
                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MutedGrey,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "18:00 BST",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGrey
                        )
                    } else {
                        Text(
                            text = "${match.homeScore}  -  ${match.awayScore}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (match.status == "LIVE") PitchGreen else LightGrey,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Away team
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = match.awayTeam.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = LightGrey,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


// ---------------------- STREAM BOX TAB SCREEN ----------------------
@Composable
fun StreamBoxScreen(
    match: Match?,
    chatMessages: List<ChatMessage>,
    isAnalyzing: Boolean,
    analysisText: String?,
    onAnalyseAI: () -> Unit,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit
) {
    if (match == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SportsSoccer,
                contentDescription = "No Match",
                tint = MutedGrey,
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "NO MATCH SELECTED",
                style = MaterialTheme.typography.titleMedium,
                color = LightGrey,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Please select any active fixture on the 'Scores' tab to review lives streams & AI assistants.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedGrey,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        return
    }

    var liveFeedChoice by remember { mutableStateOf(0) } // 0: Live Feed Match Stats, 1: AI Coach Assistant

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Broadcast Stream Player View
        item {
            LiveStreamViewer(match = match)
        }

        // Live Feed selector buttons with clean high-contrast light styling
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(FieldEmerald)
                    .padding(4.dp)
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
                    Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LIVE STATS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI COACH CHAT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (liveFeedChoice == 0) {
            // Live Feed: Possession bars, custom pitch visualizer list
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    modifier = Modifier.border(1.dp, PitchDarkLine.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(match.homeTeam.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = LightGrey)
                                Text("Possession: ${match.stats.possessionHome}%", style = MaterialTheme.typography.labelSmall, color = MutedGrey)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("POSSESSION", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PitchGreen)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(match.awayTeam.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = LightGrey)
                                Text("Possession: ${match.stats.possessionAway}%", style = MaterialTheme.typography.labelSmall, color = MutedGrey)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Home vs Away possession meter line
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(FieldEmerald)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(match.stats.possessionHome.toFloat())
                                    .background(PitchGreen)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(match.stats.possessionAway.toFloat())
                                    .background(LiveOrange)
                            )
                        }
                    }
                }
            }

            // Game stats listing (Shots, Shots on Target, Corners, Fouls, Yellow Cards)
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    modifier = Modifier.border(1.dp, PitchDarkLine.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("MATCH STATISTICS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedGrey)

                        StatRow("Shots Goal", match.stats.shotsHome, match.stats.shotsAway)
                        StatRow("On-Target", match.stats.shotsOnTargetHome, match.stats.shotsOnTargetAway)
                        StatRow("Corners", match.stats.cornersHome, match.stats.cornersAway)
                        StatRow("Fouls Raised", match.stats.foulsHome, match.stats.foulsAway)
                        StatRow("Yellow-Cards", match.stats.yellowCardsHome, match.stats.yellowCardsAway)
                    }
                }
            }

            // Real field formations positioning node grid
            item {
                FootballPitchTracker(match = match)
            }

            // Quick trigger "Analyse AI" floating summary
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = FieldEmerald),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, "AI", tint = PitchGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SCORESTREAM CO-COACH INSIGHTS", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = LightGrey)
                            }

                            if (!isAnalyzing && analysisText == null) {
                                Button(
                                    onClick = onAnalyseAI,
                                    colors = ButtonDefaults.buttonColors(containerColor = PitchGreen, contentColor = Color.White),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("Analyse AI", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (isAnalyzing) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(color = PitchGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyzing live matches patterns...", fontSize = 11.sp, color = MutedGrey)
                            }
                        }

                        analysisText?.let {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = LightGrey,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Tactician AI Assistant Chatbot Tab Choice
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .border(1.dp, PitchDarkLine.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopAppBar(
                            title = {
                                Text(
                                    "AI ScoreStream Chat",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = LightGrey,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            actions = {
                                IconButton(onClick = onClearChat) {
                                    Icon(Icons.Default.Delete, contentDescription = "Clear Chat", tint = LiveRed)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                            modifier = Modifier.height(48.dp)
                        )

                        HorizontalDivider(color = PitchDarkLine.copy(alpha = 0.3f))

                        val lazyListState = rememberLazyListState()
                        LaunchedEffect(chatMessages.size) {
                            if (chatMessages.isNotEmpty()) {
                                lazyListState.animateScrollToItem(chatMessages.size - 1)
                            }
                        }

                        // Message window list box
                        Box(modifier = Modifier.weight(1f).padding(8.dp)) {
                            LazyColumn(
                                state = lazyListState,
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(chatMessages) { msg ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = if (msg.isAi) Alignment.CenterStart else Alignment.CenterEnd
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth(0.85f)
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = 12.dp,
                                                        topEnd = 12.dp,
                                                        bottomStart = if (msg.isAi) 0.dp else 12.dp,
                                                        bottomEnd = if (msg.isAi) 12.dp else 0.dp
                                                    )
                                                )
                                                .background(if (msg.isAi) FieldEmerald else NeonLime)
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = if (msg.isAi) "⚽ AI ANALYST" else "USER",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (msg.isAi) PitchGreen else LightGrey
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
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

                        HorizontalDivider(color = PitchDarkLine.copy(alpha = 0.3f))

                        // Message typing entry box
                        var textInput by remember { mutableStateOf("") }
                        val focusManager = LocalFocusManager.current

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = textInput,
                                onValueChange = { textInput = it },
                                placeholder = {
                                    Text(
                                        "Ask about formations, predictions, xG...",
                                        fontSize = 12.sp,
                                        color = MutedGrey
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_input_field"),
                                shape = RoundedCornerShape(20.dp),
                                maxLines = 2,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(onSend = {
                                    if (textInput.isNotBlank()) {
                                        onSendMessage(textInput)
                                        textInput = ""
                                        focusManager.clearFocus()
                                    }
                                }),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = StadiumBlack,
                                    unfocusedContainerColor = StadiumBlack,
                                    disabledContainerColor = StadiumBlack,
                                    focusedTextColor = LightGrey,
                                    unfocusedTextColor = LightGrey,
                                    focusedIndicatorColor = PitchGreen,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = {
                                    if (textInput.isNotBlank()) {
                                        onSendMessage(textInput)
                                        textInput = ""
                                        focusManager.clearFocus()
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(PitchGreen, CircleShape)
                                    .testTag("send_message_button")
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
            }
        }
    }
}

@Composable
fun StatRow(label: String, homeValue: Int, awayValue: Int) {
    val total = homeValue + awayValue
    val isPercentage = label.contains("%")
    val homeFill = if (total > 0) homeValue.toFloat() / total else 0.5f

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

        // Left/Right split filled bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(CircleShape)
                .background(FieldEmerald)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(if (homeFill > 0) homeFill else 0.01f)
                    .background(PitchGreen)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(if (1f - homeFill > 0) 1f - homeFill else 0.01f)
                    .background(LiveOrange)
            )
        }
    }
}


// ---------------------- TABLE TAB SCREEN ----------------------
@Composable
fun TableScreen(standings: List<StandingItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "LEAGUE STANDINGS TABLE",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MutedGrey,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        // Header Card row representation
        item {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = FieldEmerald),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#", modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold, color = LightGrey, fontSize = 11.sp)
                    Text("TEAM", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = LightGrey, fontSize = 11.sp)
                    Text("P", modifier = Modifier.width(28.dp), fontWeight = FontWeight.Bold, color = LightGrey, fontSize = 11.sp, textAlign = TextAlign.End)
                    Text("GD", modifier = Modifier.width(35.dp), fontWeight = FontWeight.Bold, color = LightGrey, fontSize = 11.sp, textAlign = TextAlign.End)
                    Text("PTS", modifier = Modifier.width(42.dp), fontWeight = FontWeight.Bold, color = PitchGreen, fontSize = 11.sp, textAlign = TextAlign.End)
                }
            }
        }

        items(standings) { item ->
            val isHighlighted = item.teamName == "Arsenal" || item.teamName == "Manchester United"
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isHighlighted) NeonLime.copy(alpha = 0.3f) else CardSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (isHighlighted) PitchGreen.copy(alpha = 0.4f) else PitchDarkLine.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Position
                    Text(
                        text = "${item.position}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(30.dp),
                        color = if (item.position <= 4) PitchGreen else MutedGrey,
                        fontWeight = FontWeight.Bold
                    )

                    // Logo & Team
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    if (item.teamName == "Arsenal") Color(0xFFD01010)
                                    else if (item.teamName == "Chelsea") Color(0xFF0030C0)
                                    else if (item.teamName == "Manchester United") Color(0xFFE00000)
                                    else if (item.teamName == "Manchester City") Color(0xFF6CC1E5)
                                    else PitchGreen
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.teamName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightGrey,
                            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Played
                    Text(
                        text = "${item.played}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(28.dp),
                        color = MutedGrey,
                        textAlign = TextAlign.End
                    )

                    // Goal Diff
                    val gdSign = if (item.goalDifference > 0) "+${item.goalDifference}" else "${item.goalDifference}"
                    Text(
                        text = gdSign,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(35.dp),
                        color = if (item.goalDifference > 0) Color(0xFF2E7D32) else LiveRed,
                        textAlign = TextAlign.End
                    )

                    // Points Value
                    Text(
                        text = "${item.points}",
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


// ---------------------- NEWS TAB SCREEN ----------------------
@Composable
fun NewsScreen(news: List<NewsArticle>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "FEATURED FOOTBALL NEWS",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MutedGrey,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        items(news) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PitchDarkLine.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.source.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = PitchGreen,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = item.date,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGrey
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = LightGrey
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGrey,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated expandable details window content
                    var expanded by remember { mutableStateOf(false) }
                    AnimatedVisibility(
                        visible = expanded,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        Column {
                            HorizontalDivider(color = PitchDarkLine.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = LightGrey,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    TextButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.textButtonColors(contentColor = PitchGreen)
                    ) {
                        Text(if (expanded) "Show Less" else "Read Full Article", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
