package com.example.ui.screens.generator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyllabusData
import com.example.ui.components.CleanInputField
import com.example.ui.components.CustomDropdownField
import com.example.ui.components.FloatingCard
import com.example.ui.components.GradientButton
import com.example.ui.components.NumberGridSelector
import com.example.ui.components.RadioSelectionRow
import com.example.ui.components.SegmentedPillRow
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VioletAccent

import androidx.compose.material.icons.filled.Menu

@Composable
fun McqGeneratorSetupScreen(
    onBack: () -> Unit,
    onGenerate: (className: String, subject: String, chapter: String, scope: String, specificTopic: String?, difficulty: String, count: Int) -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val classOptions = remember { SyllabusData.getClasses() }

    // Dynamic state initialization
    var selectedClass by remember { mutableStateOf("Class 11") }
    var selectedSubject by remember {
        mutableStateOf(SyllabusData.getSubjects("Class 11").firstOrNull() ?: "Physics")
    }
    var selectedChapter by remember {
        mutableStateOf(
            SyllabusData.getChapters("Class 11", selectedSubject).firstOrNull() ?: "Physical World and Measurement"
        )
    }

    // Dynamic lists based on selections
    val subjectOptions = remember(selectedClass) {
        SyllabusData.getSubjects(selectedClass)
    }
    val chapterOptions = remember(selectedClass, selectedSubject) {
        SyllabusData.getChapters(selectedClass, selectedSubject)
    }

    var chapterScopeIndex by remember { mutableIntStateOf(0) } // 0: Entire Chapter, 1: Specific Topic
    var specificTopicText by remember { mutableStateOf("") }
    var difficultyIndex by remember { mutableIntStateOf(1) } // 0: Easy, 1: Medium, 2: Hard, 3: Very Hard
    var selectedMcqCount by remember { mutableStateOf("10") }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showSyllabusInfoDialog by remember { mutableStateOf(false) }

    val difficultyOptions = listOf("Easy", "Medium", "Hard", "Very Hard")

    // --- Dynamic Dropdown Event Handlers (per curriculum spec) ---
    val onClassChange: (String) -> Unit = { newClass ->
        selectedClass = newClass
        val newSubjects = SyllabusData.getSubjects(newClass)
        val defaultSubject = if (newSubjects.contains(selectedSubject)) {
            selectedSubject
        } else {
            newSubjects.firstOrNull().orEmpty()
        }
        selectedSubject = defaultSubject
        val newChapters = SyllabusData.getChapters(newClass, defaultSubject)
        selectedChapter = newChapters.firstOrNull().orEmpty()
        specificTopicText = ""
    }

    val onSubjectChange: (String) -> Unit = { newSubject ->
        selectedSubject = newSubject
        val newChapters = SyllabusData.getChapters(selectedClass, newSubject)
        selectedChapter = newChapters.firstOrNull().orEmpty()
        specificTopicText = ""
    }

    val onChapterChange: (String) -> Unit = { newChapter ->
        selectedChapter = newChapter
        specificTopicText = ""
    }

    val onScopeChange: (Int) -> Unit = { newScopeIndex ->
        chapterScopeIndex = newScopeIndex
        if (newScopeIndex == 0) {
            // Requirement #5: Clear specific-topic value when "Entire Chapter" is selected
            specificTopicText = ""
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
                        modifier = Modifier.testTag("generator_back_button")
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
                            text = "AI MCQ Generator",
                            color = TextOnWhitePrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Curriculum-accurate NCERT & Entrance prep",
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
                                text = { Text("Reset to Defaults", fontSize = 14.sp, color = Color(0xFF1E293B)) },
                                onClick = {
                                    selectedClass = "Class 11"
                                    selectedSubject = "Physics"
                                    selectedChapter = SyllabusData.getChapters("Class 11", "Physics").firstOrNull()
                                        ?: "Physical World and Measurement"
                                    chapterScopeIndex = 0
                                    specificTopicText = ""
                                    difficultyIndex = 1
                                    selectedMcqCount = "10"
                                    showOverflowMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Curriculum & Syllabus Info", fontSize = 14.sp, color = Color(0xFF1E293B)) },
                                onClick = {
                                    showOverflowMenu = false
                                    showSyllabusInfoDialog = true
                                }
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
                    .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingCard(
                    modifier = Modifier
                        .widthIn(max = 520.dp)
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
                        // 1. Stacked Dynamic Dropdowns: Class, Subject, Chapter
                        CustomDropdownField(
                            label = "Class / Exam Target",
                            selectedValue = selectedClass,
                            options = classOptions,
                            onSelectOption = onClassChange,
                            modifier = Modifier.testTag("dropdown_class")
                        )

                        CustomDropdownField(
                            label = "Subject",
                            selectedValue = selectedSubject.ifEmpty { "Select Subject" },
                            options = subjectOptions,
                            onSelectOption = onSubjectChange,
                            modifier = Modifier.testTag("dropdown_subject")
                        )

                        CustomDropdownField(
                            label = "Chapter / Unit",
                            selectedValue = selectedChapter.ifEmpty { "Select Chapter" },
                            options = chapterOptions,
                            onSelectOption = onChapterChange,
                            modifier = Modifier.testTag("dropdown_chapter")
                        )

                        // 2. Radio Row: "Entire Chapter" vs "Specific Topic"
                        Column {
                            Text(
                                text = "Coverage Scope",
                                color = TextOnWhitePrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            RadioSelectionRow(
                                options = listOf("Entire Chapter", "Specific Topic"),
                                selectedIndex = chapterScopeIndex,
                                onSelect = onScopeChange,
                                modifier = Modifier.testTag("radio_chapter_scope")
                            )
                        }

                        // Requirement #5: When "Specific Topic" is selected, render a text input directly below
                        AnimatedVisibility(
                            visible = chapterScopeIndex == 1,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Specific Topic / Sub-theme",
                                    color = TextOnWhitePrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                CleanInputField(
                                    value = specificTopicText,
                                    onValueChange = { specificTopicText = it },
                                    placeholder = "Enter a specific topic within this chapter",
                                    leadingIcon = Icons.Default.Topic,
                                    modifier = Modifier.testTag("input_specific_topic")
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "AI will narrow question generation specifically to this concept within $selectedChapter.",
                                    color = SlateGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

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
                            val specificTopic = if (chapterScopeIndex == 1 && specificTopicText.isNotBlank()) {
                                specificTopicText.trim()
                            } else {
                                null
                            }
                            onGenerate(
                                selectedClass,
                                selectedSubject,
                                selectedChapter,
                                scope,
                                specificTopic,
                                diff,
                                count
                            )
                        },
                        modifier = Modifier
                            .widthIn(max = 520.dp)
                            .fillMaxWidth()
                            .testTag("sticky_generate_mcqs_button")
                    )
                }
            }
        }
    }

    // Syllabus Information Dialog
    if (showSyllabusInfoDialog) {
        AlertDialog(
            onDismissRequest = { showSyllabusInfoDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Syllabus Info",
                    tint = VioletAccent
                )
            },
            title = {
                Text(
                    text = "NCERT & Exam Curriculum",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "• Aligned with the latest NCERT 2024-25 textbooks (Poorvi, Malhar, Ganita Prakash, Curiosity, Exploring Society, etc.).",
                        fontSize = 13.sp,
                        color = TextOnWhiteSecondary
                    )
                    Text(
                        text = "• Includes standard NCERT curricula for Classes 1 to 12 across Mathematics, Science, Social Science, EVS, English, Physics, Chemistry, Biology, and Computer Science.",
                        fontSize = 13.sp,
                        color = TextOnWhiteSecondary
                    )
                    Text(
                        text = "• Comprehensive NTA-notified NEET UG and JEE Main/Advanced combined syllabi with Dropper / Target track support.",
                        fontSize = 13.sp,
                        color = TextOnWhiteSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSyllabusInfoDialog = false }) {
                    Text("Got It", color = VioletAccent, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
