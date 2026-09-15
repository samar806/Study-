package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VioletAccent
import java.util.UUID

/**
 * Model representing an uploaded textbook, worksheet, or handwritten answer page.
 */
data class UploadedPage(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri? = null,
    val bitmap: Bitmap? = null,
    val title: String,
    val noteSnippet: String = "",
    val rotationDegrees: Float = 0f, // 0, 90, 180, 270
    val badgeColor: Color = Color(0xFFE0E7FF)
)

/**
 * Action type to handle whether the picker will add a new page or replace an existing page.
 */
sealed class PickerTarget {
    object AddNew : PickerTarget()
    data class ReplaceExisting(val pageId: String) : PickerTarget()
}

/**
 * Main clickable "Upload or capture an image" banner card.
 */
@Composable
fun MainUploadDropZone(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Upload or capture an image",
    subtitle: String = "Textbook pages, notes, diagrams, worksheets, question papers, etc.",
    testTag: String = "dashed_image_dropzone"
) {
    Box(
        modifier = modifier
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(20.dp)
            .testTag(testTag),
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
                    contentDescription = "Upload or Capture",
                    tint = VioletAccent,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                color = TextOnWhitePrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = SlateGray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

/**
 * Section displaying the list of uploaded page thumbnails with quick delete ('X'),
 * tap-to-preview, and the "+ Add page" tile.
 */
@Composable
fun UploadedPagesSection(
    pages: List<UploadedPage>,
    onAddPageClick: () -> Unit,
    onThumbnailClick: (page: UploadedPage) -> Unit,
    onDeletePage: (pageId: String) -> Unit,
    modifier: Modifier = Modifier,
    sectionTitle: String = "Uploaded Pages"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$sectionTitle (${pages.size})",
                color = TextOnWhitePrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (pages.isNotEmpty()) {
                Text(
                    text = "Tap thumbnail to preview or edit",
                    color = SlateGray,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(
                items = pages,
                key = { _, item -> item.id }
            ) { _, item ->
                UploadedPageThumbnailCard(
                    page = item,
                    onClick = { onThumbnailClick(item) },
                    onDelete = { onDeletePage(item.id) }
                )
            }

            // "+ Add page" tile
            item(key = "add_page_tile") {
                AddPageTile(
                    onClick = onAddPageClick,
                    modifier = Modifier.testTag("add_page_card_button")
                )
            }
        }
    }
}

/**
 * Individual thumbnail card.
 * Tapping anywhere on the thumbnail opens the preview modal.
 * Tapping the 'X' button in top-right immediately deletes the item without opening preview.
 */
@Composable
fun UploadedPageThumbnailCard(
    page: UploadedPage,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 88.dp, height = 114.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CardBorderLight, RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .testTag("page_thumbnail_${page.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            // Visual Image preview container with rotation applied
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(page.badgeColor)
                    .graphicsLayer(rotationZ = page.rotationDegrees),
                contentAlignment = Alignment.Center
            ) {
                when {
                    page.uri != null -> {
                        SubcomposeAsyncImage(
                            model = page.uri,
                            contentDescription = page.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(16.dp),
                                        color = VioletAccent
                                    )
                                }
                            },
                            error = {
                                SamplePageIllustration(snippet = page.noteSnippet)
                            }
                        )
                    }
                    page.bitmap != null -> {
                        androidx.compose.foundation.Image(
                            bitmap = page.bitmap.asImageBitmap(),
                            contentDescription = page.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        SamplePageIllustration(snippet = page.noteSnippet)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = page.title,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextOnWhitePrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Separate, isolated Quick Remove 'X' Badge
        // Outer box provides full 44-48dp touch target while visual circle is crisp 20dp
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(36.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDelete
                )
                .testTag("delete_badge_${page.id}"),
            contentAlignment = Alignment.TopEnd
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xEE1E293B))
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove ${page.title}",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

/**
 * Visual illustration for sample/mock pages with styled diagram lines.
 */
@Composable
fun SamplePageIllustration(
    snippet: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = snippet.ifEmpty { "Sample Page Content" },
                fontSize = 7.sp,
                lineHeight = 9.sp,
                color = Color(0xFF334155),
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
            // Decorative mock diagram/formula line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 24.dp, height = 2.dp)
                        .background(VioletAccent.copy(alpha = 0.6f), RoundedCornerShape(1.dp))
                )
            }
        }
    }
}

