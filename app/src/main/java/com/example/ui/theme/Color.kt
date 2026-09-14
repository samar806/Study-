package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Canvas & Backgrounds
val NavyDark = Color(0xFF0B1224)
val NavyDeep = Color(0xFF111B34)
val NavyCard = Color(0xFF1E293B)
val LightCanvas = Color(0xFFF1F5F9)

// Card Surfaces
val CardWhite = Color(0xFFFFFFFF)
val CardOffWhite = Color(0xFFF7F8FC)
val CardBorderLight = Color(0xFFE2E8F0)
val CardBorderDark = Color(0xFF24324F)

// Primary Accent Gradient (#7C5CFC -> #4C7EFB)
val VioletAccent = Color(0xFF7C5CFC)
val BlueAccent = Color(0xFF4C7EFB)
val VioletGlow = Color(0x407C5CFC)

val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(VioletAccent, BlueAccent)
)

val PrimaryGradientVertical = Brush.verticalGradient(
    colors = listOf(VioletAccent, BlueAccent)
)

val NavyBackgroundGradient = Brush.verticalGradient(
    colors = listOf(NavyDark, NavyDeep)
)

val LightBackgroundGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFF8FAFC), Color(0xFFEEF2F6))
)

// Hero Card Gradient with angle feel
val HeroCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF7C5CFC), Color(0xFF5A72FA), Color(0xFF4C7EFB))
)

// Semantic Tokens
val SuccessGreen = Color(0xFF22C55E)
val ErrorRed = Color(0xFFEF4444)
val WarningOrange = Color(0xFFF59E0B)
val NeutralGray = Color(0xFF94A3B8)
val SlateGray = Color(0xFF64748B)

// Text Colors
val TextOnNavyPrimary = Color(0xFFFFFFFF)
val TextOnNavySecondary = Color(0xFF94A3B8)
val TextOnWhitePrimary = Color(0xFF0F172A)
val TextOnWhiteSecondary = Color(0xFF64748B)
val TextMuted = Color(0xFF94A3B8)

// Pill & Input colors
val PillInactiveBg = Color(0xFFF1F5F9)
val PillInactiveText = Color(0xFF475569)
val InputBgLight = Color(0xFFF8FAFC)
val InputBorderLight = Color(0xFFE2E8F0)
val InputBorderFocused = Color(0xFF7C5CFC)

// Soft icon badges
val PromptBadgeBg = Color(0xFFFDF2F8)
val ImageBadgeBg = Color(0xFFF0FDF4)
val PdfBadgeBg = Color(0xFFEFF6FF)
