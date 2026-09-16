package com.example.ui.screens.doubt

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.service.GeminiMcqService
import com.example.ui.components.ChatInputBar
import com.example.ui.navigation.RepeatingHeader
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.VioletAccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class DoubtChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String = "",
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val isThinking: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val attachmentName: String? = null,
    val attachmentType: String? = null, // "IMAGE", "PDF", "NOTE"
    val imageUrl: String? = null,
    val imageCaption: String? = null,
    val isGeneratingImage: Boolean = false,
    val imageError: String? = null
)

data class AttachmentInfo(
    val name: String,
    val type: String, // "IMAGE", "PDF", "NOTE"
    val contextDescription: String
)

@Composable
fun DoubtSectionScreen(
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val geminiService = remember { GeminiMcqService() }

    var chatInput by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var activeAttachment by remember { mutableStateOf<AttachmentInfo?>(null) }
    var showAttachmentModal by remember { mutableStateOf(false) }
    var previewImageUrl by remember { mutableStateOf<String?>(null) }

    val messages = remember {
        mutableStateListOf(
            DoubtChatMessage(
                id = "welcome_1",
                text = "Hi! I am your **AI Doubt Solver**. Ask me any question, attach notes or photos, or tap **'🖼️ Explain with a picture'** to generate visual diagrams!",
                isUser = false
            )
        )
    }

    // Helper to check if student query asks for an image/picture/diagram
    fun isImageRequest(query: String): Boolean {
        val q = query.lowercase()
        val keywords = listOf(
            "picture", "image", "diagram", "drawing", "figure", "photo", "illustration", "sketch", "draw"
        )
        return keywords.any { q.contains(it) }
    }

    // Helper to generate explanatory diagram image for a message
    fun requestDiagramForMessage(msg: DoubtChatMessage, customTopic: String? = null) {
        val index = messages.indexOfFirst { it.id == msg.id }
        if (index == -1) return

        messages[index] = messages[index].copy(isGeneratingImage = true, imageError = null)

        coroutineScope.launch {
            val topicToUse = customTopic ?: msg.text
            val result = geminiService.generateEducationalDiagram(
                conceptTopic = topicToUse,
                seed = System.currentTimeMillis()
            )
            val updatedIdx = messages.indexOfFirst { it.id == msg.id }
            if (updatedIdx != -1) {
                if (result.isSuccess) {
                    val (url, caption) = result.getOrNull()!!
                    messages[updatedIdx] = messages[updatedIdx].copy(
                        isGeneratingImage = false,
                        imageUrl = url,
                        imageCaption = caption,
                        imageError = null
                    )
                } else {
                    messages[updatedIdx] = messages[updatedIdx].copy(
                        isGeneratingImage = false,
                        imageError = "Couldn't create a diagram for this — try rephrasing?"
                    )
                }
            }
        }
    }

    // Helper to trigger sending message
    fun triggerSendMessage(queryText: String, attachment: AttachmentInfo?) {
        if (queryText.isBlank() && attachment == null) return

        val userMsgText = queryText.ifBlank { "Please explain the attached document/photo." }
        val userMsgId = System.currentTimeMillis().toString()
        val hasImageIntent = isImageRequest(userMsgText)

        // 1. Add User Message
        messages.add(
            DoubtChatMessage(
                id = userMsgId,
                text = userMsgText,
                isUser = true,
                attachmentName = attachment?.name,
                attachmentType = attachment?.type
            )
        )

        val attachmentContext = attachment?.contextDescription
        chatInput = ""
        activeAttachment = null

        // 2. Add Thinking Placeholder
        val thinkingMsgId = "thinking_${System.currentTimeMillis()}"
        messages.add(
            DoubtChatMessage(
                id = thinkingMsgId,
                text = "",
                isUser = false,
                isThinking = true
            )
        )

        // Scroll to bottom
        coroutineScope.launch {
            delay(100)
            listState.animateScrollToItem((messages.size - 1).coerceAtLeast(0))
        }

        // 3. Call Real Gemini API & Image Model
        coroutineScope.launch {
            val history = messages.filter { !it.isThinking && !it.isError }.map { msg ->
                GeminiMcqService.DoubtContextMessage(
                    text = msg.text,
                    isUser = msg.isUser,
                    attachmentContext = if (msg.isUser) msg.attachmentName else null
                )
            }

            val systemNote = if (hasImageIntent) {
                "\n[System Note: The student requested a visual image/diagram. Provide a brief 1-2 sentence text overview. An educational image generator is rendering the diagram inline alongside your message. Do NOT state that you cannot draw or generate pictures.]"
            } else ""

            val result = geminiService.askDoubtWithContext(
                history = history.dropLast(1), // Exclude the user message we just added
                studentQuery = userMsgText + systemNote,
                attachmentContext = attachmentContext
            )

            val thinkingIndex = messages.indexOfFirst { it.id == thinkingMsgId }
            if (thinkingIndex != -1) {
                if (result.isSuccess) {
                    val replyText = result.getOrNull().orEmpty()
                    val newAiMsg = DoubtChatMessage(
                        id = System.currentTimeMillis().toString(),
                        text = replyText,
                        isUser = false,
                        isGeneratingImage = hasImageIntent
                    )
                    messages[thinkingIndex] = newAiMsg

                    // Trigger image generation directly if image intent was detected!
                    if (hasImageIntent) {
                        requestDiagramForMessage(newAiMsg, customTopic = userMsgText)
                    }
                } else {
                    val errorDetail = result.exceptionOrNull()?.message ?: "Network error"
                    messages[thinkingIndex] = DoubtChatMessage(
                        id = System.currentTimeMillis().toString(),
                        text = userMsgText, // store original query for retry
                        isUser = false,
                        isError = true,
                        errorMessage = "Could not reach AI Teacher. ($errorDetail)"
                    )
                }
            }

            delay(100)
            listState.animateScrollToItem((messages.size - 1).coerceAtLeast(0))
        }
    }

    // Helper to retry a failed message
    fun retryFailedMessage(failedMsg: DoubtChatMessage) {
        val failedIndex = messages.indexOfFirst { it.id == failedMsg.id }
        if (failedIndex != -1) {
            val queryText = failedMsg.text
            val hasImageIntent = isImageRequest(queryText)
            messages[failedIndex] = DoubtChatMessage(
                id = failedMsg.id,
                text = "",
                isUser = false,
                isThinking = true
            )

            coroutineScope.launch {
                val history = messages.filter { !it.isThinking && !it.isError }.map { msg ->
                    GeminiMcqService.DoubtContextMessage(
                        text = msg.text,
                        isUser = msg.isUser
                    )
                }

                val systemNote = if (hasImageIntent) {
                    "\n[System Note: The student requested a visual image/diagram. Provide a brief 1-2 sentence text overview. Do NOT state that you cannot draw.]"
                } else ""

                val result = geminiService.askDoubtWithContext(
                    history = history,
                    studentQuery = queryText + systemNote
                )

                val idx = messages.indexOfFirst { it.id == failedMsg.id }
                if (idx != -1) {
                    if (result.isSuccess) {
                        val newAiMsg = DoubtChatMessage(
                            id = System.currentTimeMillis().toString(),
                            text = result.getOrNull().orEmpty(),
                            isUser = false,
                            isGeneratingImage = hasImageIntent
                        )
                        messages[idx] = newAiMsg
                        if (hasImageIntent) {
                            requestDiagramForMessage(newAiMsg, customTopic = queryText)
                        }
                    } else {
                        messages[idx] = DoubtChatMessage(
                            id = failedMsg.id,
                            text = queryText,
                            isUser = false,
                            isError = true,
                            errorMessage = result.exceptionOrNull()?.message ?: "Retry failed."
                        )
                    }
                }
            }
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                RepeatingHeader(
                    title = "Doubt Section",
                    subtitle = "Real-time AI Teacher with Diagram Generation",
                    onBackClick = onBack,
                    onOpenDrawer = onOpenDrawer
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(messages, key = { _, item -> item.id }) { _, message ->
                    MessageBubbleItem(
                        message = message,
                        onRetry = { retryFailedMessage(message) },
                        onExplainWithPicture = { requestDiagramForMessage(message) },
                        onImageClick = { url -> previewImageUrl = url }
                    )
                }
            }
        }

        // Bottom Input Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .shadow(elevation = 16.dp, ambientColor = Color(0x33000000)),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Active attachment chip indicator
                activeAttachment?.let { att ->
                    Surface(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (att.type == "IMAGE") Icons.Default.Image else Icons.Default.Description,
                                contentDescription = null,
                                tint = VioletAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Attached: ${att.name}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove attachment",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { activeAttachment = null }
                            )
                        }
                    }
                }

                ChatInputBar(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    placeholder = if (activeAttachment != null) "Ask about this attachment..." else "Ask any doubt or concept...",
                    hasAttachment = activeAttachment != null,
                    onAttachClick = { showAttachmentModal = true },
                    onMicClick = {
                        isRecording = !isRecording
                        if (isRecording) {
                            coroutineScope.launch {
                                delay(1200)
                                chatInput = "Explain the structure of a plant cell and its organelles"
                                isRecording = false
                            }
                        }
                    },
                    onSendClick = {
                        triggerSendMessage(chatInput, activeAttachment)
                    },
                    isRecording = isRecording
                )
            }
        }
    }

    // Attachment Modal Sheet Dialog
    if (showAttachmentModal) {
        Dialog(onDismissRequest = { showAttachmentModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Attach Learning Material",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    AttachmentOptionRow(
                        icon = Icons.Default.PhotoCamera,
                        title = "Take Photo of Textbook / Notes",
                        subtitle = "Scan question or diagram directly",
                        onClick = {
                            activeAttachment = AttachmentInfo(
                                name = "scanned_textbook_page.jpg",
                                type = "IMAGE",
                                contextDescription = "Photo of a physics problem regarding kinetic energy and velocity formulas."
                            )
                            showAttachmentModal = false
                        }
                    )

                    AttachmentOptionRow(
                        icon = Icons.Default.Description,
                        title = "Upload Class Handout / PDF",
                        subtitle = "Attach PDF notes or assignment file",
                        onClick = {
                            activeAttachment = AttachmentInfo(
                                name = "biology_chapter_4_notes.pdf",
                                type = "PDF",
                                contextDescription = "PDF document containing summary of cell division, mitosis stages, and chromosome duplication."
                            )
                            showAttachmentModal = false
                        }
                    )

                    AttachmentOptionRow(
                        icon = Icons.Default.Image,
                        title = "Pick Diagram from Gallery",
                        subtitle = "Upload an existing image or diagram",
                        onClick = {
                            activeAttachment = AttachmentInfo(
                                name = "force_diagram_figure.png",
                                type = "IMAGE",
                                contextDescription = "Diagram showing normal force, friction, and gravitational acceleration vector arrows on an inclined plane."
                            )
                            showAttachmentModal = false
                        }
                    )
                }
            }
        }
    }

    // Full Screen Image Preview Modal
    previewImageUrl?.let { url ->
        Dialog(onDismissRequest = { previewImageUrl = null }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { previewImageUrl = null },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Educational Diagram",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            IconButton(onClick = { previewImageUrl = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = url,
                            contentDescription = "Full Educational Diagram",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 450.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEF2FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = VioletAccent, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
fun MessageBubbleItem(
    message: DoubtChatMessage,
    onRetry: () -> Unit,
    onExplainWithPicture: () -> Unit,
    onImageClick: (String) -> Unit
) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        when {
            // Thinking State
            message.isThinking -> {
                ThinkingBubble()
            }

            // Error State with Retry
            message.isError -> {
                ErrorBubble(
                    errorMessage = message.errorMessage ?: "Failed to generate AI response.",
                    onRetry = onRetry
                )
            }

            // User Message
            message.isUser -> {
                Column(horizontalAlignment = Alignment.End) {
                    message.attachmentName?.let { attName ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEEF2FF),
                            border = BorderStroke(1.dp, Color(0xFFC7D2FE)),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Attachment, contentDescription = null, tint = VioletAccent, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = attName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VioletAccent)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                brush = PrimaryGradient,
                                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = message.text,
                            color = Color.White,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // AI Teacher Response
            else -> {
                Surface(
                    shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth(0.92f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI MCQ Teacher",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = VioletAccent
                            )
                        }

                        // Formatted Text
                        Text(
                            text = parseMarkdownToAnnotatedString(message.text),
                            color = Color(0xFF1E293B),
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )

                        // Generated Image Diagram Display
                        message.imageUrl?.let { url ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                color = Color(0xFFF8FAFC),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onImageClick(url) }
                            ) {
                                Column {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = message.imageCaption ?: "Educational Diagram",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Row(
                                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.SmartDisplay, contentDescription = null, tint = VioletAccent, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = message.imageCaption ?: "Diagram",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF334155),
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                        }
                                        TextButton(
                                            onClick = onExplainWithPicture,
                                            contentPadding = PaddingValues(0.dp),
                                            modifier = Modifier.height(24.dp)
                                        ) {
                                            Text("Regenerate", fontSize = 12.sp, color = VioletAccent)
                                        }
                                    }
                                }
                            }
                        }

                        // Loading diagram state
                        if (message.isGeneratingImage) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = VioletAccent
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Creating educational diagram…",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }
                        }

                        // Image Error State
                        message.imageError?.let { errText ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFEF2F2),
                                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = errText,
                                        fontSize = 12.sp,
                                        color = Color(0xFFB91C1C),
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(onClick = onExplainWithPicture) {
                                        Text("Retry Diagram", fontSize = 12.sp, color = VioletAccent)
                                    }
                                }
                            }
                        }

                        // Bottom Action Row (Explain with a picture)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = onExplainWithPicture,
                                shape = RoundedCornerShape(100.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier.testTag("explain_with_picture_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🖼️", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Explain with a picture",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF334155)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThinkingBubble() {
    val transition = rememberInfiniteTransition(label = "thinking_dot")
    val alpha1 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "dot1"
    )
    val alpha2 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 200, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "dot2"
    )
    val alpha3 by transition.animateFloat(
        initialValue = 0.3f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 400, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "dot3"
    )

    Surface(
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AI is thinking",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = VioletAccent
            )
            Spacer(modifier = Modifier.width(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(VioletAccent.copy(alpha = alpha1))
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(VioletAccent.copy(alpha = alpha2))
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(VioletAccent.copy(alpha = alpha3))
                )
            }
        }
    }
}

@Composable
fun ErrorBubble(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFEF2F2),
        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Response Failed",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF991B1B)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = errorMessage,
                fontSize = 13.sp,
                color = Color(0xFF7F1D1D)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                onClick = onRetry,
                shape = RoundedCornerShape(100.dp),
                color = Color(0xFFDC2626)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Retry",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Robust markdown bold text parser for Jetpack Compose Text.
 */
fun parseMarkdownToAnnotatedString(text: String): AnnotatedString {
    return buildAnnotatedString {
        val regex = Regex("\\*\\*(.*?)\\*\\*")
        var lastIndex = 0
        for (match in regex.findAll(text)) {
            val start = match.range.first
            val end = match.range.last + 1
            if (start > lastIndex) {
                append(text.substring(lastIndex, start))
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))) {
                append(match.groupValues[1])
            }
            lastIndex = end
        }
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
}
