package com.example.ui.screens.quiz

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingCard
import com.example.ui.components.GradientButton
import com.example.ui.components.GradientProgressBar
import com.example.ui.components.PillTag
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.NavyBackgroundGradient
import com.example.ui.theme.NavyDark
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TextOnNavyPrimary
import com.example.ui.theme.TextOnNavySecondary
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.VioletAccent
import com.example.ui.theme.WarningOrange
import androidx.compose.material.icons.filled.Menu
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.data.model.GeneratedMcq
import com.example.data.repository.DailyChallengeRepository

data class QuizQuestion(
    val id: Int,
    val subject: String,
    val difficulty: String,
    val topic: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int = 0,
    val explanation: String = ""
)

@Composable
fun QuizInterfaceScreen(
    questions: List<GeneratedMcq>? = null,
    initialAnswers: Map<Int, Int> = emptyMap(),
    initialIndex: Int = 0,
    isDailyChallenge: Boolean = false,
    isReviewMode: Boolean = false,
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    onQuizComplete: () -> Unit = onBack,
    modifier: Modifier = Modifier
) {
    val quizQuestions = remember(questions) {
        if (!questions.isNullOrEmpty()) {
            questions.mapIndexed { idx, q ->
                QuizQuestion(
                    id = idx + 1,
                    subject = q.subject,
                    difficulty = q.difficulty,
                    topic = q.topic,
                    question = q.question,
                    options = q.options.mapIndexed { optIdx, opt ->
                        if (opt.matches(Regex("^[A-D]\\..*"))) opt else "${('A' + optIdx)}. $opt"
                    },
                    correctAnswerIndex = q.correctAnswerIndex,
                    explanation = q.explanation
                )
            }
        } else {
            listOf(
                QuizQuestion(
                    id = 1,
                    subject = "Chemistry",
                    difficulty = "Medium",
                    topic = "Organic Chemistry",
                    question = "Which of the following functional groups is present in carboxylic acids?",
                    options = listOf("A. -OH", "B. -COOH", "C. -NH₂", "D. -CHO"),
                    correctAnswerIndex = 1,
                    explanation = "Carboxylic acids contain the -COOH group (carbonyl + hydroxyl)."
                ),
                QuizQuestion(
                    id = 2,
                    subject = "Chemistry",
                    difficulty = "Medium",
                    topic = "Thermodynamics",
                    question = "For an isolated system, the change in internal energy (ΔU) in any process is always:",
                    options = listOf("A. Positive", "B. Negative", "C. Zero", "D. Dependent on path"),
                    correctAnswerIndex = 2,
                    explanation = "An isolated system cannot exchange energy or matter with surroundings, so ΔU = 0."
                ),
                QuizQuestion(
                    id = 3,
                    subject = "Chemistry",
                    difficulty = "Hard",
                    topic = "Chemical Bonding",
                    question = "Which of the following molecules possesses a zero dipole moment due to symmetric geometry?",
                    options = listOf("A. NH₃", "B. H₂O", "C. BF₃", "D. SO₂"),
                    correctAnswerIndex = 2,
                    explanation = "BF3 has trigonal planar geometry where bond dipoles cancel out completely."
                )
            )
        }
    }

    var currentIndex by remember(initialIndex) { mutableIntStateOf(initialIndex.coerceIn(0, (quizQuestions.size - 1).coerceAtLeast(0))) }
    val currentQuestion = quizQuestions[currentIndex]

    // Answers and reviews map
    val selectedAnswers = remember(initialAnswers) { mutableStateOf(initialAnswers.toMutableMap()) }
    val markedForReview = remember { mutableStateOf(mutableSetOf<Int>()) }

    var showCompletionDialog by remember { mutableStateOf(false) }
    var completedScore by remember { mutableIntStateOf(0) }
    var completedStreak by remember { mutableIntStateOf(0) }

    // Live countdown timer: starting at 29 minutes 45 seconds (1785 seconds)
    var remainingSeconds by remember { mutableIntStateOf(29 * 60 + 45) }

    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timerString = String.format("%02d:%02d", minutes, seconds)

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val isCurrentMarked = markedForReview.value.contains(currentIndex)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Deep calm navy canvas for focus quiz mode
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header with countdown timer
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1E293B),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("quiz_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Exit Quiz",
                            tint = Color.White
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
                                tint = Color.White
                            )
                        }
                    }

                    Text(
                        text = "Quiz in Progress",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )

                    // Live countdown timer pill
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0x33FFFFFF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (remainingSeconds < 120) Color(0xFFEF4444) else Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = timerString,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Position count & Thin Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentIndex + 1} / ${quizQuestions.size}",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (isCurrentMarked) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = WarningOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Marked for review",
                                color = WarningOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Thin progress bar showing position
                GradientProgressBar(
                    progress = (currentIndex + 1f) / quizQuestions.size,
                    height = 4.dp
                )
            }

            // Scrollable Question Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tag row: Chemistry · Medium · Organic Chemistry
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PillTag(
                            text = currentQuestion.subject,
                            backgroundColor = Color(0x2638BDF8),
                            textColor = Color(0xFF38BDF8)
                        )
                        PillTag(
                            text = currentQuestion.difficulty,
                            backgroundColor = Color(0x26FBBF24),
                            textColor = Color(0xFFFBBF24)
                        )
                        PillTag(
                            text = currentQuestion.topic,
                            backgroundColor = Color(0x26A855F7),
                            textColor = Color(0xFFC084FC)
                        )
                    }

                    // Question Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = currentQuestion.question,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    // 4 Options
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val selectedOpt = selectedAnswers.value[currentIndex]
                        currentQuestion.options.forEachIndexed { optIndex, optionText ->
                            val isSelected = selectedOpt == optIndex
                            val isCorrectOpt = isReviewMode && optIndex == currentQuestion.correctAnswerIndex
                            val isWrongSelected = isReviewMode && isSelected && optIndex != currentQuestion.correctAnswerIndex

                            val optBgColor = when {
                                isCorrectOpt -> Color(0xFF14532D)
                                isWrongSelected -> Color(0xFF7F1D1D)
                                isSelected -> Color(0xFF2E1065).copy(alpha = 0.85f)
                                else -> Color(0xFF1E293B)
                            }

                            val optBorderColor = when {
                                isCorrectOpt -> Color(0xFF22C55E)
                                isWrongSelected -> Color(0xFFEF4444)
                                isSelected -> VioletAccent
                                else -> Color(0xFF334155)
                            }

                            Surface(
                                onClick = {
                                    if (!isReviewMode) {
                                        val newMap = selectedAnswers.value.toMutableMap()
                                        newMap[currentIndex] = optIndex
                                        selectedAnswers.value = newMap
                                        if (isDailyChallenge) {
                                            DailyChallengeRepository.saveAnswer(currentIndex, optIndex)
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(18.dp),
                                color = optBgColor,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected || isCorrectOpt || isWrongSelected) 2.dp else 1.dp,
                                    optBorderColor
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp, vertical = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Radio circle or check/cross icon
                                    if (isCorrectOpt) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Correct",
                                            tint = Color(0xFF22C55E),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    width = if (isSelected) 6.dp else 1.5.dp,
                                                    color = if (isSelected) VioletAccent else Color(0xFF64748B),
                                                    shape = CircleShape
                                                )
                                                .background(if (isSelected) Color.White else Color.Transparent)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Text(
                                        text = optionText,
                                        color = if (isSelected || isCorrectOpt) Color.White else Color(0xFFE2E8F0),
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected || isCorrectOpt) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Explanation Box in Review mode
                    if (isReviewMode && currentQuestion.explanation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Explanation",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentQuestion.explanation,
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Bottom bar: outline "🚩 Mark for Review" + filled "Next"
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1E293B),
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Outline "🚩 Mark for Review"
                    Surface(
                        onClick = {
                            val newSet = markedForReview.value.toMutableSet()
                            if (newSet.contains(currentIndex)) {
                                newSet.remove(currentIndex)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Removed review mark.")
                                }
                            } else {
                                newSet.add(currentIndex)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Question marked for review.")
                                }
                            }
                            markedForReview.value = newSet
                        },
                        shape = RoundedCornerShape(100.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isCurrentMarked) WarningOrange else Color(0xFF64748B)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quiz_mark_for_review_button")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = if (isCurrentMarked) Icons.Default.Flag else Icons.Outlined.Flag,
                                contentDescription = null,
                                tint = if (isCurrentMarked) WarningOrange else Color(0xFFCBD5E1),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCurrentMarked) "Marked" else "Mark for Review",
                                color = if (isCurrentMarked) WarningOrange else Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Filled "Next" / "Submit Quiz" Button
                    Surface(
                        onClick = {
                            if (currentIndex < quizQuestions.size - 1) {
                                currentIndex++
                            } else {
                                if (isDailyChallenge && !isReviewMode) {
                                    completedScore = DailyChallengeRepository.completeChallenge()
                                    completedStreak = DailyChallengeRepository.getCurrentStreak()
                                    showCompletionDialog = true
                                } else {
                                    onQuizComplete()
                                }
                            }
                        },
                        shape = RoundedCornerShape(100.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(100.dp))
                            .background(PrimaryGradient)
                            .testTag("quiz_next_button")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (currentIndex < quizQuestions.size - 1) "Next" else if (isReviewMode) "Finish Review" else "Submit Quiz",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (showCompletionDialog) {
            AlertDialog(
                onDismissRequest = {
                    showCompletionDialog = false
                    onQuizComplete()
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = WarningOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily Challenge Completed!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Great job finishing today's challenge!",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Score Earned: $completedScore / ${quizQuestions.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF10B981)
                        )
                        Text(
                            text = "Current Streak: $completedStreak Days 🔥",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFF59E0B)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showCompletionDialog = false
                            onQuizComplete()
                        }
                    ) {
                        Text("Continue to Home", fontWeight = FontWeight.Bold, color = VioletAccent)
                    }
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )
    }
}
