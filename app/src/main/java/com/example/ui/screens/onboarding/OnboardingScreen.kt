package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CleanInputField
import com.example.ui.components.FloatingCard
import com.example.ui.components.GradientButton
import com.example.ui.components.GradientProgressBar
import com.example.ui.components.SegmentedPillRow
import com.example.ui.components.StepCounterPill
import com.example.ui.components.SubjectToggleChip
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.NavyBackgroundGradient
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VioletAccent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onComplete: (userName: String, exam: String, grade: String, subjects: List<String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("Aarav") }
    var selectedTargetIndex by remember { mutableIntStateOf(0) }
    val examOptions = listOf("School", "NEET", "JEE", "Other")

    var selectedGrade by remember { mutableStateOf("Class 11") }
    var gradeMenuExpanded by remember { mutableStateOf(false) }
    val gradeOptions = listOf(
        "Class 9",
        "Class 10",
        "Class 11",
        "Class 12",
        "Dropper / Repeater",
        "College / Competitive"
    )

    val availableSubjects = listOf(
        "Physics",
        "Chemistry",
        "Biology",
        "Mathematics",
        "English",
        "Social Science"
    )
    val selectedSubjects = remember {
        mutableStateListOf("Physics", "Chemistry", "Biology")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackgroundGradient)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Floating Container Card
            FloatingCard(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .weight(1f)
                    .fillMaxWidth(),
                cornerRadius = 28.dp,
                backgroundColor = CardWhite,
                borderColor = CardBorderLight,
                elevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    // Top Bar: Small back icon top-left, Step counter pill top-right ("1/6")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                                .testTag("onboarding_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1E293B),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        StepCounterPill(
                            currentStep = 1,
                            totalSteps = 6,
                            modifier = Modifier.testTag("onboarding_step_pill")
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Scrollable form content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Sparkle Badge
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .shadow(
                                    elevation = 10.dp,
                                    shape = CircleShape,
                                    ambientColor = VioletAccent.copy(alpha = 0.4f),
                                    spotColor = VioletAccent.copy(alpha = 0.4f)
                                )
                                .clip(CircleShape)
                                .background(PrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Heading & Subtext
                        Text(
                            text = "Let's get started!",
                            color = TextOnWhitePrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tell us a little about yourself so we can personalize your learning experience.",
                            color = TextOnWhiteSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Question 1: "What is your name?"
                        Text(
                            text = "What is your name?",
                            color = TextOnWhitePrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CleanInputField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = "Enter your name",
                            leadingIcon = Icons.Default.Person,
                            testTag = "onboarding_name_input"
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Question 2: "What are you preparing for?"
                        Text(
                            text = "What are you preparing for?",
                            color = TextOnWhitePrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        SegmentedPillRow(
                            options = examOptions,
                            selectedIndex = selectedTargetIndex,
                            onOptionSelected = { selectedTargetIndex = it },
                            modifier = Modifier.testTag("onboarding_target_segmented")
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Question 3: "Class/Grade" dropdown
                        Text(
                            text = "Class/Grade",
                            color = TextOnWhitePrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                    .clickable { gradeMenuExpanded = true }
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                                    .testTag("grade_dropdown_trigger"),
                                color = Color(0xFFF8FAFC)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedGrade,
                                        color = TextOnWhitePrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Dropdown chevron",
                                        tint = SlateGray
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = gradeMenuExpanded,
                                onDismissRequest = { gradeMenuExpanded = false },
                                modifier = Modifier
                                    .background(Color.White)
                                    .border(1.dp, CardBorderLight, RoundedCornerShape(12.dp))
                            ) {
                                gradeOptions.forEach { grade ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = grade,
                                                color = if (grade == selectedGrade) VioletAccent else TextOnWhitePrimary,
                                                fontWeight = if (grade == selectedGrade) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            selectedGrade = grade
                                            gradeMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Question 4: "Preferred subjects (select multiple)"
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Preferred subjects ",
                                color = TextOnWhitePrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "(select multiple)",
                                color = SlateGray,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            availableSubjects.forEach { subject ->
                                val isSelected = selectedSubjects.contains(subject)
                                SubjectToggleChip(
                                    title = subject,
                                    isSelected = isSelected,
                                    onToggle = {
                                        if (isSelected) {
                                            selectedSubjects.remove(subject)
                                        } else {
                                            selectedSubjects.add(subject)
                                        }
                                    },
                                    modifier = Modifier.testTag("subject_chip_$subject")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Bottom progress indicator and sticky action
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        GradientProgressBar(
                            progress = 0.55f,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        GradientButton(
                            text = "Next",
                            onClick = {
                                onComplete(
                                    name.ifBlank { "Aarav" },
                                    examOptions[selectedTargetIndex],
                                    selectedGrade,
                                    selectedSubjects.toList()
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "onboarding_next_button"
                        )
                    }
                }
            }
        }
    }
}
