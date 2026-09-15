package com.example.ui.screens.gamification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingCard
import com.example.ui.components.GradientProgressBar
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VioletAccent
import com.example.ui.theme.WarningOrange

data class AchievementBadge(
    val id: String,
    val title: String,
    val category: String, // solved-count, streak, accuracy, chapters-completed, level, explorer
    val description: String,
    val emoji: String,
    val iconVector: ImageVector,
    val bgGradient: List<Color>,
    val isUnlocked: Boolean = true,
    val progressCurrent: Int = 100,
    val progressMax: Int = 100
)

@Composable
fun AchievementsScreen(
    userName: String = "Aarav",
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Tabs: Badges (0) vs Stats (1)
    var selectedTab by remember { mutableIntStateOf(0) }

    // Badges required:
    // solved-count, streak, accuracy, chapters-completed, level, and an "explorer"-type milestone
    val badgeList = remember {
        listOf(
            AchievementBadge(
                id = "solved_count",
                title = "Centurion",
                category = "Solved Count",
                description = "Solved 100+ MCQs with step-by-step review",
                emoji = "💯",
                iconVector = Icons.Default.MilitaryTech,
                bgGradient = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
                isUnlocked = true,
                progressCurrent = 124,
                progressMax = 100
            ),
            AchievementBadge(
                id = "streak",
                title = "On Fire!",
                category = "Daily Streak",
                description = "Maintained a 7-day study streak",
                emoji = "🔥",
                iconVector = Icons.Default.LocalFireDepartment,
                bgGradient = listOf(Color(0xFFEF4444), Color(0xFFF97316)),
                isUnlocked = true,
                progressCurrent = 7,
                progressMax = 7
            ),
            AchievementBadge(
                id = "accuracy",
                title = "Sharpshooter",
                category = "Accuracy",
                description = "Achieved 90%+ accuracy across 5 full tests",
                emoji = "🎯",
                iconVector = Icons.Default.TrackChanges,
                bgGradient = listOf(Color(0xFF10B981), Color(0xFF059669)),
                isUnlocked = true,
                progressCurrent = 92,
                progressMax = 100
            ),
            AchievementBadge(
                id = "chapters_completed",
                title = "Curriculum Master",
                category = "Chapters Completed",
                description = "Mastered 10 full NCERT curriculum chapters",
                emoji = "📚",
                iconVector = Icons.Default.School,
                bgGradient = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                isUnlocked = true,
                progressCurrent = 10,
                progressMax = 10
            ),
            AchievementBadge(
                id = "level",
                title = "Scholar VIII",
                category = "Level",
                description = "Advanced to Level 8 Academic Scholar",
                emoji = "👑",
                iconVector = Icons.Default.Star,
                bgGradient = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
                isUnlocked = true,
                progressCurrent = 8,
                progressMax = 10
            ),
            AchievementBadge(
                id = "explorer",
                title = "Pathfinder",
                category = "Explorer Milestone",
                description = "Explored all subjects & used OCR image upload",
                emoji = "🧭",
                iconVector = Icons.Default.Explore,
                bgGradient = listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
                isUnlocked = true,
                progressCurrent = 4,
                progressMax = 4
            ),
            AchievementBadge(
                id = "grandmaster",
                title = "Grandmaster",
                category = "Solved Count",
                description = "Solve 500 MCQs in your target exam",
                emoji = "🏆",
                iconVector = Icons.Default.EmojiEvents,
                bgGradient = listOf(Color(0xFF64748B), Color(0xFF475569)),
                isUnlocked = false,
                progressCurrent = 124,
                progressMax = 500
            ),
            AchievementBadge(
                id = "speed_demon",
                title = "Lightning Recall",
                category = "Speed",
                description = "Solve 20 questions in under 30s each with 100% accuracy",
                emoji = "⚡",
                iconVector = Icons.Default.AutoAwesome,
                bgGradient = listOf(Color(0xFF64748B), Color(0xFF475569)),
                isUnlocked = false,
                progressCurrent = 12,
                progressMax = 20
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header + XP Badge
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardWhite,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, CardBorderLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("achievements_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    ) {
                        Text(
                            text = "Gamification & Achievements",
                            color = TextOnWhitePrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Level 8 · 1,850 XP earned",
                            color = SlateGray,
                            fontSize = 12.sp
                        )
                    }

                    // Level XP Pill
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEEF2FF))
                            .border(1.dp, VioletAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "1,850 XP",
                                color = VioletAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Tabs (Badges / Stats)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CardWhite,
                contentColor = VioletAccent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = VioletAccent
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.testTag("tab_badges"),
                    text = {
                        Text(
                            text = "Badges (${badgeList.count { it.isUnlocked }}/${badgeList.size})",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 0) VioletAccent else SlateGray
                        )
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.testTag("tab_stats"),
                    text = {
                        Text(
                            text = "Stats & Analytics",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 1) VioletAccent else SlateGray
                        )
                    }
                )
            }

            // Body Content based on Tab
            if (selectedTab == 0) {
                // Badges Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(badgeList, key = { it.id }) { badge ->
                        BadgeCard(badge = badge)
                    }
                }
            } else {
                // Stats Tab Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .padding(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // KPI Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatMetricCard("432", "Total Solved", "+28 this week", SuccessGreen, Modifier.weight(1f))
                        StatMetricCard("84.2%", "Accuracy", "Top 5% of class", VioletAccent, Modifier.weight(1f))
                        StatMetricCard("38s", "Avg Speed", "-4s improvement", BlueAccent, Modifier.weight(1f))
                    }

                    // Performance by subject
                    FloatingCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 16.dp,
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Subject Mastery Breakdown",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextOnWhitePrimary
                            )

                            SubjectProgressBar("Physics", 0.82f, "82% (142 questions)")
                            SubjectProgressBar("Chemistry", 0.88f, "88% (160 questions)")
                            SubjectProgressBar("Biology", 0.91f, "91% (130 questions)")
                        }
                    }
                }
            }
        }

        // Bottom Motivational Banner with a friendly mascot icon
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 16.dp,
            border = BorderStroke(1.dp, CardBorderLight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFEEF2FF), Color(0xFFFDF4FF))
                            )
                        )
                        .border(1.dp, Color(0xFFE0E7FF), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                        .testTag("motivational_mascot_banner"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Friendly Mascot Icon (Owl / Star Student avatar)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(VioletAccent, Color(0xFFD946EF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🦉", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Keep going, $userName!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextOnWhitePrimary
                        )
                        Text(
                            text = "You're in the top 5% of aspirants this week! 10 more MCQs to unlock Level 9.",
                            fontSize = 12.sp,
                            color = SlateGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeCard(badge: AchievementBadge) {
    FloatingCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("badge_card_${badge.id}"),
        cornerRadius = 18.dp,
        elevation = if (badge.isUnlocked) 2.dp else 0.dp,
        backgroundColor = if (badge.isUnlocked) Color.White else Color(0xFFF8FAFC),
        borderColor = if (badge.isUnlocked) CardBorderLight else Color(0xFFE2E8F0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge Circular Icon Tile
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(badge.bgGradient)),
                contentAlignment = Alignment.Center
            ) {
                if (badge.isUnlocked) {
                    Text(text = badge.emoji, fontSize = 26.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = badge.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (badge.isUnlocked) TextOnWhitePrimary else SlateGray
            )

            Text(
                text = badge.category,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = if (badge.isUnlocked) VioletAccent else SlateGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = badge.description,
                fontSize = 11.sp,
                color = SlateGray,
                lineHeight = 14.sp,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress indicator
            if (badge.isUnlocked) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Unlocked",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            } else {
                Text(
                    text = "${badge.progressCurrent}/${badge.progressMax} (${((badge.progressCurrent.toFloat() / badge.progressMax) * 100).toInt()}%)",
                    fontSize = 10.sp,
                    color = SlateGray
                )
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    value: String,
    label: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    FloatingCard(
        modifier = modifier,
        cornerRadius = 14.dp,
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextOnWhitePrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SlateGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

@Composable
private fun SubjectProgressBar(subject: String, progress: Float, details: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = subject, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextOnWhitePrimary)
            Text(text = details, fontSize = 11.sp, color = SlateGray)
        }
        GradientProgressBar(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}
