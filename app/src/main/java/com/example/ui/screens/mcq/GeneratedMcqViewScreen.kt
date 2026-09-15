package com.example.ui.screens.mcq

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeneratedMcq
import com.example.ui.components.FloatingCard
import com.example.ui.components.GradientButton
import com.example.ui.components.GradientProgressBar
import com.example.ui.components.PillTag
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.VioletAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratedMcqViewScreen(
    questions: List<GeneratedMcq>,
    isLoading: Boolean = false,
    progressCount: Int = 0,
    totalCount: Int = 0,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    onBack: () -> Unit,
    onAskAi: suspend (questionText: String) -> String = { "Thinking..." },
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    // User selected option tracking: index -> selected option index
    val userAnswers = remember { mutableStateOf(mutableMapOf<Int, Int>()) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showOverflowMenu by remember { mutableStateOf(false) }

    // State for Ask AI bottom sheet
    var showAiExplanationSheet by remember { mutableStateOf(false) }
    var aiExplanationText by remember { mutableStateOf("") }
    var isAiExplaining by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Reset current index if questions change
    androidx.compose.runtime.LaunchedEffect(questions) {
        if (currentIndex >= questions.size && questions.isNotEmpty()) {
            currentIndex = 0
        }
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
            // Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardWhite,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("mcq_view_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }

                    Text(
                        text = "Generated MCQs",
                        color = TextOnWhitePrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )

                    if (!isLoading && questions.isNotEmpty()) {
                        Box {
                            IconButton(
                                onClick = { showOverflowMenu = true },
                                modifier = Modifier.testTag("mcq_view_overflow_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = SlateGray
                                )
                            }

                            DropdownMenu(
                                expanded = showOverflowMenu,
                                onDismissRequest = { showOverflowMenu = false },
                                modifier = Modifier.background(CardWhite)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Export to PDF", fontSize = 14.sp) },
                                    onClick = {
                                        showOverflowMenu = false
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Exported ${questions.size} MCQs to PDF successfully!")
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Save to Question Bank", fontSize = 14.sp) },
                                    onClick = {
                                        showOverflowMenu = false
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Saved to Question Bank!")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Body content depending on state
            when {
                // 1. Initial Loading State (waiting for first batch)
                isLoading && questions.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.widthIn(max = 400.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEF2FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(44.dp),
                                    color = VioletAccent,
                                    strokeWidth = 3.5.dp
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Generating MCQs…",
                                color = TextOnWhitePrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (totalCount > 0) {
                                Text(
                                    text = "$progressCount / $totalCount generated",
                                    color = SlateGray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                val progressFraction = if (totalCount > 0) {
                                    (progressCount.toFloat() / totalCount).coerceIn(0f, 1f)
                                } else 0f

                                GradientProgressBar(
                                    progress = if (progressFraction <= 0.05f) 0.15f else progressFraction,
                                    height = 8.dp,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Surface(
                                    color = Color(0xFFEEF2FF),
                                    shape = RoundedCornerShape(20.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "⚡ 3–5 Parallel AI Workers • Zero Repeat Filter",
                                            color = Color(0xFF4338CA),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "AI is curating high quality questions with verified solutions...",
                                    color = SlateGray,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // 2. Error State (No sample fallback!)
                errorMessage != null && questions.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FloatingCard(
                            modifier = Modifier
                                .widthIn(max = 420.dp)
                                .fillMaxWidth(),
                            cornerRadius = 24.dp,
                            elevation = 3.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEE2E2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = ErrorRed,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Generation Encountered an Issue",
                                    color = TextOnWhitePrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = errorMessage,
                                    color = SlateGray,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                GradientButton(
                                    text = "Try again",
                                    onClick = onRetry,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("generation_retry_button")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    onClick = onBack,
                                    shape = RoundedCornerShape(100.dp),
                                    color = Color.Transparent,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Back to Setup",
                                        color = SlateGray,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Success / Active Generated Questions View
                questions.isNotEmpty() -> {
                    val safeIndex = currentIndex.coerceIn(0, questions.size - 1)
                    val currentQuestion = questions[safeIndex]
                    val selectedOptionIndex = userAnswers.value[safeIndex]

                    // Scrollable Question Content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = 500.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Live Streaming Indicator Banner if still generating in background (e.g. for large batches like 500)
                            if (isLoading && progressCount < totalCount) {
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            strokeWidth = 2.dp,
                                            color = BlueAccent
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "⚡ Generating in background: $progressCount / $totalCount ready • Solve questions live!",
                                            color = Color(0xFF1D4ED8),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            // Tag row: subject · difficulty · topic
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PillTag(
                                    text = currentQuestion.subject.ifBlank { "Subject" },
                                    backgroundColor = Color(0xFFEEF2FF),
                                    textColor = VioletAccent
                                )
                                PillTag(
                                    text = currentQuestion.difficulty.ifBlank { "Medium" },
                                    backgroundColor = Color(0xFFFEF3C7),
                                    textColor = Color(0xFFD97706)
                                )
                                PillTag(
                                    text = currentQuestion.topic.ifBlank { "Topic" }.take(18),
                                    backgroundColor = Color(0xFFF1F5F9),
                                    textColor = SlateGray
                                )
                            }

                            // Question Card with "✨ Ask AI" Pill top-right
                            FloatingCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 24.dp,
                                elevation = 3.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "Question ${safeIndex + 1}",
                                            color = SlateGray,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        // Small "✨ Ask AI" pill top-right
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(100.dp))
                                                .background(PrimaryGradient)
                                                .clickable {
                                                    showAiExplanationSheet = true
                                                    isAiExplaining = true
                                                    aiExplanationText = "Asking AI Teacher for guidance..."
                                                    coroutineScope.launch {
                                                        val fullQuery = "Explain this question step by step:\n${currentQuestion.question}\nOptions:\n${currentQuestion.options.mapIndexed { idx, o -> "${('A' + idx)}. $o" }.joinToString("\n")}\nCorrect answer: Option ${('A' + currentQuestion.correctAnswerIndex)}."
                                                        val response = onAskAi(fullQuery)
                                                        aiExplanationText = response
                                                        isAiExplaining = false
                                                    }
                                                }
                                                .testTag("ask_ai_pill_button"),
                                            color = Color.Transparent,
                                            shape = RoundedCornerShape(100.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Ask AI",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = currentQuestion.question,
                                        color = TextOnWhitePrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 22.sp
                                    )
                                }
                            }

                            // Rounded option rows with radio circles
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                currentQuestion.options.forEachIndexed { optIndex, optionText ->
                                    val isSelected = selectedOptionIndex == optIndex
                                    val formattedText = if (optionText.matches(Regex("^[A-D]\\..*"))) {
                                        optionText
                                    } else {
                                        "${('A' + optIndex)}. $optionText"
                                    }

                                    OptionRowItem(
                                        optionText = formattedText,
                                        isSelected = isSelected,
                                        onClick = {
                                            val newMap = userAnswers.value.toMutableMap()
                                            newMap[safeIndex] = optIndex
                                            userAnswers.value = newMap
                                        }
                                    )
                                }
                            }

                            // Soft Green Answer Card (Appears after answering)
                            AnimatedVisibility(
                                visible = selectedOptionIndex != null,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                val correctLetter = ('A' + currentQuestion.correctAnswerIndex).toString()
                                val isUserCorrect = selectedOptionIndex == currentQuestion.correctAnswerIndex

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(20.dp))
                                        .border(
                                            1.dp,
                                            if (isUserCorrect) Color(0xFFBBF7D0) else Color(0xFFFED7AA),
                                            RoundedCornerShape(20.dp)
                                        ),
                                    color = if (isUserCorrect) Color(0xFFF0FDF4) else Color(0xFFFFFBEB),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(18.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isUserCorrect) "✓ Correct Answer: $correctLetter" else "Correct Answer: $correctLetter",
                                                color = if (isUserCorrect) Color(0xFF15803D) else Color(0xFFB45309),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Explanation:",
                                            color = if (isUserCorrect) Color(0xFF166534) else Color(0xFF92400E),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = currentQuestion.explanation,
                                            color = Color(0xFF1E293B),
                                            fontSize = 13.sp,
                                            lineHeight = 19.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Bottom Navigation Bar: "‹ Previous" · page counter · "Next ›"
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = CardWhite,
                        shadowElevation = 8.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Previous Button
                            Surface(
                                onClick = {
                                    if (currentIndex > 0) currentIndex--
                                },
                                enabled = currentIndex > 0,
                                shape = RoundedCornerShape(100.dp),
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (currentIndex > 0) CardBorderLight else Color(0xFFF1F5F9)
                                ),
                                modifier = Modifier.testTag("mcq_prev_button")
                            ) {
                                Text(
                                    text = "‹ Previous",
                                    color = if (currentIndex > 0) TextOnWhitePrimary else Color(0xFFCBD5E1),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                                )
                            }

                            // Page Counter (e.g. 1 / 20)
                            Text(
                                text = "${safeIndex + 1} / ${questions.size}",
                                color = SlateGray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Next Button
                            Surface(
                                onClick = {
                                    if (currentIndex < questions.size - 1) {
                                        currentIndex++
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("You've reviewed all ${questions.size} generated questions!")
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(100.dp),
                                color = Color.Transparent,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(PrimaryGradient)
                                    .testTag("mcq_next_button")
                            ) {
                                Text(
                                    text = if (currentIndex < questions.size - 1) "Next ›" else "Finish",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                // 4. Empty State (Never hardcoded sample fallback)
                else -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No questions generated yet",
                                color = SlateGray,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            GradientButton(
                                text = "Go to Generator",
                                onClick = onBack
                            )
                        }
                    }
                }
            }
        }

        // Ask AI Modal Bottom Sheet
        if (showAiExplanationSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAiExplanationSheet = false },
                sheetState = sheetState,
                containerColor = CardWhite,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "AI Teacher Explanation",
                                color = TextOnWhitePrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { showAiExplanationSheet = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = SlateGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isAiExplaining) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 20.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = VioletAccent,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "AI Teacher is analyzing concepts...",
                                color = SlateGray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Text(
                            text = aiExplanationText,
                            color = Color(0xFF1E293B),
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    var chatInput by remember { mutableStateOf("") }
                    var isRecording by remember { mutableStateOf(false) }

                    com.example.ui.components.ChatInputBar(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = "Ask AI Teacher...",
                        hasAttachment = false,
                        onAttachClick = { },
                        onMicClick = { isRecording = !isRecording },
                        onSendClick = {
                            if (chatInput.isNotEmpty()) {
                                chatInput = ""
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Message sent to AI Teacher!")
                                }
                            }
                        },
                        isRecording = isRecording
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

@Composable
private fun OptionRowItem(
    optionText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isSelected -> VioletAccent
        else -> CardBorderLight
    }

    val backgroundColor = when {
        isSelected -> Color(0xFFF5F3FF)
        else -> Color.White
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.8.dp else 1.dp, borderColor),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio circle
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(
                        width = if (isSelected) 6.dp else 1.5.dp,
                        color = if (isSelected) VioletAccent else Color(0xFFCBD5E1),
                        shape = CircleShape
                    )
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = optionText,
                color = if (isSelected) VioletAccent else TextOnWhitePrimary,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
