package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.GeneratedMcq
import com.example.data.service.GeminiMcqService
import com.example.ui.navigation.AppScreen
import com.example.ui.navigation.DesktopSidebar
import com.example.ui.navigation.MobileBottomBar
import com.example.ui.navigation.NavigationTab
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.TabPlaceholderScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.theme.AIMCQTeacherTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIMCQTeacherApp()
        }
    }
}

@Composable
fun AIMCQTeacherApp() {
    var isDarkTheme by remember { mutableStateOf(true) }
    var currentScreen by remember { mutableStateOf(AppScreen.MAIN_SHELL) }
    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }

    // User profile state initialized with realistic data from prompt & attachments
    var userName by remember { mutableStateOf("Aarav") }
    var userExam by remember { mutableStateOf("NEET") }
    var userGrade by remember { mutableStateOf("Class 11") }
    var userSubjects by remember {
        mutableStateOf(listOf("Physics", "Chemistry", "Biology"))
    }

    // Live AI Generation State
    val geminiService = remember { GeminiMcqService() }
    val coroutineScope = rememberCoroutineScope()
    var generatedQuestions by remember { mutableStateOf<List<GeneratedMcq>>(emptyList()) }
    var isGenerating by remember { mutableStateOf(false) }
    var generationProgressCount by remember { mutableIntStateOf(0) }
    var generationTotalCount by remember { mutableIntStateOf(0) }
    var generationError by remember { mutableStateOf<String?>(null) }
    var lastGenerationAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    AIMCQTeacherTheme(darkTheme = isDarkTheme) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isDesktopOrTablet = maxWidth >= 600.dp

            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.LOGIN -> {
                        LoginScreen(
                            onLoginSuccess = { email ->
                                if (email.contains("@")) {
                                    val extractedName = email.substringBefore("@")
                                        .replaceFirstChar { it.uppercase() }
                                    if (extractedName.isNotBlank() && extractedName != "Priti") {
                                        userName = extractedName
                                    }
                                }
                                currentScreen = AppScreen.ONBOARDING
                            }
                        )
                    }

                    AppScreen.ONBOARDING -> {
                        OnboardingScreen(
                            onComplete = { name, exam, grade, subjects ->
                                userName = name
                                userExam = exam
                                userGrade = grade
                                userSubjects = subjects
                                currentScreen = AppScreen.MAIN_SHELL
                                currentTab = NavigationTab.HOME
                            },
                            onBack = {
                                currentScreen = AppScreen.LOGIN
                            }
                        )
                    }

                    AppScreen.MAIN_SHELL -> {
                        if (isDesktopOrTablet) {
                            // Desktop / Tablet Layout with Left Sidebar
                            Row(modifier = Modifier.fillMaxSize()) {
                                DesktopSidebar(
                                    currentTab = currentTab,
                                    onTabSelected = { currentTab = it },
                                    onNavigateScreen = { currentScreen = it }
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                ) {
                                    MainShellContent(
                                        currentTab = currentTab,
                                        userName = userName,
                                        userExam = userExam,
                                        userGrade = userGrade,
                                        isDarkTheme = isDarkTheme,
                                        onToggleDarkTheme = { isDarkTheme = !isDarkTheme },
                                        onNavigateScreen = { currentScreen = it }
                                    )
                                }
                            }
                        } else {
                            // Mobile Layout with Fixed Bottom Tab Bar
                            Box(modifier = Modifier.fillMaxSize()) {
                                MainShellContent(
                                    currentTab = currentTab,
                                    userName = userName,
                                    userExam = userExam,
                                    userGrade = userGrade,
                                    isDarkTheme = isDarkTheme,
                                    onToggleDarkTheme = { isDarkTheme = !isDarkTheme },
                                    onNavigateScreen = { currentScreen = it }
                                )

                                MobileBottomBar(
                                    currentTab = currentTab,
                                    onTabSelected = { currentTab = it },
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                )
                            }
                        }
                    }

                    AppScreen.MCQ_GENERATOR_SETUP -> {
                        com.example.ui.screens.generator.McqGeneratorSetupScreen(
                            onBack = { currentScreen = AppScreen.MAIN_SHELL },
                            onGenerate = { className, subject, chapter, scope, specificTopic, difficulty, count ->
                                currentScreen = AppScreen.GENERATED_MCQ_VIEW
                                val executeAction: () -> Unit = {
                                    isGenerating = true
                                    generatedQuestions = emptyList()
                                    generationProgressCount = 0
                                    generationTotalCount = count
                                    generationError = null
                                    coroutineScope.launch {
                                        val result = geminiService.generateFromSetup(
                                            className = className,
                                            subject = subject,
                                            topic = chapter,
                                            scope = scope,
                                            specificTopic = specificTopic,
                                            difficulty = difficulty,
                                            totalCount = count,
                                            onProgress = { current, total ->
                                                generationProgressCount = current
                                                generationTotalCount = total
                                            },
                                            onBatchReceived = { batchList ->
                                                generatedQuestions = batchList
                                            }
                                        )
                                        isGenerating = false
                                        if (result.isSuccess) {
                                            generatedQuestions = result.getOrNull().orEmpty()
                                            generationError = null
                                        } else {
                                            if (generatedQuestions.isEmpty()) {
                                                generationError = result.exceptionOrNull()?.message
                                                    ?: "Failed to generate MCQs. Please try again."
                                            }
                                        }
                                    }
                                }
                                lastGenerationAction = executeAction
                                executeAction()
                            }
                        )
                    }

                    AppScreen.PROMPT_GENERATION -> {
                        com.example.ui.screens.generator.PromptGenerationScreen(
                            onBack = { currentScreen = AppScreen.MAIN_SHELL },
                            onGenerate = { prompt, questionType, language, includeExplanations, includeNumericals, examPattern, count ->
                                currentScreen = AppScreen.GENERATED_MCQ_VIEW
                                val executeAction: () -> Unit = {
                                    isGenerating = true
                                    generatedQuestions = emptyList()
                                    generationProgressCount = 0
                                    generationTotalCount = count
                                    generationError = null
                                    coroutineScope.launch {
                                        val result = geminiService.generateFromPrompt(
                                            userPrompt = prompt,
                                            questionType = questionType,
                                            language = language,
                                            includeExplanations = includeExplanations,
                                            includeNumericals = includeNumericals,
                                            examPattern = examPattern,
                                            totalCount = count,
                                            onProgress = { current, total ->
                                                generationProgressCount = current
                                                generationTotalCount = total
                                            },
                                            onBatchReceived = { batchList ->
                                                generatedQuestions = batchList
                                            }
                                        )
                                        isGenerating = false
                                        if (result.isSuccess) {
                                            generatedQuestions = result.getOrNull().orEmpty()
                                            generationError = null
                                        } else {
                                            if (generatedQuestions.isEmpty()) {
                                                generationError = result.exceptionOrNull()?.message
                                                    ?: "Failed to generate MCQs from prompt. Please try again."
                                            }
                                        }
                                    }
                                }
                                lastGenerationAction = executeAction
                                executeAction()
                            }
                        )
                    }

                    AppScreen.IMAGE_GENERATION -> {
                        com.example.ui.screens.generator.ImageGenerationScreen(
                            onBack = { currentScreen = AppScreen.MAIN_SHELL },
                            onGenerate = { pageDescriptions, difficulty, count ->
                                currentScreen = AppScreen.GENERATED_MCQ_VIEW
                                val executeAction: () -> Unit = {
                                    isGenerating = true
                                    generatedQuestions = emptyList()
                                    generationProgressCount = 0
                                    generationTotalCount = count
                                    generationError = null
                                    coroutineScope.launch {
                                        val result = geminiService.generateFromImage(
                                            pageDescriptions = pageDescriptions,
                                            difficulty = difficulty,
                                            totalCount = count,
                                            onProgress = { current, total ->
                                                generationProgressCount = current
                                                generationTotalCount = total
                                            },
                                            onBatchReceived = { batchList ->
                                                generatedQuestions = batchList
                                            }
                                        )
                                        isGenerating = false
                                        if (result.isSuccess) {
                                            generatedQuestions = result.getOrNull().orEmpty()
                                            generationError = null
                                        } else {
                                            if (generatedQuestions.isEmpty()) {
                                                generationError = result.exceptionOrNull()?.message
                                                    ?: "Failed to generate MCQs from notes. Please try again."
                                            }
                                        }
                                    }
                                }
                                lastGenerationAction = executeAction
                                executeAction()
                            }
                        )
                    }

                    AppScreen.GENERATED_MCQ_VIEW -> {
                        com.example.ui.screens.mcq.GeneratedMcqViewScreen(
                            questions = generatedQuestions,
                            isLoading = isGenerating,
                            progressCount = generationProgressCount,
                            totalCount = generationTotalCount,
                            errorMessage = generationError,
                            onRetry = { lastGenerationAction?.invoke() },
                            onBack = { currentScreen = AppScreen.MAIN_SHELL },
                            onAskAi = { questionContext ->
                                geminiService.askAiExplanation(questionContext)
                            }
                        )
                    }

                    AppScreen.QUIZ_INTERFACE -> {
                        com.example.ui.screens.quiz.QuizInterfaceScreen(
                            questions = generatedQuestions.ifEmpty { null },
                            onBack = { currentScreen = AppScreen.MAIN_SHELL },
                            onQuizComplete = { currentScreen = AppScreen.MAIN_SHELL }
                        )
                    }

                    AppScreen.WRITTEN_ANSWER_PRACTICE -> {
                        com.example.ui.screens.practice.WrittenAnswerPracticeScreen(
                            onBack = { currentScreen = AppScreen.MAIN_SHELL }
                        )
                    }

                    AppScreen.AI_NOTES_GENERATOR -> {
                        com.example.ui.screens.notes.AINotesGeneratorScreen(
                            onBack = { currentScreen = AppScreen.MAIN_SHELL }
                        )
                    }

                    AppScreen.QUICK_REVISION -> {
                        com.example.ui.screens.revision.QuickRevisionScreen(
                            onBack = { currentScreen = AppScreen.MAIN_SHELL },
                            onStartQuickQuiz = { currentScreen = AppScreen.QUIZ_INTERFACE }
                        )
                    }

                    AppScreen.DAILY_CHALLENGE -> {
                        com.example.ui.screens.challenge.DailyChallengeScreen(
                            onBack = { currentScreen = AppScreen.MAIN_SHELL },
                            onStartChallenge = { currentScreen = AppScreen.QUIZ_INTERFACE }
                        )
                    }

                    AppScreen.QUESTION_BANK -> {
                        com.example.ui.screens.bank.QuestionBankScreen(
                            onBack = { currentScreen = AppScreen.MAIN_SHELL },
                            onViewItem = { currentScreen = AppScreen.QUIZ_INTERFACE },
                            currentTab = currentTab,
                            onTabSelected = {
                                currentTab = it
                                currentScreen = AppScreen.MAIN_SHELL
                            }
                        )
                    }

                    AppScreen.ACHIEVEMENTS -> {
                        com.example.ui.screens.gamification.AchievementsScreen(
                            userName = userName,
                            onBack = { currentScreen = AppScreen.MAIN_SHELL }
                        )
                    }

                    AppScreen.ADMIN_PANEL -> {
                        com.example.ui.screens.admin.AdminPanelScreen(
                            onBackToApp = { currentScreen = AppScreen.MAIN_SHELL }
                        )
                    }
                    AppScreen.DOUBT_SECTION -> {
                        com.example.ui.screens.doubt.DoubtSectionScreen(
                            onBack = { currentScreen = AppScreen.MAIN_SHELL }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainShellContent(
    currentTab: NavigationTab,
    userName: String,
    userExam: String,
    userGrade: String,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onNavigateScreen: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    when (currentTab) {
        NavigationTab.HOME -> {
            HomeScreen(
                userName = userName,
                onNavigateScreen = onNavigateScreen,
                modifier = modifier
            )
        }

        NavigationTab.PRACTICE -> {
            com.example.ui.screens.bank.QuestionBankScreen(
                onBack = { onNavigateScreen(AppScreen.MAIN_SHELL) },
                onViewItem = { onNavigateScreen(AppScreen.QUIZ_INTERFACE) },
                currentTab = currentTab,
                onTabSelected = { tab ->
                    if (tab != NavigationTab.PRACTICE) {
                        onNavigateScreen(AppScreen.MAIN_SHELL)
                    }
                },
                modifier = modifier
            )
        }

        NavigationTab.AI_TEACHER -> {
            com.example.ui.screens.notes.AINotesGeneratorScreen(
                onBack = { onNavigateScreen(AppScreen.MAIN_SHELL) },
                modifier = modifier
            )
        }

        NavigationTab.PROGRESS -> {
            com.example.ui.screens.gamification.AchievementsScreen(
                userName = userName,
                onBack = { onNavigateScreen(AppScreen.MAIN_SHELL) },
                modifier = modifier
            )
        }

        else -> {
            TabPlaceholderScreen(
                tab = currentTab,
                userName = userName,
                userExam = userExam,
                userGrade = userGrade,
                isDarkTheme = isDarkTheme,
                onToggleDarkTheme = onToggleDarkTheme,
                onNavigateScreen = onNavigateScreen,
                modifier = modifier
            )
        }
    }
}
