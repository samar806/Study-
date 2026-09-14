package com.example.ui.screens.generator

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CustomDropdownField
import com.example.ui.components.FloatingCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.VioletAccent
import kotlinx.coroutines.launch

data class SampleImageThumbnail(
    val title: String,
    val noteSnippet: String,
    val badgeColor: Color
)

@Composable
fun ImageGenerationScreen(
    onBack: () -> Unit,
    onGenerate: (pageDescriptions: List<String>, difficulty: String, count: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dropdowns
    var selectedDifficulty by remember { mutableStateOf("Medium") }
    var selectedMcqCount by remember { mutableStateOf("20") }

    val difficultyOptions = listOf("Easy", "Medium", "Hard", "Very Hard")
    val countOptions = listOf("10", "15", "20", "25", "50")

    // Mock Uploaded Thumbnails
    val sampleThumbnails = remember {
        mutableStateListOf(
            SampleImageThumbnail("Page 42", "Newton's 2nd Law\nF = dp/dt\nF = ma", Color(0xFFE0E7FF)),
            SampleImageThumbnail("Page 43", "Free Body Diagram\nNormal force & Friction", Color(0xFFFEF3C7)),
            SampleImageThumbnail("Handwritten", "Practice Problems #1-5\nPulleys & Incline", Color(0xFFDCFCE7))
        )
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
                        modifier = Modifier.testTag("image_gen_back_button")
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
                            text = "Generate from Image",
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
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Large Dashed-Border Drop-Zone
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .drawBehind {
                                val stroke = Stroke(
                                    width = 3.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
                                )
                                drawRoundRect(
                                    color = Color(0xFFCBD5E1),
                                    style = stroke,
                                    cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
                                )
                            }
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .clickable {
                                sampleThumbnails.add(
                                    SampleImageThumbnail(
                                        "New Photo ${sampleThumbnails.size + 1}",
                                        "Formula sheet & Summary",
                                        Color(0xFFF3E8FF)
                                    )
                                )
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Sample page captured & added!")
                                }
                            }
                            .padding(20.dp)
                            .testTag("dashed_image_dropzone"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFEEF2FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = VioletAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Upload or capture an image",
                                color = TextOnWhitePrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Textbook pages, notes, diagrams, worksheets, question papers, etc.",
                                color = SlateGray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }

                    // Row of uploaded thumbnails + "+" tile
                    Column {
                        Text(
                            text = "Uploaded Pages (${sampleThumbnails.size})",
                            color = TextOnWhitePrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(sampleThumbnails) { index, item ->
                                ThumbnailCard(
                                    thumbnail = item,
                                    onDelete = {
                                        if (sampleThumbnails.size > 1) {
                                            sampleThumbnails.removeAt(index)
                                        } else {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Keep at least one page to generate.")
                                            }
                                        }
                                    }
                                )
                            }

                            // "+" tile
                            item {
                                AddMoreTile(
                                    onClick = {
                                        sampleThumbnails.add(
                                            SampleImageThumbnail(
                                                "Page ${sampleThumbnails.size + 1}",
                                                "Sample note notes excerpt",
                                                Color(0xFFFEF3C7)
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Difficulty and Number of MCQs Dropdowns Card
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
                            CustomDropdownField(
                                label = "Difficulty",
                                selectedValue = selectedDifficulty,
                                options = difficultyOptions,
                                onSelectOption = { selectedDifficulty = it },
                                modifier = Modifier.testTag("dropdown_image_difficulty")
                            )

                            CustomDropdownField(
                                label = "Number of MCQs",
                                selectedValue = selectedMcqCount,
                                options = countOptions,
                                onSelectOption = { selectedMcqCount = it },
                                modifier = Modifier.testTag("dropdown_image_mcq_count")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Sticky Bottom Section with OCR Helper line
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardWhite,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GradientButton(
                        text = "Generate MCQs",
                        onClick = {
                            val descriptions = sampleThumbnails.map { "${it.title}: ${it.noteSnippet.replace("\n", " ")}" }
                            val count = selectedMcqCount.toIntOrNull() ?: 20
                            onGenerate(descriptions, selectedDifficulty, count)
                        },
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                            .testTag("image_generate_mcqs_button")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "AI will read the image using OCR and generate relevant questions.",
                        color = SlateGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )
    }
}

@Composable
private fun ThumbnailCard(
    thumbnail: SampleImageThumbnail,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 86.dp, height = 110.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CardBorderLight, RoundedCornerShape(14.dp))
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            // Fake miniature note lines representation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(thumbnail.badgeColor)
                    .padding(6.dp)
            ) {
                Text(
                    text = thumbnail.noteSnippet,
                    fontSize = 7.sp,
                    lineHeight = 9.sp,
                    color = Color(0xFF334155),
                    maxLines = 5
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = thumbnail.title,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextOnWhitePrimary,
                maxLines = 1
            )
        }

        // Close delete button
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(3.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(Color(0xCC000000))
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

@Composable
private fun AddMoreTile(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 86.dp, height = 110.dp)
            .drawBehind {
                val stroke = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                )
                drawRoundRect(
                    color = Color(0xFFCBD5E1),
                    style = stroke,
                    cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEF2FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add image",
                    tint = VioletAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add page",
                color = SlateGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
