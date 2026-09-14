package com.example.ui.screens.practice

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingCard
import com.example.ui.components.GradientButton
import com.example.ui.components.ImagePreviewModal
import com.example.ui.components.ImageSourcePickerDialog
import com.example.ui.components.MainUploadDropZone
import com.example.ui.components.PickerTarget
import com.example.ui.components.UploadedPage
import com.example.ui.components.UploadedPagesSection
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.VioletAccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WrittenAnswerPracticeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Uploaded handwritten answer pages
    val uploadedPages = remember {
        mutableStateListOf(
            UploadedPage(
                title = "Answer Page 1",
                noteSnippet = "Q1. State Newton's Second Law:\nRate of change of momentum is proportional to applied force.\ndp/dt = d(mv)/dt = m(dv/dt) = ma",
                badgeColor = Color(0xFFE0E7FF),
                rotationDegrees = 0f
            ),
            UploadedPage(
                title = "Answer Page 2",
                noteSnippet = "Free Body Diagram & Proof:\nImpulse = Integral(F dt) = change in momentum Δp.\nUnits: N·s or kg·m/s",
                badgeColor = Color(0xFFFEF3C7),
                rotationDegrees = 0f
            )
        )
    }

    var isPickerOpen by remember { mutableStateOf(false) }
    var pickerTarget by remember { mutableStateOf<PickerTarget>(PickerTarget.AddNew) }
    var previewingPageId by remember { mutableStateOf<String?>(null) }
    var isEvaluating by remember { mutableStateOf(false) }
    var evaluationResult by remember { mutableStateOf<String?>(null) }

    val currentPreviewPage = uploadedPages.find { it.id == previewingPageId }
    val previewIndex = uploadedPages.indexOfFirst { it.id == previewingPageId }

    val handleImageAcquired: (Uri?, Bitmap?, String, String, Color) -> Unit = { uri, bitmap, title, snippet, color ->
        when (val target = pickerTarget) {
            is PickerTarget.AddNew -> {
                val newPage = UploadedPage(
                    uri = uri,
                    bitmap = bitmap,
                    title = title.ifEmpty { "Answer Sheet ${uploadedPages.size + 1}" },
                    noteSnippet = snippet.ifEmpty { "Handwritten answer page" },
                    badgeColor = color,
                    rotationDegrees = 0f
                )
                uploadedPages.add(newPage)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Answer page uploaded!")
                }
            }
            is PickerTarget.ReplaceExisting -> {
                val index = uploadedPages.indexOfFirst { it.id == target.pageId }
                if (index != -1) {
                    val existing = uploadedPages[index]
                    val updated = existing.copy(
                        uri = uri,
                        bitmap = bitmap,
                        title = title.ifEmpty { existing.title },
                        noteSnippet = snippet.ifEmpty { existing.noteSnippet },
                        badgeColor = color,
                        rotationDegrees = 0f
                    )
                    uploadedPages[index] = updated
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Answer page replaced!")
                    }
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { capturedBitmap ->
        if (capturedBitmap != null) {
            handleImageAcquired(
                null,
                capturedBitmap,
                "Camera Sheet ${uploadedPages.size + 1}",
                "Captured answer sheet from camera",
                Color(0xFFFEF3C7)
            )
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedUri ->
        if (selectedUri != null) {
            handleImageAcquired(
                selectedUri,
                null,
                "Gallery Sheet ${uploadedPages.size + 1}",
                "Selected from gallery",
                Color(0xFFE0E7FF)
            )
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedUri ->
        if (selectedUri != null) {
            handleImageAcquired(
                selectedUri,
                null,
                "File Sheet ${uploadedPages.size + 1}",
                "Imported file sheet",
                Color(0xFFDCFCE7)
            )
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
                        modifier = Modifier.testTag("written_practice_back_button")
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
                            text = "Written Answer Practice",
                            color = TextOnWhitePrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Upload handwritten pages for step-by-step grading",
                            color = SlateGray,
                            fontSize = 12.sp
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
                    // Question Prompt Card
                    FloatingCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp,
                        elevation = 2.dp
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
                                    Icon(
                                        imageVector = Icons.Default.Assignment,
                                        contentDescription = null,
                                        tint = VioletAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Question 1 (5 Marks)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VioletAccent
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFEEF2FF))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Class 11 Physics",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VioletAccent
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "State and prove Newton's Second Law of Motion (F = ma). Derive the impulse-momentum relationship and include a neat free body diagram for verification.",
                                color = TextOnWhitePrimary,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Upload area banner (fully clickable)
                    MainUploadDropZone(
                        onClick = {
                            pickerTarget = PickerTarget.AddNew
                            isPickerOpen = true
                        },
                        title = "Upload handwritten answer sheet",
                        subtitle = "Capture photo of notebook or select scanned pages (JPG, PNG, WEBP)",
                        testTag = "written_upload_dropzone"
                    )

                    // Uploaded Pages Section with thumbnails, tap to preview, quick 'X' delete, and "+ Add page"
                    UploadedPagesSection(
                        pages = uploadedPages,
                        onAddPageClick = {
                            pickerTarget = PickerTarget.AddNew
                            isPickerOpen = true
                        },
                        onThumbnailClick = { page ->
                            previewingPageId = page.id
                        },
                        onDeletePage = { pageId ->
                            uploadedPages.removeAll { it.id == pageId }
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Answer page removed.")
                            }
                        },
                        sectionTitle = "Uploaded Answer Sheets"
                    )

                    // Evaluation Summary Card if evaluated
                    if (evaluationResult != null) {
                        FloatingCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 20.dp,
                            elevation = 3.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "AI Evaluation Score: 4.5 / 5.0",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextOnWhitePrimary
                                    )
                                }

                                Text(
                                    text = evaluationResult ?: "",
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = Color(0xFF334155)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Bottom Submit Button
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
                    if (isEvaluating) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            CircularProgressIndicator(
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(24.dp),
                                color = VioletAccent
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Evaluating handwritten steps with OCR...",
                                color = TextOnWhitePrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        GradientButton(
                            text = "Submit for AI Evaluation",
                            onClick = {
                                if (uploadedPages.isEmpty()) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Please upload at least one answer page.")
                                    }
                                    return@GradientButton
                                }
                                isEvaluating = true
                                coroutineScope.launch {
                                    delay(1200)
                                    isEvaluating = false
                                    evaluationResult = "✅ Step 1 (Definition): Correctly stated law of momentum.\n✅ Step 2 (Mathematical Derivation): F = dp/dt = ma derived accurately.\n✅ Step 3 (Impulse Formula): Accurate relationship J = Δp with SI unit.\n💡 Suggestion: Label normal force and gravity vector arrows more clearly in the free body diagram for full marks."
                                    snackbarHostState.showSnackbar("Evaluation complete!")
                                }
                            },
                            modifier = Modifier
                                .widthIn(max = 500.dp)
                                .fillMaxWidth()
                                .testTag("written_submit_button")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "AI inspects handwritten diagrams, derivations, and step accuracy.",
                        color = SlateGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Image Source Picker Modal
        ImageSourcePickerDialog(
            isOpen = isPickerOpen,
            title = if (pickerTarget is PickerTarget.AddNew) "Add Answer Sheet" else "Replace Answer Sheet",
            onDismiss = { isPickerOpen = false },
            onLaunchCamera = { cameraLauncher.launch(null) },
            onLaunchGallery = { galleryLauncher.launch("image/*") },
            onLaunchFiles = { filePickerLauncher.launch("image/*") },
            onSelectSample = { title, snippet, color ->
                handleImageAcquired(null, null, title, snippet, color)
            }
        )

        // Modal Image Preview with Pinch-Zoom, Rotate 90°, Replace, and Remove
        ImagePreviewModal(
            page = currentPreviewPage,
            isOpen = previewingPageId != null,
            pageIndex = previewIndex,
            totalPages = uploadedPages.size,
            onClose = { previewingPageId = null },
            onRotate = { pageId ->
                val idx = uploadedPages.indexOfFirst { it.id == pageId }
                if (idx != -1) {
                    val current = uploadedPages[idx]
                    uploadedPages[idx] = current.copy(
                        rotationDegrees = (current.rotationDegrees + 90f) % 360f
                    )
                }
            },
            onReplace = { pageId ->
                pickerTarget = PickerTarget.ReplaceExisting(pageId)
                isPickerOpen = true
            },
            onRemove = { pageId ->
                uploadedPages.removeAll { it.id == pageId }
                previewingPageId = null
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Answer sheet removed.")
                }
            }
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )
    }
}
