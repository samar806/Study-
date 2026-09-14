package com.example.ui.screens.generator

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.CustomDropdownField
import com.example.ui.components.FloatingCard
import com.example.ui.components.GradientButton
import com.example.ui.components.NumberGridSelector
import com.example.ui.components.RadioSelectionRow
import com.example.ui.components.SegmentedPillRow
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VioletAccent

@Composable
fun McqGeneratorSetupScreen(
    onBack: () -> Unit,
    onGenerate: (className: String, subject: String, chapter: String, scope: String, difficulty: String, count: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Form state
    var selectedClass by remember { mutableStateOf("Class 11") }
    var selectedSubject by remember { mutableStateOf("Physics") }
    var selectedChapter by remember { mutableStateOf("Motion in a Straight Line") }
    var chapterScopeIndex by remember { mutableIntStateOf(0) } // 0: Entire Chapter, 1: Specific Topic
    var difficultyIndex by remember { mutableIntStateOf(1) } // 0: Easy, 1: Medium, 2: Hard, 3: Very Hard
    var selectedMcqCount by remember { mutableStateOf("10") }
    var showOverflowMenu by remember { mutableStateOf(false) }

    val classOptions = listOf("Class 9", "Class 10", "Class 11", "Class 12", "Dropper / Target")
    val subjectOptions = listOf("Physics", "Chemistry", "Biology", "Mathematics")
    val chapterOptions = listOf(
        "Motion in a Straight Line",
        "Laws of Motion",
        "Work, Energy and Power",
        "Gravitation",
        "Thermodynamics",
        "Oscillations & Waves"
    )
    val difficultyOptions = listOf("Easy", "Medium", "Hard", "Very Hard")

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
                        modifier = Modifier.testTag("generator_back_button")
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
                            text = "AI MCQ Generator",
                            color = TextOnWhitePrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Create MCQs with AI in seconds",
                            color = SlateGray,
                            fontSize = 12.sp
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showOverflowMenu = true },
                            modifier = Modifier.testTag("generator_overflow_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = SlateGray
                            )
                        }

                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false },
                            modifier = Modifier.background(CardWhite)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Reset to Defaults", fontSize = 14.sp) },
                                onClick = {
                                    selectedClass = "Class 11"
                                    selectedSubject = "Physics"
                                    selectedChapter = "Motion in a Straight Line"
                                    difficultyIndex = 1
                                    selectedMcqCount = "10"
                                    showOverflowMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Exam Syllabus Info", fontSize = 14.sp) },
                                onClick = { showOverflowMenu = false }
                            )
                        }
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
                FloatingCard(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth(),
                    cornerRadius = 24.dp,
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        // 1. Stacked Dropdowns: Class, Subject, Chapter/Topic
                        CustomDropdownField(
                            label = "Class",
                            selectedValue = selectedClass,
                            options = classOptions,
                            onSelectOption = { selectedClass = it },
                            modifier = Modifier.testTag("dropdown_class")
                        )

                        CustomDropdownField(
                            label = "Subject",
                            selectedValue = selectedSubject,
                            options = subjectOptions,
                            onSelectOption = { selectedSubject = it },
                            modifier = Modifier.testTag("dropdown_subject")
                        )

                        CustomDropdownField(
                            label = "Chapter / Topic",
                            selectedValue = selectedChapter,
                            options = chapterOptions,
                            onSelectOption = { selectedChapter = it },
                            modifier = Modifier.testTag("dropdown_chapter")
                        )

                        // 2. Radio Row: "Entire Chapter" vs "Specific Topic"
                        RadioSelectionRow(
                            options = listOf("Entire Chapter", "Specific Topic"),
                            selectedIndex = chapterScopeIndex,
                            onSelect = { chapterScopeIndex = it },
                            modifier = Modifier.testTag("radio_chapter_scope")
                        )

                        // 3. Difficulty Level: 4-segment pill row
                        Column {
                            Text(
                                text = "Difficulty Level",
                                color = TextOnWhitePrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            SegmentedPillRow(
                                options = difficultyOptions,
                                selectedIndex = difficultyIndex,
                                onOptionSelected = { difficultyIndex = it },
                                modifier = Modifier.testTag("segmented_difficulty")
                            )
                        }

                        // 4. Number of MCQs Button Grid
                        Column {
                            Text(
                                text = "Number of MCQs",
                                color = TextOnWhitePrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            NumberGridSelector(
                                selectedNumber = selectedMcqCount,
                                onSelectNumber = { selectedMcqCount = it },
                                modifier = Modifier.testTag("grid_number_mcqs")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Sticky Bottom Button
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
                            val diff = difficultyOptions.getOrElse(difficultyIndex) { "Medium" }
                            val scope = if (chapterScopeIndex == 0) "Entire Chapter" else "Specific Topic"
                            val count = selectedMcqCount.toIntOrNull() ?: 10
                            onGenerate(selectedClass, selectedSubject, selectedChapter, scope, diff, count)
                        },
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                            .testTag("sticky_generate_mcqs_button")
                    )
                }
            }
        }
    }
}
