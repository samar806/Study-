package com.example.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingCard
import com.example.ui.components.GradientButton
import com.example.ui.components.GradientProgressBar
import com.example.ui.components.PulseFlameIcon
import com.example.ui.navigation.AppScreen
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.HeroCardGradient
import com.example.ui.theme.ImageBadgeBg
import com.example.ui.theme.PdfBadgeBg
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.PromptBadgeBg
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextOnNavyPrimary
import com.example.ui.theme.TextOnNavySecondary
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VioletAccent
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    userName: String = "Aarav",
    onNavigateScreen: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp) // space for bottom nav
        ) {
            // Navy Header Section with Greeting & Profile
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0B1224), Color(0xFF131D3B))
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Background ambient circles
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawCircle(
                        color = Color(0x0AFFFFFF),
                        radius = size.width * 0.45f,
                        center = Offset(size.width * 0.9f, size.height * 0.2f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Greeting
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Good Morning, $userName! 👋",
                                color = TextOnNavyPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ready to learn something new?",
                            color = TextOnNavySecondary,
                            fontSize = 13.sp
                        )
                    }

                    // Bell icon with dot + User Avatar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Bell with unread dot
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x26FFFFFF))
                                .clickable {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("You have 3 new study alerts!")
                                    }
                                }
                                .testTag("notification_bell_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            // Red/orange notification dot
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 8.dp, end = 8.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ErrorRed)
                            )
                        }

                        // User Avatar
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(PrimaryGradient)
                                .clickable {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Logged in as $userName")
                                    }
                                }
                                .testTag("user_avatar_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                // 1. Hero Gradient Card (Largest element)
                HeroGeneratorCard(
                    onGenerateClick = {
                        onNavigateScreen(AppScreen.MCQ_GENERATOR_SETUP)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. "Create from" Section Header
                Text(
                    text = "Create from",
                    color = TextOnWhitePrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Row of 3 mini-cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CreateFromCard(
                        emoji = "✍️",
                        title = "From Prompt",
                        subtitle = "Type what you want",
                        badgeBg = PromptBadgeBg,
                        iconTint = Color(0xFFD946EF),
                        iconVector = Icons.Default.Edit,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onNavigateScreen(AppScreen.PROMPT_GENERATION)
                        }
                    )

                    CreateFromCard(
                        emoji = "📷",
                        title = "From Image",
                        subtitle = "Upload or capture",
                        badgeBg = ImageBadgeBg,
                        iconTint = SuccessGreen,
                        iconVector = Icons.Default.PhotoCamera,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onNavigateScreen(AppScreen.IMAGE_GENERATION)
                        }
                    )

                    CreateFromCard(
                        emoji = "📄",
                        title = "From PDF/Notes",
                        subtitle = "Upload documents",
                        badgeBg = PdfBadgeBg,
                        iconTint = BlueAccent,
                        iconVector = Icons.Default.Description,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onNavigateScreen(AppScreen.PROMPT_GENERATION)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. "Continue Learning" Card
                Text(
                    text = "Continue Learning",
                    color = TextOnWhitePrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                ContinueLearningCard(
                    subject = "Physics",
                    chapter = "Motion in a Straight Line",
                    questionCount = 20,
                    progressPercent = 75,
                    onContinueClick = {
                        onNavigateScreen(AppScreen.GENERATED_MCQ_VIEW)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 4. Daily Challenge Banner
                DailyChallengeBanner(
                    questionsCount = 10,
                    streakDays = 5,
                    onStartClick = {
                        onNavigateScreen(AppScreen.QUIZ_INTERFACE)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Prototype Quick Screen Switcher for all 8 screens
                FloatingCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    backgroundColor = Color(0xFFF1F5F9),
                    elevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💡 Prototype Screen Switcher",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateGray
                            )
                            Text(
                                text = "8 Screens Ready",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VioletAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        // Row 1: Setup & Initial screens
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SwitcherChip(
                                label = "1. Login",
                                onClick = { onNavigateScreen(AppScreen.LOGIN) },
                                modifier = Modifier.weight(1f)
                            )
                            SwitcherChip(
                                label = "2. Onboard",
                                onClick = { onNavigateScreen(AppScreen.ONBOARDING) },
                                modifier = Modifier.weight(1f)
                            )
                            SwitcherChip(
                                label = "3. Home",
                                isActive = true,
                                onClick = { onNavigateScreen(AppScreen.MAIN_SHELL) },
                                modifier = Modifier.weight(1f)
                            )
                            SwitcherChip(
                                label = "4. Setup",
                                onClick = { onNavigateScreen(AppScreen.MCQ_GENERATOR_SETUP) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Row 2: Generator & Practice screens
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SwitcherChip(
                                label = "5. Prompt",
                                onClick = { onNavigateScreen(AppScreen.PROMPT_GENERATION) },
                                modifier = Modifier.weight(1f)
                            )
                            SwitcherChip(
                                label = "6. Image",
                                onClick = { onNavigateScreen(AppScreen.IMAGE_GENERATION) },
                                modifier = Modifier.weight(1f)
                            )
                            SwitcherChip(
                                label = "7. MCQs",
                                onClick = { onNavigateScreen(AppScreen.GENERATED_MCQ_VIEW) },
                                modifier = Modifier.weight(1f)
                            )
                            SwitcherChip(
                                label = "8. Quiz",
                                onClick = { onNavigateScreen(AppScreen.QUIZ_INTERFACE) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )
    }
}

@Composable
private fun SwitcherChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .then(
                if (isActive) Modifier.background(PrimaryGradient)
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        color = if (isActive) Color.Transparent else Color.White,
        shape = RoundedCornerShape(100.dp),
        border = if (isActive) null else androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isActive) Color.White else VioletAccent,
            maxLines = 1,
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 6.dp)
        )
    }
}

/**
 * 1. Hero Gradient Card (Largest element)
 */
@Composable
fun HeroGeneratorCard(
    onGenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = VioletAccent.copy(alpha = 0.4f),
                spotColor = BlueAccent.copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(HeroCardGradient)
                .padding(22.dp)
        ) {
            // Decorative star sparkles in corner
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color(0x1AFFFFFF),
                    radius = 80.dp.toPx(),
                    center = Offset(size.width * 0.95f, size.height * 0.1f)
                )
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "✨ AI MCQ Generator",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Create high-quality MCQs from any topic, text, book, notes, image or PDF.",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // White pill button: "Generate MCQs"
                Surface(
                    onClick = onGenerateClick,
                    shape = RoundedCornerShape(100.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .testTag("hero_generate_mcqs_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 11.dp)
                    ) {
                        Text(
                            text = "Generate MCQs",
                            color = Color(0xFF1E293B),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 2. Mini Card for "Create from"
 */
@Composable
fun CreateFromCard(
    emoji: String,
    title: String,
    subtitle: String,
    badgeBg: Color,
    iconTint: Color,
    iconVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingCard(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("create_from_${title.replace(" ", "_").lowercase()}"),
        cornerRadius = 20.dp,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                color = TextOnWhitePrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = SlateGray,
                fontSize = 10.sp,
                lineHeight = 13.sp
            )
        }
    }
}

/**
 * 3. "Continue Learning" Card
 */
@Composable
fun ContinueLearningCard(
    subject: String,
    chapter: String,
    questionCount: Int,
    progressPercent: Int,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Subject Icon Badge (e.g. Science / Physics)
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEEF2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = VioletAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$subject · $chapter",
                        color = TextOnWhitePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$questionCount questions",
                            color = SlateGray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = " • ",
                            color = SlateGray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "$progressPercent%",
                            color = SuccessGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Continue Button
                GradientButton(
                    text = "Continue",
                    onClick = onContinueClick,
                    modifier = Modifier.testTag("continue_learning_button")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mini progress indicator
            GradientProgressBar(
                progress = progressPercent / 100f,
                height = 5.dp
            )
        }
    }
}

/**
 * 4. Daily Challenge Banner with pulsing flame icon
 */
@Composable
fun DailyChallengeBanner(
    questionsCount: Int,
    streakDays: Int,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingCard(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFFED7AA), RoundedCornerShape(20.dp)),
        cornerRadius = 20.dp,
        backgroundColor = Color(0xFFFFFBEB),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Pulsing Flame Icon
                PulseFlameIcon(size = 32.dp)

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Daily Challenge",
                        color = Color(0xFF9A3412),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$questionsCount questions • $streakDays days streak",
                        color = Color(0xFFC2410C),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Start Button
            Surface(
                onClick = onStartClick,
                shape = RoundedCornerShape(100.dp),
                color = Color(0xFFEA580C),
                shadowElevation = 2.dp,
                modifier = Modifier.testTag("start_daily_challenge_button")
            ) {
                Text(
                    text = "Start",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp)
                )
            }
        }
    }
}
