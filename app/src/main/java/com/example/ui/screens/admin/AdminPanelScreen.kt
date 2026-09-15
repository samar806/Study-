package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingCard
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextOnNavyPrimary
import com.example.ui.theme.TextOnNavySecondary
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VioletAccent
import com.example.ui.theme.WarningOrange

data class AdminNavItem(
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int? = null
)

data class RecentUserRecord(
    val id: String,
    val name: String,
    val email: String,
    val classExam: String,
    val questionsSolved: Int,
    val joinedDate: String,
    val isPremium: Boolean = false
)

@Composable
fun AdminPanelScreen(
    onBackToApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 11 Admin sidebar menu items
    val navItems = remember {
        listOf(
            AdminNavItem("Dashboard", Icons.Default.Dashboard),
            AdminNavItem("Users", Icons.Default.People),
            AdminNavItem("Classes", Icons.Default.School),
            AdminNavItem("Subjects", Icons.Default.Book),
            AdminNavItem("Chapters", Icons.Default.Category),
            AdminNavItem("Question Categories", Icons.Default.Category),
            AdminNavItem("Reported Questions", Icons.Default.Flag, badgeCount = 3),
            AdminNavItem("AI Usage", Icons.Default.AutoAwesome),
            AdminNavItem("Generation Limits", Icons.Default.Speed),
            AdminNavItem("Settings", Icons.Default.Settings),
            AdminNavItem("Analytics", Icons.Default.Analytics)
        )
    }

    var activeNavIndex by remember { mutableStateOf(0) }

    // Recent users mock dataset
    val recentUsers = remember {
        listOf(
            RecentUserRecord("u_1", "Aarav Sharma", "aarav.sharma@gmail.com", "NEET · Class 11", 142, "Today", isPremium = true),
            RecentUserRecord("u_2", "Priya Patel", "priya.p@outlook.com", "JEE · Class 12", 280, "Yesterday", isPremium = true),
            RecentUserRecord("u_3", "Rohan Verma", "rohan.v99@gmail.com", "Class 10 CBSE", 85, "2 days ago", isPremium = false),
            RecentUserRecord("u_4", "Ananya Iyer", "ananya.iyer@gmail.com", "NEET · Dropper", 310, "3 days ago", isPremium = true),
            RecentUserRecord("u_5", "Kabir Sen", "kabir.sen@gmail.com", "Class 9 Science", 42, "5 days ago", isPremium = false)
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        val isWide = maxWidth >= 700.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // FIXED NAVY SIDEBAR
            Surface(
                modifier = Modifier
                    .width(if (isWide) 250.dp else 200.dp)
                    .fillMaxHeight(),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(vertical = 16.dp, horizontal = 12.dp)
                ) {
                    // Logo + Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Admin Console",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "AI MCQ Teacher",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFF1E293B), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // 11 Nav items
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        navItems.forEachIndexed { index, item ->
                            val isSelected = activeNavIndex == index
                            Surface(
                                onClick = { activeNavIndex = index },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .testTag("admin_nav_${item.title.lowercase().replace(" ", "_")}"),
                                color = if (isSelected) Color(0xFF1E293B) else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = if (isSelected) VioletAccent else Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = item.title,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (item.badgeCount != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(ErrorRed)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${item.badgeCount}",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFF1E293B), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Logout at the bottom
                    Surface(
                        onClick = onBackToApp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .testTag("admin_logout_button"),
                        color = Color(0x1AEF4444)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = ErrorRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Back to App / Logout",
                                color = Color(0xFFFCA5A5),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // LIGHT MAIN PANEL
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                color = Color(0xFFF8FAFC)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Top Bar: "Admin Dashboard" title + Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Admin Dashboard",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnWhitePrimary
                            )
                            Text(
                                text = "Platform telemetry, curriculum health & user oversight",
                                fontSize = 13.sp,
                                color = SlateGray
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFDCFCE7))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(SuccessGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Gemini API Active",
                                        color = Color(0xFF15803D),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // TWO KPI CARDS with small "+X%" trend indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // KPI 1: Total Users
                        KpiTrendCard(
                            title = "Total Users",
                            value = "24,580",
                            trendText = "+12.4% this month",
                            isPositiveTrend = true,
                            icon = Icons.Default.People,
                            iconTint = Color(0xFF2563EB),
                            iconBg = Color(0xFFEFF6FF),
                            modifier = Modifier.weight(1f)
                        )

                        // KPI 2: Total Questions
                        KpiTrendCard(
                            title = "Total Questions",
                            value = "182,450",
                            trendText = "+8.7% this week",
                            isPositiveTrend = true,
                            icon = Icons.Default.Book,
                            iconTint = VioletAccent,
                            iconBg = Color(0xFFF5F3FF),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // USER-ACTIVITY LINE CHART
                    FloatingCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("user_activity_chart_card"),
                        cornerRadius = 18.dp,
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "User Activity Trend",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextOnWhitePrimary
                                    )
                                    Text(
                                        text = "Daily active questions generated & solved over last 7 days",
                                        fontSize = 12.sp,
                                        color = SlateGray
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFEEF2FF))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "Last 7 Days",
                                        color = VioletAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Custom Canvas User Activity Line Chart
                            UserActivityLineChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        }
                    }

                    // "RECENT USERS" LIST
                    FloatingCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recent_users_table_card"),
                        cornerRadius = 18.dp,
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recent Users",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextOnWhitePrimary
                                )

                                Text(
                                    text = "5 new today",
                                    fontSize = 12.sp,
                                    color = SlateGray
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("User", modifier = Modifier.weight(1.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateGray)
                                Text("Exam / Grade", modifier = Modifier.weight(1.2f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateGray)
                                Text("Questions", modifier = Modifier.weight(0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateGray)
                                Text("Status", modifier = Modifier.weight(0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateGray)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // User Rows
                            recentUsers.forEach { user ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // User Column (Avatar + Name + Email)
                                    Row(
                                        modifier = Modifier.weight(1.5f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryGradient),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = user.name.take(1),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = user.name,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                                color = TextOnWhitePrimary
                                            )
                                            Text(
                                                text = user.email,
                                                fontSize = 11.sp,
                                                color = SlateGray
                                            )
                                        }
                                    }

                                    // Exam / Grade Column
                                    Text(
                                        text = user.classExam,
                                        modifier = Modifier.weight(1.2f),
                                        fontSize = 12.sp,
                                        color = TextOnWhitePrimary
                                    )

                                    // Questions Solved
                                    Text(
                                        text = "${user.questionsSolved}",
                                        modifier = Modifier.weight(0.8f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = VioletAccent
                                    )

                                    // Status Badge
                                    Box(
                                        modifier = Modifier.weight(0.8f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (user.isPremium) Color(0xFFFEF3C7) else Color(0xFFECFDF5))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = if (user.isPremium) "Premium" else "Active",
                                                color = if (user.isPremium) Color(0xFFB45309) else SuccessGreen,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * KPI Card with title, bold count, and small "+X%" trend indicator
 */
@Composable
private fun KpiTrendCard(
    title: String,
    value: String,
    trendText: String,
    isPositiveTrend: Boolean,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    FloatingCard(
        modifier = modifier.testTag("kpi_card_${title.lowercase().replace(" ", "_")}"),
        cornerRadius = 18.dp,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateGray
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextOnWhitePrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Small trend indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = if (isPositiveTrend) SuccessGreen else ErrorRed,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = trendText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositiveTrend) SuccessGreen else ErrorRed
                )
            }
        }
    }
}

/**
 * User-activity Line Chart drawn cleanly on Canvas
 */
@Composable
private fun UserActivityLineChart(modifier: Modifier = Modifier) {
    val dataPoints = remember { listOf(18f, 25f, 22f, 38f, 32f, 45f, 54f) }
    val days = remember { listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun") }

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val width = size.width
            val height = size.height
            val maxVal = 60f

            // Draw horizontal grid lines
            val gridLines = 4
            for (i in 0..gridLines) {
                val y = height * (i.toFloat() / gridLines)
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val stepX = width / (dataPoints.size - 1)
            val path = Path()
            val fillPath = Path()

            val points = dataPoints.mapIndexed { index, value ->
                val x = index * stepX
                val y = height - (value / maxVal * height)
                Offset(x, y)
            }

            fillPath.moveTo(0f, height)

            points.forEachIndexed { index, pt ->
                if (index == 0) {
                    path.moveTo(pt.x, pt.y)
                    fillPath.lineTo(pt.x, pt.y)
                } else {
                    val prev = points[index - 1]
                    val controlX1 = prev.x + (pt.x - prev.x) / 2f
                    val controlY1 = prev.y
                    val controlX2 = prev.x + (pt.x - prev.x) / 2f
                    val controlY2 = pt.y
                    path.cubicTo(controlX1, controlY1, controlX2, controlY2, pt.x, pt.y)
                    fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, pt.x, pt.y)
                }
            }

            fillPath.lineTo(width, height)
            fillPath.close()

            // Draw gradient fill under chart
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(VioletAccent.copy(alpha = 0.25f), Color.Transparent),
                    startY = 0f,
                    endY = height
                )
            )

            // Draw line
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(listOf(VioletAccent, BlueAccent)),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw points
            points.forEach { pt ->
                drawCircle(
                    color = Color.White,
                    radius = 5.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = VioletAccent,
                    radius = 3.dp.toPx(),
                    center = pt
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days of week labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { day ->
                Text(
                    text = day,
                    fontSize = 11.sp,
                    color = SlateGray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
