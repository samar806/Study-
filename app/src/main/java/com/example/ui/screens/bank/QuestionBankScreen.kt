package com.example.ui.screens.bank

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingCard
import com.example.ui.navigation.MobileBottomBar
import com.example.ui.navigation.NavigationTab
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VioletAccent
import com.example.ui.theme.WarningOrange
import kotlinx.coroutines.launch

data class SavedItem(
    val id: String,
    val title: String,
    val subject: String,
    val chapter: String,
    val questionCount: Int,
    val difficulty: String,
    val dateSaved: String,
    val iconEmoji: String,
    val iconBg: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankScreen(
    onBack: () -> Unit,
    onViewItem: (SavedItem) -> Unit = {},
    currentTab: NavigationTab = NavigationTab.PRACTICE,
    onTabSelected: (NavigationTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Tabs: Saved Questions (0) vs Saved Tests (1)
    var selectedTab by remember { mutableIntStateOf(0) }
    var isOverflowMenuOpen by remember { mutableStateOf(false) }

    // Search query
    var searchQuery by remember { mutableStateOf("") }

    // Filter chip states
    var selectedSubjectFilter by remember { mutableStateOf("All Subjects") }
    var selectedDifficultyFilter by remember { mutableStateOf("All Difficulties") }

    val subjects = listOf("All Subjects", "Physics", "Chemistry", "Biology", "Mathematics")
    val difficulties = listOf("All Difficulties", "Easy", "Medium", "Hard")

    // Mock saved items dataset
    val savedQuestionsList = remember {
        listOf(
            SavedItem(
                id = "sq_1",
                title = "Newton's 2nd Law & Friction",
                subject = "Physics",
                chapter = "Laws of Motion",
                questionCount = 15,
                difficulty = "Medium",
                dateSaved = "2 days ago",
                iconEmoji = "🧲",
                iconBg = Color(0xFFEFF6FF)
            ),
            SavedItem(
                id = "sq_2",
                title = "Electrostatic Potential & Capacitance",
                subject = "Physics",
                chapter = "Electrostatics",
                questionCount = 20,
                difficulty = "Hard",
                dateSaved = "4 days ago",
                iconEmoji = "⚡",
                iconBg = Color(0xFFFEF3C7)
            ),
            SavedItem(
                id = "sq_3",
                title = "Organic Reaction Mechanisms & Electrophiles",
                subject = "Chemistry",
                chapter = "Organic Chemistry",
                questionCount = 12,
                difficulty = "Hard",
                dateSaved = "1 week ago",
                iconEmoji = "🧪",
                iconBg = Color(0xFFF3E8FF)
            ),
            SavedItem(
                id = "sq_4",
                title = "Cell Cycle & Mitosis Phases",
                subject = "Biology",
                chapter = "Cell Biology",
                questionCount = 18,
                difficulty = "Easy",
                dateSaved = "2 weeks ago",
                iconEmoji = "🧬",
                iconBg = Color(0xFFECFDF5)
            ),
            SavedItem(
                id = "sq_5",
                title = "Thermodynamics & Carnot Cycle",
                subject = "Physics",
                chapter = "Thermal Physics",
                questionCount = 25,
                difficulty = "Medium",
                dateSaved = "3 weeks ago",
                iconEmoji = "🔥",
                iconBg = Color(0xFFFFF1F2)
            )
        )
    }

    val savedTestsList = remember {
        listOf(
            SavedItem(
                id = "st_1",
                title = "NEET Full Length Mock #4",
                subject = "Physics & Chemistry",
                chapter = "All Units",
                questionCount = 45,
                difficulty = "Hard",
                dateSaved = "Yesterday",
                iconEmoji = "📝",
                iconBg = Color(0xFFEEF2FF)
            ),
            SavedItem(
                id = "st_2",
                title = "Class 11 Mechanics Diagnostic Sprint",
                subject = "Physics",
                chapter = "Mechanics",
                questionCount = 30,
                difficulty = "Medium",
                dateSaved = "3 days ago",
                iconEmoji = "⏱️",
                iconBg = Color(0xFFDCFCE7)
            ),
            SavedItem(
                id = "st_3",
                title = "Chemical Bonding Rapid Review Test",
                subject = "Chemistry",
                chapter = "Chemical Bonding",
                questionCount = 25,
                difficulty = "Medium",
                dateSaved = "5 days ago",
                iconEmoji = "🔬",
                iconBg = Color(0xFFFDF2F8)
            )
        )
    }

    val currentRawList = if (selectedTab == 0) savedQuestionsList else savedTestsList

    val filteredList = currentRawList.filter { item ->
        val matchesQuery = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.chapter.contains(searchQuery, ignoreCase = true) ||
                item.subject.contains(searchQuery, ignoreCase = true)
        val matchesSubject = selectedSubjectFilter == "All Subjects" || item.subject.contains(selectedSubjectFilter, ignoreCase = true)
        val matchesDiff = selectedDifficultyFilter == "All Difficulties" || item.difficulty.equals(selectedDifficultyFilter, ignoreCase = true)
        matchesQuery && matchesSubject && matchesDiff
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
            // Header + Overflow Icon
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
                        modifier = Modifier.testTag("bank_back_button")
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
                            text = "Question Bank",
                            color = TextOnWhitePrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Your saved collections & practice sets",
                            color = SlateGray,
                            fontSize = 12.sp
                        )
                    }

                    // Overflow Menu Icon
                    Box {
                        IconButton(
                            onClick = { isOverflowMenuOpen = true },
                            modifier = Modifier.testTag("bank_overflow_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = Color(0xFF475569)
                            )
                        }

                        DropdownMenu(
                            expanded = isOverflowMenuOpen,
                            onDismissRequest = { isOverflowMenuOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sort by Date (Newest)") },
                                onClick = {
                                    isOverflowMenuOpen = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Sorted by newest first.")
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort by Questions Count") },
                                onClick = {
                                    isOverflowMenuOpen = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Sorted by question count.")
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export Question Bank (PDF)") },
                                onClick = {
                                    isOverflowMenuOpen = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Exporting PDF question bank...")
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Tabs (Saved Questions / Saved Tests)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CardWhite,
                contentColor = VioletAccent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = VioletAccent
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.testTag("tab_saved_questions"),
                    text = {
                        Text(
                            text = "Saved Questions (${savedQuestionsList.size})",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 0) VioletAccent else SlateGray
                        )
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.testTag("tab_saved_tests"),
                    text = {
                        Text(
                            text = "Saved Tests (${savedTestsList.size})",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 1) VioletAccent else SlateGray
                        )
                    }
                )
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (selectedTab == 0) "Search saved questions or chapters..." else "Search saved tests...",
                            fontSize = 13.sp,
                            color = SlateGray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = SlateGray,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = SlateGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VioletAccent,
                        unfocusedBorderColor = CardBorderLight,
                        focusedContainerColor = CardWhite,
                        unfocusedContainerColor = CardWhite
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bank_search_bar")
                )
            }

            // Horizontal Filter Chips (All Subjects, All Chapters, Difficulty)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subjects.forEach { subj ->
                    val isSelected = selectedSubjectFilter == subj
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubjectFilter = subj },
                        label = { Text(subj, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VioletAccent,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_subject_$subj")
                    )
                }

                difficulties.forEach { diff ->
                    val isSelected = selectedDifficultyFilter == diff
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDifficultyFilter = diff },
                        label = { Text(diff, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = VioletAccent,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_diff_$diff")
                    )
                }
            }

            // List of Saved-Set Rows
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔍", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No saved sets match your filters",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextOnWhitePrimary
                                )
                                Text(
                                    text = "Try clearing search or changing filters",
                                    fontSize = 12.sp,
                                    color = SlateGray
                                )
                            }
                        }
                    }
                } else {
                    items(filteredList, key = { it.id }) { item ->
                        SavedSetRow(
                            item = item,
                            onView = {
                                onViewItem(item)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Opening '${item.title}'")
                                }
                            }
                        )
                    }
                }
            }
        }

        // Bottom Tab Bar
        MobileBottomBar(
            currentTab = currentTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

/**
 * List of saved-set rows:
 * icon, title, "N questions · Difficulty", "View" link
 */
@Composable
private fun SavedSetRow(
    item: SavedItem,
    onView: () -> Unit
) {
    FloatingCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onView)
            .testTag("saved_row_${item.id}"),
        cornerRadius = 16.dp,
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon tile
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.iconEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title & Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextOnWhitePrimary
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${item.questionCount} questions",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateGray
                    )
                    Text(
                        text = " · ",
                        fontSize = 12.sp,
                        color = SlateGray
                    )

                    // Difficulty Tag
                    val diffColor = when (item.difficulty.lowercase()) {
                        "easy" -> SuccessGreen
                        "hard" -> ErrorRed
                        else -> WarningOrange
                    }
                    Text(
                        text = item.difficulty,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = diffColor
                    )

                    Text(
                        text = " · ${item.subject}",
                        fontSize = 11.sp,
                        color = SlateGray
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // "View" link
            Surface(
                onClick = onView,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .testTag("view_link_${item.id}"),
                color = Color(0xFFEEF2FF)
            ) {
                Text(
                    text = "View",
                    color = VioletAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
