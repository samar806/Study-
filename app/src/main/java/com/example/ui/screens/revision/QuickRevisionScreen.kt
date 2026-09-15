package com.example.ui.screens.revision

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChecklistItem(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    var isChecked: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickRevisionScreen(
    onBack: () -> Unit,
    onStartQuickQuiz: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Curriculum subject & chapter selectors
    val classes = SyllabusData.syllabus.keys.toList()
    val defaultClass = "Class 12"
    val subjects = SyllabusData.syllabus[defaultClass]?.keys?.toList() ?: listOf("Physics", "Chemistry", "Biology", "Mathematics")
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull() ?: "Physics") }
    val chapters = SyllabusData.syllabus[defaultClass]?.get(selectedSubject) ?: listOf("Electric Charges and Fields", "Electrostatic Potential", "Current Electricity")
    var selectedChapter by remember { mutableStateOf(chapters.firstOrNull() ?: "Electric Charges and Fields") }

    var isSubjectExpanded by remember { mutableStateOf(false) }
    var isChapterExpanded by remember { mutableStateOf(false) }

    // 5 "Includes" checklist items, pre-checked as specified
    val checklistItems = remember {
        mutableStateListOf(
            ChecklistItem("concepts", "Key Concepts", "Core fundamental theorems & diagrams", "💡", isChecked = true),
            ChecklistItem("formulas", "Important Formulas", "Standard equations, units & relations", "📐", isChecked = true),
            ChecklistItem("definitions", "Definitions", "Exact NCERT definitions & terminology", "📖", isChecked = true),
            ChecklistItem("mistakes", "Common Mistakes", "Pitfalls & tricky edge cases in exams", "⚠️", isChecked = true),
            ChecklistItem("mcqs", "5–10 Quick MCQs", "Diagnostic recall check with instant solution", "⚡", isChecked = true)
        )
    }

    var isGenerating by remember { mutableStateOf(false) }
    var revisionGenerated by remember { mutableStateOf(false) }

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
                        modifier = Modifier.testTag("revision_back_button")
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
                            text = "Quick Revision",
                            color = TextOnWhitePrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "10-minute fast recall sprint",
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
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = WarningOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "High Yield",
                                color = Color(0xFFB45309),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Scrollable Configuration Form
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .padding(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Topic Selector Card
                FloatingCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    elevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEF2FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎯", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Revision Scope",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextOnWhitePrimary
                            )
                        }

                        // Subject Dropdown
                        ExposedDropdownMenuBox(
                            expanded = isSubjectExpanded,
                            onExpandedChange = { isSubjectExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedSubject,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Subject", fontSize = 12.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSubjectExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VioletAccent,
                                    unfocusedBorderColor = CardBorderLight,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .testTag("revision_subject_dropdown")
                            )
                            ExposedDropdownMenu(
                                expanded = isSubjectExpanded,
                                onDismissRequest = { isSubjectExpanded = false },
                                containerColor = Color.White
                            ) {
                                subjects.forEach { subj ->
                                    DropdownMenuItem(
                                        text = { Text(subj, fontSize = 13.sp, color = Color(0xFF1E293B)) },
                                        onClick = {
                                            selectedSubject = subj
                                            isSubjectExpanded = false
                                            val newChaps = SyllabusData.syllabus[defaultClass]?.get(subj) ?: emptyList()
                                            selectedChapter = newChaps.firstOrNull() ?: ""
                                        }
                                    )
                                }
                            }
                        }

                        // Chapter Dropdown
                        ExposedDropdownMenuBox(
                            expanded = isChapterExpanded,
                            onExpandedChange = { isChapterExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedChapter,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Chapter", fontSize = 12.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isChapterExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VioletAccent,
                                    unfocusedBorderColor = CardBorderLight,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .testTag("revision_chapter_dropdown")
                            )
                            ExposedDropdownMenu(
                                expanded = isChapterExpanded,
                                onDismissRequest = { isChapterExpanded = false },
                                containerColor = Color.White
                            ) {
                                chapters.forEach { chap ->
                                    DropdownMenuItem(
                                        text = { Text(chap, fontSize = 13.sp, color = Color(0xFF1E293B)) },
                                        onClick = {
                                            selectedChapter = chap
                                            isChapterExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // "Includes" Checklist Card
                FloatingCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    elevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Includes Checklist",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnWhitePrimary
                                )
                                Text(
                                    text = "Pre-checked for maximum exam recall",
                                    fontSize = 12.sp,
                                    color = SlateGray
                                )
                            }

                            val checkedCount = checklistItems.count { it.isChecked }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFECFDF5))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$checkedCount/5 selected",
                                    color = SuccessGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Checklist Items
                        checklistItems.forEachIndexed { index, item ->
                            val isChecked = item.isChecked
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        checklistItems[index] = item.copy(isChecked = !isChecked)
                                    }
                                    .border(
                                        1.dp,
                                        if (isChecked) VioletAccent.copy(alpha = 0.5f) else CardBorderLight,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .testTag("checklist_item_${item.id}"),
                                color = if (isChecked) Color(0xFFFBFBFE) else Color.White
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.iconEmoji, fontSize = 20.sp)

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = TextOnWhitePrimary
                                        )
                                        Text(
                                            text = item.description,
                                            fontSize = 11.sp,
                                            color = SlateGray
                                        )
                                    }

                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { checked ->
                                            checklistItems[index] = item.copy(isChecked = checked)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = VioletAccent,
                                            uncheckedColor = CardBorderLight
                                        ),
                                        modifier = Modifier.testTag("checkbox_${item.id}")
                                    )
                                }
                            }
                        }
                    }
                }

                // Estimated time badge
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Estimated Time: ~8 Mins · High-Yield Active Recall",
                            color = Color(0xFF1E40AF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Generated Revision Card
                if (revisionGenerated) {
                    GeneratedRevisionResultsCard(
                        subject = selectedSubject,
                        chapter = selectedChapter,
                        onStartQuiz = { onStartQuickQuiz?.invoke() }
                    )
                }
            }
        }

        // Sticky Bottom "Generate Revision" Button
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
                    text = if (isGenerating) "Compiling Revision Packet..." else "Generate Revision",
                    onClick = {
                        isGenerating = true
                        coroutineScope.launch {
                            delay(1200)
                            isGenerating = false
                            revisionGenerated = true
                            snackbarHostState.showSnackbar("Revision packet ready!")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("generate_revision_sticky_button"),
                    enabled = !isGenerating,
                    leadingIcon = if (isGenerating) null else {
                        {
                            Icon(
                                imageVector = Icons.Default.Bolt,
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
private fun GeneratedRevisionResultsCard(
    subject: String,
    chapter: String,
    onStartQuiz: () -> Unit
) {
    FloatingCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        elevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Quick Revision: $chapter",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextOnWhitePrimary
                    )
                    Text(
                        text = "Complete packet compiled with 5 practice MCQs",
                        fontSize = 12.sp,
                        color = SlateGray
                    )
                }
            }

            // Summary Section Cards
            RevisionSectionRow("Key Concepts", "Electric flux Φ = ∮ E · dA. Gaussian surfaces must possess symmetry.", "💡")
            RevisionSectionRow("Formulas", "Coulomb's Law: F = (1/4πε₀) · (q₁q₂/r²)\nElectric Field: E = F/q = (1/4πε₀) · (q/r²)", "📐")
            RevisionSectionRow("Common Mistakes", "Ignoring vector nature of electric fields. Superposition requires vector addition!", "⚠️")

            // Practice MCQs launch button
            Surface(
                onClick = onStartQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("start_revision_quiz_button"),
                color = Color(0xFFEEF2FF),
                border = BorderStroke(1.dp, VioletAccent.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "5-Question Quick Diagnostic",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = VioletAccent
                            )
                            Text(
                                text = "Instant evaluation & step-by-step solutions",
                                fontSize = 11.sp,
                                color = SlateGray
                            )
                        }
                    }

                    Text(
                        text = "Start →",
                        fontWeight = FontWeight.Bold,
                        color = VioletAccent,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RevisionSectionRow(
    title: String,
    content: String,
    emoji: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, CardBorderLight, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextOnWhitePrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                color = SlateGray,
                lineHeight = 17.sp
            )
        }
    }
}
