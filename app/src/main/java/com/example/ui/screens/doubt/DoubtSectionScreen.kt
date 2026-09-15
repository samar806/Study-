package com.example.ui.screens.doubt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ChatInputBar
import com.example.ui.navigation.RepeatingHeader
import com.example.ui.theme.PrimaryGradient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean
)

@Composable
fun DoubtSectionScreen(
    onBack: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var chatInput by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "1",
                text = "Hi! I am your AI Doubt Solver. What concept are you struggling with today?",
                isUser = false
            )
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                RepeatingHeader(
                    title = "Doubt Section",
                    subtitle = "Ask any question and get instant AI help",
                    onBackClick = onBack,
                    onOpenDrawer = onOpenDrawer
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message)
                }
            }
        }
        
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
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                ChatInputBar(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    placeholder = "Ask your doubt here...",
                    hasAttachment = false,
                    onAttachClick = { },
                    onMicClick = { isRecording = !isRecording },
                    onSendClick = {
                        if (chatInput.isNotBlank()) {
                            val userText = chatInput
                            messages.add(
                                ChatMessage(
                                    id = System.currentTimeMillis().toString(),
                                    text = userText,
                                    isUser = true
                                )
                            )
                            chatInput = ""
                            
                            // Simulate AI Response
                            coroutineScope.launch {
                                delay(1000)
                                messages.add(
                                    ChatMessage(
                                        id = (System.currentTimeMillis() + 1).toString(),
                                        text = "That's a great question! Let me explain the concepts behind it.",
                                        isUser = false
                                    )
                                )
                            }
                        }
                    },
                    isRecording = isRecording
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (message.isUser) Color.Transparent else Color.White
    val textColor = if (message.isUser) Color.White else Color(0xFF1E293B)
    
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        if (message.isUser) {
            Box(
                modifier = Modifier
                    .background(
                        brush = PrimaryGradient,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message.text,
                    color = textColor,
                    fontSize = 15.sp
                )
            }
        } else {
            Surface(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
                color = backgroundColor,
                shadowElevation = 1.dp
            ) {
                Text(
                    text = message.text,
                    color = textColor,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}
