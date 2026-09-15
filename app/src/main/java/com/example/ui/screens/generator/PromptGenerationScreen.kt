package com.example.ui.screens.generator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CustomDropdownField
import com.example.ui.components.FloatingCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.VioletAccent

import androidx.compose.material.icons.filled.Menu

@Composable
fun PromptGenerationScreen(
    onBack: () -> Unit,
    onGenerate: (prompt: String, questionType: String, language: String, includeExplanations: Boolean, includeNumericals: Boolean, examPattern: String?, count: Int) -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var promptText by remember {
        mutableStateOf("Generate 50 Class 11 Chemistry MCQs from Classification of Elements and Periodicity in Properties at NEET level.")
    }
    val maxChars = 1000

    // Options
    var selectedQuestionType by remember { mutableStateOf("Mixed") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var includeExplanations by remember { mutableStateOf(true) }
    var includeNumericals by remember { mutableStateOf(true) }
    var enableExamPattern by remember { mutableStateOf(true) }
    var selectedExamPattern by remember { mutableStateOf("NEET") }

    val questionTypeOptions = listOf("Mixed", "Conceptual", "Numerical", "Assertion-Reason", "Statement-Based")
    val languageOptions = listOf("English", "Hindi", "Hinglish", "Spanish", "French")
    val examPatternOptions = listOf("NEET", "JEE Main", "CBSE Board", "CUET", "State CET")

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
                        modifier = Modifier.testTag("prompt_back_button")
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
                            text = "Generate from Text Prompt",
                            color = TextOnWhitePrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Tell AI what you want to generate...",
                        color = TextOnWhitePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Large Textarea Card with placeholder and character counter
                    FloatingCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp,
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            ) {
                                if (promptText.isEmpty()) {
                                    Text(
                                        text = "e.g. Generate 20 MCQs for Thermodynamics with formula derivations and detailed step-by-step solutions...",
                                        color = TextMuted,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                                BasicTextField(
                                    value = promptText,
                                    onValueChange = {
                                        if (it.length <= maxChars) {
                                            promptText = it
                                        }
                                    },
                                    textStyle = LocalTextStyle.current.copy(
                                        color = TextOnWhitePrimary,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    ),
                                    cursorBrush = SolidColor(VioletAccent),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .testTag("prompt_textarea_input")
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "${promptText.length}/$maxChars",
                                    color = SlateGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // "Additional Options" section
                    Text(
                        text = "Additional Options",
                        color = TextOnWhitePrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FloatingCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Question Type & Language Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CustomDropdownField(
                                    label = "Question Type",
                                    selectedValue = selectedQuestionType,
                                    options = questionTypeOptions,
                                    onSelectOption = { selectedQuestionType = it },
                                    modifier = Modifier.weight(1f)
                                )

                                CustomDropdownField(
                                    label = "Language",
                                    selectedValue = selectedLanguage,
                                    options = languageOptions,
                                    onSelectOption = { selectedLanguage = it },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Toggle: Include Explanations
                            OptionToggleRow(
                                title = "Include Explanations",
                                checked = includeExplanations,
                                onCheckedChange = { includeExplanations = it },
                                testTag = "toggle_include_explanations"
                            )

                            // Toggle: Include Numerical Questions
                            OptionToggleRow(
                                title = "Include Numerical Questions",
                                checked = includeNumericals,
                                onCheckedChange = { includeNumericals = it },
                                testTag = "toggle_include_numericals"
                            )

                            // Toggle: Exam Pattern
                            OptionToggleRow(
                                title = "Exam Pattern",
                                checked = enableExamPattern,
                                onCheckedChange = { enableExamPattern = it },
                                testTag = "toggle_exam_pattern"
                            )

                            // Exam Pattern dropdown if enabled
                            if (enableExamPattern) {
                                CustomDropdownField(
                                    label = "Target Exam Pattern",
                                    selectedValue = selectedExamPattern,
                                    options = examPatternOptions,
                                    onSelectOption = { selectedExamPattern = it },
                                    modifier = Modifier.testTag("dropdown_exam_pattern")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Sticky Gradient Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardWhite,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GradientButton(
                        text = "Generate MCQs",
                        onClick = {
                            val parsedCount = Regex("\\b(\\d+)\\b")
                                .findAll(promptText)
                                .mapNotNull { it.value.toIntOrNull() }
                                .firstOrNull { it in 1..500 } ?: 10

                            onGenerate(
                                promptText,
                                selectedQuestionType,
                                selectedLanguage,
                                includeExplanations,
                                includeNumericals,
                                if (enableExamPattern) selectedExamPattern else null,
                                parsedCount
                            )
                        },
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                            .testTag("prompt_generate_mcqs_button")
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextOnWhitePrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VioletAccent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