/**
 * The "+ Add page" card shown beside uploaded pages.
 */
@Composable
fun AddPageTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 88.dp, height = 114.dp)
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEF2FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add page",
                    tint = VioletAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "+ Add page",
                color = SlateGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Image Source Picker Dialog:
 * Offers Take Photo (Camera), Choose from Photos/Gallery, Browse Files (JPG/PNG/WEBP),
 * and Preset Textbook samples for rapid testing.
 */
@Composable
fun ImageSourcePickerDialog(
    isOpen: Boolean,
    title: String = "Select Image Source",
    onDismiss: () -> Unit,
    onLaunchCamera: () -> Unit,
    onLaunchGallery: () -> Unit,
    onLaunchFiles: () -> Unit,
    onSelectSample: (title: String, snippet: String, color: Color) -> Unit
) {
    if (!isOpen) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEF2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = VioletAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnWhitePrimary
                    )
                    Text(
                        text = "Supports JPG, JPEG, PNG, WEBP",
                        fontSize = 11.sp,
                        color = SlateGray
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SourceOptionItem(
                    icon = Icons.Default.CameraAlt,
                    iconBg = Color(0xFFFEF3C7),
                    iconTint = Color(0xFFD97706),
                    title = "Take Photo with Camera",
                    subtitle = "Capture a textbook page or handwritten notes",
                    onClick = {
                        onDismiss()
                        onLaunchCamera()
                    },
                    testTag = "picker_option_camera"
                )

                SourceOptionItem(
                    icon = Icons.Default.PhotoLibrary,
                    iconBg = Color(0xFFE0E7FF),
                    iconTint = VioletAccent,
                    title = "Choose from Photos / Gallery",
                    subtitle = "Select from saved photos on device",
                    onClick = {
                        onDismiss()
                        onLaunchGallery()
                    },
                    testTag = "picker_option_gallery"
                )

                SourceOptionItem(
                    icon = Icons.Default.Description,
                    iconBg = Color(0xFFDCFCE7),
                    iconTint = SuccessGreen,
                    title = "Browse Files (PDF/Image)",
                    subtitle = "Choose from device downloads or documents",
                    onClick = {
                        onDismiss()
                        onLaunchFiles()
                    },
                    testTag = "picker_option_files"
                )

                // Quick preset for instant review
                SourceOptionItem(
                    icon = Icons.Default.AutoAwesome,
                    iconBg = Color(0xFFF3E8FF),
                    iconTint = Color(0xFF9333EA),
                    title = "NCERT Textbook Sample Page",
                    subtitle = "Preloaded Physics / Chemistry diagram page",
                    onClick = {
                        val randomSamples = listOf(
                            Triple("Page 44", "Laws of Motion\nConservation of Momentum\nm1v1 + m2v2 = const", Color(0xFFE0E7FF)),
                            Triple("Page 45", "Equilibrium of Particles\nLami's Theorem\nFree Body Analysis", Color(0xFFFEF3C7)),
                            Triple("Worksheet #3", "Projectile Motion\nRange R = u^2 sin(2θ)/g\nMax height H", Color(0xFFDCFCE7)),
                            Triple("Diagram Page", "Electric Field Lines\nGauss Law flux Φ = Q/ε0\nSurface Integral", Color(0xFFFCE7F3))
                        )
                        val picked = randomSamples.random()
                        onSelectSample(picked.first, picked.second, picked.third)
                        onDismiss()
                    },
                    testTag = "picker_option_sample"
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("picker_cancel_button")
            ) {
                Text("Cancel", color = SlateGray, fontWeight = FontWeight.Medium)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun SourceOptionItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CardBorderLight),
        color = Color(0xFFF8FAFC)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextOnWhitePrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = SlateGray
                )
            }
        }
    }
}

/**
 * Full-screen or large modal image preview.
 * Includes:
 * - Pinch-to-zoom on mobile + Zoom In / Zoom Out controls
 * - Double tap to zoom toggle
 * - Rotate 90 degrees button (each tap rotates 90°)
 * - Replace button (opens picker to replace only this page)
 * - Remove this page button (removes page and closes preview)
 * - Close (X) button / tap outside to close
 */
