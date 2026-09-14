package com.example.ui.navigation

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppTheme
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TextOnNavyPrimary
import com.example.ui.theme.TextOnNavySecondary
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VioletAccent
import com.example.ui.theme.WarningOrange

enum class AppScreen {
    LOGIN,
    ONBOARDING,
    MAIN_SHELL,
    MCQ_GENERATOR_SETUP,
    PROMPT_GENERATION,
    IMAGE_GENERATION,
    GENERATED_MCQ_VIEW,
    QUIZ_INTERFACE
}

enum class NavigationTab(
    val title: String,
    val emoji: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", "🏠", Icons.Filled.Home, Icons.Outlined.Home),
    PRACTICE("Practice", "📚", Icons.Filled.Book, Icons.Outlined.Book),
    AI_TEACHER("AI Teacher", "✨", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    PROGRESS("Progress", "📊", Icons.Filled.Insights, Icons.Outlined.Insights),
    PROFILE("Profile", "👤", Icons.Filled.Person, Icons.Outlined.Person)
}

/**
 * Mobile Fixed Bottom Tab Bar with 5 items.
 * Active item is rendered with the gradient accent pill.
 */
@Composable
fun MobileBottomBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                ambientColor = Color(0x33000000),
                spotColor = Color(0x33000000)
            ),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationTab.entries.forEach { tab ->
                val isSelected = tab == currentTab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .clickable { onTabSelected(tab) }
                        .then(
                            if (isSelected) {
                                Modifier
                                    .background(PrimaryGradient)
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            } else {
                                Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                            }
                        )
                        .testTag("tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = tab.emoji,
                            fontSize = if (isSelected) 17.sp else 16.sp
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Desktop Left Sidebar for wide screens (adaptive).
 */
@Composable
fun DesktopSidebar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    onNavigateScreen: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight(),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Brand Logo Top
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AI MCQ Teacher",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Personal Study Assistant",
                        color = TextOnNavySecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Text(
                text = "MAIN MENU",
                color = SlateGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )

            NavigationTab.entries.forEach { tab ->
                val isSelected = tab == currentTab
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (isSelected) Modifier.background(PrimaryGradient)
                            else Modifier.clickable { onTabSelected(tab) }
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = tab.emoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = tab.title,
                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MORE TOOLS",
                color = SlateGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            )

            SidebarSubItem(emoji = "⚡", title = "Generate MCQs") {
                onTabSelected(NavigationTab.HOME)
            }
            SidebarSubItem(emoji = "📦", title = "Question Bank") {
                onTabSelected(NavigationTab.PRACTICE)
            }
            SidebarSubItem(emoji = "🏆", title = "Achievements") {
                onTabSelected(NavigationTab.PROGRESS)
            }
            SidebarSubItem(emoji = "⚙️", title = "Settings") {
                onTabSelected(NavigationTab.PROFILE)
            }

            Spacer(modifier = Modifier.weight(1f))

            // User Profile Bottom Row
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                color = Color(0xFF1E293B)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "A", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Aarav Sharma",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Class 11 · NEET",
                            color = TextOnNavySecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarSubItem(
    emoji: String,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
        )
    }
    Spacer(modifier = Modifier.height(2.dp))
}

/**
 * Repeating Header Component:
 * Back arrow top-left (optional), bold title + optional gray subtitle, optional icon top-right.
 */
@Composable
fun RepeatingHeader(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    rightAction: @Composable (() -> Unit)? = null,
    isDarkNavy: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isDarkNavy) Color(0x33FFFFFF) else Color(0xFFF1F5F9))
                        .testTag("header_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isDarkNavy) Color.White else Color(0xFF1E293B),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column {
                Text(
                    text = title,
                    color = if (isDarkNavy) TextOnNavyPrimary else TextOnWhitePrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                subtitle?.let {
                    Text(
                        text = it,
                        color = if (isDarkNavy) TextOnNavySecondary else TextOnWhiteSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        rightAction?.let {
            Spacer(modifier = Modifier.width(8.dp))
            it()
        }
    }
}
