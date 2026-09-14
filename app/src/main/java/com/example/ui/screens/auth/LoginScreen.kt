package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CleanInputField
import com.example.ui.components.FloatingCard
import com.example.ui.components.GoogleSignInButton
import com.example.ui.components.GradientButton
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CardWhite
import com.example.ui.theme.NavyBackgroundGradient
import com.example.ui.theme.NavyDark
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.SlateGray
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VioletAccent
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (email: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("priti@example.com") }
    var password by remember { mutableStateOf("••••••••••") }
    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackgroundGradient)
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient ring decoration in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width * 0.5f, size.height * 0.35f)
            drawCircle(
                color = Color(0x0AFFFFFF),
                radius = size.width * 0.75f,
                center = center,
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = Color(0x08FFFFFF),
                radius = size.width * 1.1f,
                center = center,
                style = Stroke(width = 1.5f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Centered Floating Card
            FloatingCard(
                modifier = Modifier
                    .widthIn(max = 440.dp)
                    .fillMaxWidth(),
                cornerRadius = 28.dp,
                backgroundColor = CardWhite,
                borderColor = CardBorderLight,
                elevation = 14.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Circular Logo Badge (Graduation cap mark)
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = CircleShape,
                                ambientColor = VioletAccent.copy(alpha = 0.5f),
                                spotColor = VioletAccent.copy(alpha = 0.5f)
                            )
                            .clip(CircleShape)
                            .background(PrimaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Graduation Cap Logo",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Title
                    Text(
                        text = "AI MCQ Teacher",
                        color = TextOnWhitePrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Italic Tagline
                    Text(
                        text = "Generate. Practice. Understand. Improve.",
                        color = TextOnWhiteSecondary,
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // Full-width white "Continue with Google" button
                    GoogleSignInButton(
                        onClick = {
                            onLoginSuccess(email)
                        },
                        text = "Continue with Google"
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // "OR" divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFE2E8F0),
                            thickness = 1.dp
                        )
                        Text(
                            text = "OR",
                            color = SlateGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFE2E8F0),
                            thickness = 1.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Rounded Email Field (Mail Icon)
                    CleanInputField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "name@example.com",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        testTag = "login_email_input"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Rounded Password Field (Lock icon + Eye toggle)
                    CleanInputField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Enter your password",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        testTag = "login_password_input"
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Full-width gradient "Login" / "Create Account" button
                    GradientButton(
                        text = if (isSignUpMode) "Create Account" else "Login",
                        onClick = {
                            onLoginSuccess(email)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "login_submit_button"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Centered "Forgot Password?" link
                    Text(
                        text = "Forgot Password?",
                        color = VioletAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Password reset link sent to $email")
                                }
                            }
                            .padding(4.dp)
                            .testTag("forgot_password_link")
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Bottom row: "Don't have an account? Create Account"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUpMode) "Already have an account? " else "Don't have an account? ",
                            color = TextOnWhiteSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isSignUpMode) "Login" else "Create Account",
                            color = VioletAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    isSignUpMode = !isSignUpMode
                                }
                                .padding(vertical = 4.dp)
                                .testTag("toggle_signup_link")
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