@Composable
fun ImagePreviewModal(
    page: UploadedPage?,
    isOpen: Boolean,
    pageIndex: Int,
    totalPages: Int,
    onClose: () -> Unit,
    onRotate: (pageId: String) -> Unit,
    onReplace: (pageId: String) -> Unit,
    onRemove: (pageId: String) -> Unit
) {
    if (!isOpen || page == null) return

    // Interactive zoom and pan states
    var scale by remember(page.id) { mutableFloatStateOf(1f) }
    var offset by remember(page.id) { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE60F172A))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClose
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* prevent backdrop tap inside content */ }
                    )
            ) {
                // Top Header Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xF21E293B)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = page.title,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(VioletAccent.copy(alpha = 0.3f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${page.rotationDegrees.toInt()}°",
                                        color = Color(0xFFC7D2FE),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "Page ${pageIndex + 1} of $totalPages • Pinch or use buttons to zoom",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        // Close button (touch target >= 48dp)
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("preview_close_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33FFFFFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close preview",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Main Image Zoomable / Rotatable Canvas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(0.dp))
                        .pointerInput(page.id) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (scale > 1.2f) {
                                        scale = 1f
                                        offset = Offset.Zero
                                    } else {
                                        scale = 2.2f
                                    }
                                }
                            )
                        }
                        .pointerInput(page.id) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(0.5f, 5f)
                                offset += pan
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                                rotationZ = page.rotationDegrees
                            }
                            .padding(24.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                            .widthIn(max = 480.dp)
                            .heightIn(max = 560.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            page.uri != null -> {
                                SubcomposeAsyncImage(
                                    model = page.uri,
                                    contentDescription = page.title,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize(),
                                    loading = {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(color = VioletAccent)
                                        }
                                    },
                                    error = {
                                        SamplePageIllustrationLarge(page)
                                    }
                                )
                            }
                            page.bitmap != null -> {
                                androidx.compose.foundation.Image(
                                    bitmap = page.bitmap.asImageBitmap(),
                                    contentDescription = page.title,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else -> {
                                SamplePageIllustrationLarge(page)
                            }
                        }
                    }

                    // Floating Zoom In / Out / Reset Controls on side
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xCC1E293B))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = { scale = (scale * 1.3f).coerceAtMost(5f) },
                            modifier = Modifier.size(44.dp).testTag("preview_zoom_in_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = "Zoom in",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = {
                                scale = 1f
                                offset = Offset.Zero
                            },
                            modifier = Modifier.size(44.dp).testTag("preview_zoom_reset_button")
                        ) {
                            Text(
                                text = "${(scale * 100).toInt()}%",
                                color = Color(0xFFC7D2FE),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { scale = (scale / 1.3f).coerceAtLeast(0.5f) },
                            modifier = Modifier.size(44.dp).testTag("preview_zoom_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomOut,
                                contentDescription = "Zoom out",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Bottom Action Toolbar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xF21E293B)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Rotate 90° Button
                        ActionButton(
                            icon = Icons.Default.RotateRight,
                            label = "Rotate",
                            badge = "+90°",
                            onClick = { onRotate(page.id) },
                            testTag = "preview_rotate_button"
                        )

                        // 2. Replace Button
                        ActionButton(
                            icon = Icons.Default.SwapHoriz,
                            label = "Replace",
                            onClick = { onReplace(page.id) },
                            testTag = "preview_replace_button"
                        )

                        // 3. Remove This Page Button
                        ActionButton(
                            icon = Icons.Default.Delete,
                            label = "Remove",
                            tint = ErrorRed,
                            onClick = { onRemove(page.id) },
                            testTag = "preview_remove_button"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    badge: String? = null,
    tint: Color = Color.White,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = Color(0x33FFFFFF)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = tint,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (badge != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = badge,
                    color = Color(0xFFC7D2FE),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SamplePageIllustrationLarge(page: UploadedPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(page.badgeColor)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = page.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextOnWhitePrimary
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "NCERT Curriculum",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VioletAccent
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = Color.White,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = page.noteSnippet.ifEmpty {
                        "Newton's Laws of Motion:\n\n1. Every object continues in a state of rest or uniform motion.\n2. F = dp/dt = m(dv/dt) = ma.\n3. To every action there is an equal and opposite reaction.\n\nFree Body Diagram Analysis:\n• Normal reaction perpendicular to surface\n• Friction f_s <= μ_s * N opposes relative motion"
                    },
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = Color(0xFF1E293B)
                )
            }
        }
    }
}
