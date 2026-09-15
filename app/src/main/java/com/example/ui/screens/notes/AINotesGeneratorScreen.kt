package com.example.ui.screens.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyllabusData
import com.example.ui.components.FloatingCard
import com.example.ui.components.CustomDropdownField
import com.example.ui.components.GradientButton
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.HeroCardGradient
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VioletAccent
import androidx.compose.material.icons.filled.Menu
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class NoteTypeItem(
    val id: String,
    val label: String,
    val caption: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AINotesGeneratorScreen(
    onBack: (() -> Unit)? = null,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Classes, Subjects, Chapters
    val classes = SyllabusData.syllabus.keys.toList()
    var selectedClass by remember { mutableStateOf("NEET") }
    val subjects = SyllabusData.syllabus[selectedClass]?.keys?.toList() ?: listOf("Physics", "Chemistry", "Biology")
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull() ?: "Physics") }
    val chapters = SyllabusData.syllabus[selectedClass]?.get(selectedSubject) ?: listOf("Laws of Motion", "Work Energy Power", "Gravitation")
    var selectedChapter by remember { mutableStateOf(chapters.firstOrNull() ?: "Laws of Motion") }

    // Dropdown states
    var isSubjectExpanded by remember { mutableStateOf(false) }
    var isChapterExpanded by remember { mutableStateOf(false) }
    var specificTopic by remember { mutableStateOf("") }

    // 7 Note Types in 2-column grid
    val noteTypes = remember {
        listOf(
            NoteTypeItem(
                id = "short_notes",
                label = "Short Notes",
                caption = "Quick bullet points & key formulas",
                icon = Icons.Default.Edit,
                iconBg = Color(0xFFEFF6FF),
                iconTint = Color(0xFF2563EB)
            ),
            NoteTypeItem(
                id = "detailed_notes",
                label = "Detailed Notes",
                caption = "In-depth explanations with examples",
                icon = Icons.Default.MenuBook,
                iconBg = Color(0xFFF5F3FF),
                iconTint = VioletAccent
            ),
            NoteTypeItem(
                id = "revision_notes",
                label = "Revision Notes",
                caption = "Last-minute exam recall sheets",
                icon = Icons.Default.Refresh,
                iconBg = Color(0xFFECFDF5),
                iconTint = SuccessGreen
            ),
            NoteTypeItem(
                id = "important_definitions",
                label = "Important Definitions",
                caption = "Precise scientific terms & laws",
                icon = Icons.Default.FormatQuote,
                iconBg = Color(0xFFFFFBEB),
                iconTint = Color(0xFFD97706)
            ),
            NoteTypeItem(
                id = "important_formulas",
                label = "Important Formulas",
                caption = "All derivations, symbols & SI units",
                icon = Icons.Default.Calculate,
                iconBg = Color(0xFFFDF2F8),
                iconTint = Color(0xFFDB2777)
            ),
            NoteTypeItem(
                id = "key_concepts",
                label = "Key Concepts",
                caption = "Fundamental mental models & rules",
                icon = Icons.Default.Lightbulb,
                iconBg = Color(0xFFFEF3C7),
                iconTint = Color(0xFFB45309)
            ),
            NoteTypeItem(
                id = "exam_focused_notes",
                label = "Exam-Focused Notes",
                caption = "High-yield topics & recurring questions",
                icon = Icons.Default.Star,
                iconBg = Color(0xFFEEF2FF),
                iconTint = Color(0xFF4F46E5)
            )
        )
    }

    var selectedNoteTypeId by remember { mutableStateOf("short_notes") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<String?>(null) }
    var resultTab by remember { mutableStateOf(0) }

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
                border = BorderStroke(1.dp, CardBorderLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("notes_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1E293B)
                            )
                        }
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
                            text = "AI Notes Generator",
                            color = TextOnWhitePrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Smart structured notes & high-yield summaries",
                            color = SlateGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Main Content: 2-Column Grid of Note Types + Controls
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Topic & Chapter selectors (spans full width)
                item(span = { GridItemSpan(2) }) {
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
                                text = "Select Class & Subject",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextOnWhitePrimary
                            )

                            CustomDropdownField(
                                label = "Class / Exam Target",
                                selectedValue = selectedClass,
                                options = classes,
                                onSelectOption = { newClass ->
                                    selectedClass = newClass
                                    val newSubjects = SyllabusData.syllabus[newClass]?.keys?.toList() ?: emptyList()
                                    selectedSubject = newSubjects.firstOrNull() ?: ""
                                    val newChapters = SyllabusData.syllabus[newClass]?.get(selectedSubject) ?: emptyList()
                                    selectedChapter = newChapters.firstOrNull() ?: ""
                                },
                                modifier = Modifier.testTag("notes_class_dropdown")
                            )

                            CustomDropdownField(
                                label = "Subject",
                                selectedValue = selectedSubject.ifEmpty { "Select Subject" },
                                options = subjects,
                                onSelectOption = { subj ->
                                    selectedSubject = subj
                                    val newChapters = SyllabusData.syllabus[selectedClass]?.get(subj) ?: emptyList()
                                    selectedChapter = newChapters.firstOrNull() ?: ""
                                },
                                modifier = Modifier.testTag("notes_subject_dropdown")
                            )

                            CustomDropdownField(
                                label = "Chapter / Unit",
                                selectedValue = selectedChapter.ifEmpty { "Select Chapter" },
                                options = chapters,
                                onSelectOption = { chap ->
                                    selectedChapter = chap
                                },
                                modifier = Modifier.testTag("notes_chapter_dropdown")
                            )

                            // Specific Topic / Keywords Input
                            OutlinedTextField(
                                value = specificTopic,
                                onValueChange = { specificTopic = it },
                                label = { Text("Specific Topic / Keywords (Optional)", fontSize = 12.sp) },
                                placeholder = { Text("e.g. Electromagnetic induction laws", fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VioletAccent,
                                    unfocusedBorderColor = CardBorderLight,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    focusedLabelColor = TextOnWhiteSecondary,
                                    unfocusedLabelColor = TextOnWhiteSecondary,
                                    focusedPlaceholderColor = TextOnWhiteSecondary,
                                    unfocusedPlaceholderColor = TextOnWhiteSecondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("notes_specific_topic_input"),
                                singleLine = true
                            )
                        }
                    }
                }

                // Grid Section Title
                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Choose Note Type",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnWhitePrimary
                        )
                        Text(
                            text = "7 formats available",
                            fontSize = 12.sp,
                            color = SlateGray
                        )
                    }
                }

                // 7 Note-Type Cards
                items(noteTypes) { item ->
                    val isSelected = item.id == selectedNoteTypeId

                    FloatingCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedNoteTypeId = item.id }
                            .testTag("note_type_${item.id}"),
                        cornerRadius = 16.dp,
                        elevation = if (isSelected) 4.dp else 1.dp,
                        backgroundColor = if (isSelected) Color(0xFFFBFBFE) else Color.White,
                        borderColor = if (isSelected) VioletAccent else CardBorderLight
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(item.iconBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = item.iconTint,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(VioletAccent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = item.label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnWhitePrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.caption,
                                fontSize = 11.sp,
                                color = SlateGray,
                                lineHeight = 15.sp,
                                minLines = 2
                            )
                        }
                    }
                }

                // If notes are generated, display the interactive notes sheet
                if (generatedResult != null) {
                    item(span = { GridItemSpan(2) }) {
                        GeneratedNotesCard(
                            notesText = generatedResult ?: "",
                            selectedNoteType = noteTypes.find { it.id == selectedNoteTypeId }?.label ?: "Notes",
                            subject = selectedSubject,
                            chapter = selectedChapter,
                            activeTab = resultTab,
                            onTabChange = { resultTab = it },
                            onCopy = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Notes copied to clipboard!")
                                }
                            }
                        )
                    }
                }
            }
        }

        // Sticky Bottom "Generate Notes" Gradient Button
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(elevation = 16.dp, ambientColor = Color(0x33000000)),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorderLight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                GradientButton(
                    text = if (isGenerating) "Synthesizing Notes..." else "Generate Notes",
                    onClick = {
                        isGenerating = true
                        coroutineScope.launch {
                            delay(1200)
                            val noteTypeObj = noteTypes.find { it.id == selectedNoteTypeId }
                            val label = noteTypeObj?.label ?: "Study Notes"
                            generatedResult = buildSampleNotes(selectedSubject, selectedChapter, label)
                            isGenerating = false
                            snackbarHostState.showSnackbar("AI Notes generated successfully!")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("generate_notes_sticky_button"),
                    enabled = !isGenerating,
                    leadingIcon = if (isGenerating) null else {
                        {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )
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
private fun GeneratedNotesCard(
    notesText: String,
    selectedNoteType: String,
    subject: String,
    chapter: String,
    activeTab: Int,
    onTabChange: (Int) -> Unit,
    onCopy: () -> Unit
) {
    FloatingCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        cornerRadius = 20.dp,
        elevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
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
                            .background(Color(0xFFEEF2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = VioletAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "$selectedNoteType: $chapter",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextOnWhitePrimary
                        )
                        Text(
                            text = "$subject · NCERT Aligned",
                            fontSize = 12.sp,
                            color = SlateGray
                        )
                    }
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.testTag("copy_notes_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = VioletAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color(0xFFF1F5F9),
                contentColor = VioletAccent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = VioletAccent
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                listOf("Full Content", "Formula Sheet", "Key Points").forEachIndexed { index, title ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { onTabChange(index) },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeTab == index) VioletAccent else SlateGray
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, CardBorderLight, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = when (activeTab) {
                        1 -> "📐 Key Formulas & Units:\n• Momentum: p = m · v [kg·m/s]\n• Newton's 2nd Law: F = dp/dt = m · a\n• Friction Force: f_max = μ_s · N\n• Impulse: J = ∫ F dt = Δp"
                        2 -> "💡 High-Yield Key Points:\n• Force is not required to maintain uniform motion (1st Law inertia).\n• Action and reaction forces act on DIFFERENT bodies simultaneously.\n• Apparent weight in an elevator accelerating upward: N = m(g + a)."
                        else -> notesText
                    },
                    fontSize = 13.sp,
                    color = TextOnWhitePrimary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

private fun buildSampleNotes(subject: String, chapter: String, noteType: String): String {
    return """
📚 $noteType for $chapter ($subject)

1. Core Definition & First Principles:
   The chapter establishes how external influences alter the state of bodies.
   - Inertia: Resistance to change in state of rest or uniform motion.
   - Momentum: Quantity of motion possessed by a body (p = m · v).

2. Fundamental Laws & Theorems:
   - First Law: Law of Inertia (Defines qualitative force).
   - Second Law: F = dp/dt = m·a (Quantifies force in Newtons).
   - Third Law: For every action, there is an equal and opposite reaction on distinct bodies.

3. High-Yield Exam Pitfalls:
   - Action-Reaction pairs NEVER cancel each other out because they act on different objects.
   - Normal contact force is NOT necessarily equal to mg (e.g. inclined plane N = mg cos θ).
    """.trimIndent()
}
