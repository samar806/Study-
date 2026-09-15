package com.example.ui.screens.challenge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VioletAccent
import com.example.ui.theme.WarningOrange
import androidx.compose.material.icons.filled.Menu

import com.example.data.repository.DailyChallengeRepository

@Composable
fun DailyChallengeScreen(
    onBack: () -> Unit,
    onStartChallenge: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val state = DailyChallengeRepository.currentChallengeState
    val streakCount = DailyChallengeRepository.getCurrentStreak()
    val weekDays = DailyChallengeRepository.getLast7CalendarDays()

    // Pulsing flame animation for fire icon
    val infiniteTransition = rememberInfiniteTransition(label = "flame_pulse")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameScale"
    )

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
            // Header
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
                        modifier = Modifier.testTag("daily_challenge_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }

                    if (onOpenDrawer != null) {
                        IconButton(
                            onClick = onOpenDrawer,
                            modifier = Modifier.testTag("hamburger_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color(0xFF1E293B)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    ) {
                        Text(
                            text = "Daily Challenge",
                            color = TextOnWhitePrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Solve today's set to keep your streak alive",
                            color = SlateGray,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = WarningOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$streakCount Days",
                                color = Color(0xFFB45309),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Scrollable Body
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. FEATURE CARD: fire icon, "Today's Challenge", bold question count, dynamic button
                FloatingCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("today_challenge_feature_card"),
                    cornerRadius = 24.dp,
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (state?.isCompleted == true) Color(0x3310B981) else Color(0x33F59E0B)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocalFireDepartment,
                                            contentDescription = null,
                                            tint = if (state?.isCompleted == true) SuccessGreen else WarningOrange,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = when {
                                                state?.isCompleted == true -> "COMPLETED TODAY"
                                                state?.userAnswers?.isNotEmpty() == true -> "IN PROGRESS"
                                                else -> "TODAY'S CHALLENGE"
                                            },
                                            color = if (state?.isCompleted == true) Color(0xFFA7F3D0) else Color(0xFFFDE68A),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = if (state?.isCompleted == true) "Today's Challenge Complete" else "Physics & Chemistry Sprint",
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = when {
                                        state?.isCompleted == true -> "Score: ${state?.score ?: 0} / ${state?.questions?.size ?: 10} • Great job!"
                                        state?.userAnswers?.isNotEmpty() == true -> "${state?.userAnswers?.size ?: 0} / ${state?.questions?.size ?: 10} Questions Answered"
                                        else -> "Laws of Motion & Chemical Kinetics"
                                    },
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 13.sp
                                )
                            }

                            // Glowing Animated Fire Icon
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            if (state?.isCompleted == true) listOf(Color(0xFF10B981), Color(0xFF047857))
                                            else listOf(Color(0xFFFF7A00), Color(0xFFFF3D00))
                                        )
                                    )
                                    .scale(flameScale),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (state?.isCompleted == true) Icons.Default.Check else Icons.Default.LocalFireDepartment,
                                    contentDescription = "Status icon",
                                    tint = Color.White,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Bold question count + metrics
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x26FFFFFF))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${state?.questions?.size ?: 10} Questions",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "High-Yield NEET/JEE",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(30.dp)
                                    .background(Color(0x33FFFFFF))
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "15 Mins",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Time Target",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(30.dp)
                                    .background(Color(0x33FFFFFF))
                            )

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "+50 XP",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Reward",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Dynamic Action Button
                        GradientButton(
                            text = when {
                                state?.isCompleted == true -> "Review Answers"
                                state?.userAnswers?.isNotEmpty() == true -> "Continue Challenge"
                                else -> "Start Now"
                            },
                            onClick = onStartChallenge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("daily_challenge_start_button"),
                            leadingIcon = {
                                Icon(
                                    imageVector = if (state?.isCompleted == true) Icons.Default.Check else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                    }
                }

                // 2. "YOUR STREAK" SECTION: day count + 7 circular day markers (Mon–Sun)
                FloatingCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("your_streak_card"),
                    cornerRadius = 20.dp,
                    elevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = WarningOrange,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Your Streak",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextOnWhitePrimary
                                    )
                                }

                                Text(
                                    text = "Practice today to reach 6 days in a row!",
                                    fontSize = 12.sp,
                                    color = SlateGray
                                )
                            }

                            // Day Count
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFEF3C7))
                                    .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$streakCount Days 🔥",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }

                        // 7 Circular Day Markers (Mon–Sun)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            weekDays.forEach { day ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .then(
                                                when {
                                                    day.isCompleted -> Modifier.background(SuccessGreen)
                                                    day.isToday -> Modifier
                                                        .background(Color(0xFFEEF2FF))
                                                        .border(2.dp, VioletAccent, CircleShape)
                                                    else -> Modifier
                                                        .background(Color(0xFFF1F5F9))
                                                        .border(1.dp, CardBorderLight, CircleShape)
                                                }
                                            )
                                            .testTag("streak_marker_${day.dayLabel}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when {
                                            day.isCompleted -> {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Completed",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            day.isToday -> {
                                                Text(
                                                    text = "🔥",
                                                    fontSize = 16.sp
                                                )
                                            }
                                            else -> {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFFCBD5E1))
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = day.dayLabel,
                                        fontSize = 11.sp,
                                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                                        color = if (day.isToday) VioletAccent else SlateGray
                                    )
                                }
                            }
                        }
                    }
                }

                // Today's Leaderboard Preview
                FloatingCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    elevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Today's Top Performers",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextOnWhitePrimary
                                )
                            }

                            Text(
                                text = "Live",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }

                        listOf(
                            Triple("1. Priya S.", "10/10 in 7m 20s", "🥇 100 XP"),
                            Triple("2. Rohan M.", "10/10 in 8m 45s", "🥈 80 XP"),
                            Triple("3. Aarav (You)", "Pending Challenge", "🔥 +50 XP ready")
                        ).forEach { (rankName, score, badge) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = rankName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextOnWhitePrimary
                                    )
                                    Text(
                                        text = score,
                                        fontSize = 11.sp,
                                        color = SlateGray
                                    )
                                }

                                Text(
                                    text = badge,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VioletAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
