package io.livekit.android.example.voiceassistant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.livekit.android.example.voiceassistant.ui.theme.FridayAITheme
import io.livekit.android.example.voiceassistant.ui.theme.FridayBlue
import io.livekit.android.example.voiceassistant.ui.theme.FridayDark
import io.livekit.android.example.voiceassistant.ui.theme.FridayGlow
import kotlinx.coroutines.delay
import kotlin.math.sin

class VoiceAuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FridayAITheme {
                VoiceAuthScreen(
                    onAuthSuccess = { userName ->
                        // Update last login and proceed to main app
                        val userManager = UserProfileManager(this@VoiceAuthActivity)
                        userManager.updateLastLogin()
                        
                        val intent = Intent(this@VoiceAuthActivity, MainActivity::class.java)
                        intent.putExtra("user_name", userName)
                        startActivity(intent)
                        finish()
                    },
                    onAuthFailed = {
                        // Handle authentication failure
                        FridayErrorHandler.handleError(
                            this@VoiceAuthActivity,
                            FridayErrorHandler.FridayError.TokenError
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceAuthScreen(
    onAuthSuccess: (String) -> Unit,
    onAuthFailed: () -> Unit
) {
    val context = LocalContext.current
    val userManager = UserProfileManager(context)
    val userName = userManager.getUserName()
    
    var isListening by remember { mutableStateOf(false) }
    var authState by remember { mutableStateOf(AuthState.WAITING) }
    var confidenceLevel by remember { mutableStateOf(0.0f) }
    
    LaunchedEffect(isListening) {
        if (isListening) {
            // Simulate voice authentication process
            authState = AuthState.LISTENING
            delay(2000) // Listen for 2 seconds
            
            authState = AuthState.PROCESSING
            delay(1500) // Process for 1.5 seconds
            
            // Simulate voice verification (in production, use actual voice data)
            val mockVoiceData = "mock_voice_data".toByteArray()
            confidenceLevel = userManager.verifyVoice(mockVoiceData)
            
            if (confidenceLevel > 0.75f) {
                authState = AuthState.SUCCESS
                delay(1000)
                onAuthSuccess(userName)
            } else {
                authState = AuthState.FAILED
                delay(2000)
                authState = AuthState.WAITING
                isListening = false
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(FridayDark, FridayBlue.copy(alpha = 0.2f))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Friday AI Logo
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = FridayBlue.copy(alpha = 0.2f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "F",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = FridayBlue
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Welcome Back Message
            Text(
                text = "Welcome back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = FridayBlue,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Hello, $userName",
                fontSize = 18.sp,
                color = FridayBlue.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Voice Authentication Visualizer
            VoiceAuthVisualizer(
                authState = authState,
                confidenceLevel = confidenceLevel
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Authentication Status
            AuthStatusText(authState = authState, confidenceLevel = confidenceLevel)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Voice Authentication Button
            Button(
                onClick = {
                    if (!isListening && authState != AuthState.SUCCESS) {
                        isListening = true
                        confidenceLevel = 0.0f
                    }
                },
                enabled = !isListening && authState != AuthState.SUCCESS,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (authState) {
                        AuthState.SUCCESS -> FridayGlow
                        AuthState.FAILED -> Color.Red.copy(alpha = 0.7f)
                        else -> FridayBlue
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = when (authState) {
                        AuthState.WAITING -> "Authenticate with Voice"
                        AuthState.LISTENING -> "Listening..."
                        AuthState.PROCESSING -> "Processing..."
                        AuthState.SUCCESS -> "Authentication Successful"
                        AuthState.FAILED -> "Try Again"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FridayDark
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Skip Button (for development)
            TextButton(
                onClick = { onAuthSuccess(userName) }
            ) {
                Text(
                    text = "Skip Authentication (Dev Mode)",
                    color = FridayBlue.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Developer Credit
            Text(
                text = "Developed by Paras Gusain",
                fontSize = 12.sp,
                color = FridayBlue.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

enum class AuthState {
    WAITING,
    LISTENING,
    PROCESSING,
    SUCCESS,
    FAILED
}

@Composable
fun VoiceAuthVisualizer(
    authState: AuthState,
    confidenceLevel: Float
) {
    val animatedRadius by animateFloatAsState(
        targetValue = when (authState) {
            AuthState.LISTENING -> 100f
            AuthState.PROCESSING -> 80f
            AuthState.SUCCESS -> 120f
            AuthState.FAILED -> 60f
            else -> 70f
        },
        animationSpec = tween(durationMillis = 500)
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = when (authState) {
            AuthState.LISTENING -> 1.0f
            AuthState.PROCESSING -> 0.8f
            AuthState.SUCCESS -> 1.0f
            AuthState.FAILED -> 0.6f
            else -> 0.7f
        },
        animationSpec = tween(durationMillis = 300)
    )
    
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawVoiceAuthWaves(authState, animatedRadius, animatedAlpha, confidenceLevel)
        }
        
        // Central Icon
        Text(
            text = when (authState) {
                AuthState.LISTENING -> "🎤"
                AuthState.PROCESSING -> "🔄"
                AuthState.SUCCESS -> "✅"
                AuthState.FAILED -> "❌"
                else -> "🔒"
            },
            fontSize = 48.sp,
            modifier = Modifier.offset(y = (-4).dp)
        )
        
        // Confidence Level Display
        if (authState == AuthState.PROCESSING || authState == AuthState.SUCCESS) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = 40.dp)
            ) {
                Text(
                    text = "${(confidenceLevel * 100).toInt()}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FridayBlue
                )
                Text(
                    text = "Confidence",
                    fontSize = 12.sp,
                    color = FridayBlue.copy(alpha = 0.7f)
                )
            }
        }
    }
}

fun DrawScope.drawVoiceAuthWaves(
    authState: AuthState,
    radius: Float,
    alpha: Float,
    confidenceLevel: Float
) {
    val color = when (authState) {
        AuthState.LISTENING -> Color(0xFF00E5FF)
        AuthState.PROCESSING -> Color(0xFF40C4FF)
        AuthState.SUCCESS -> Color(0xFF1DE9B6)
        AuthState.FAILED -> Color(0xFFFF5252)
        else -> Color(0xFF00E5FF)
    }
    
    // Draw multiple concentric circles for wave effect
    for (i in 1..4) {
        val waveRadius = radius * (i * 0.3f + confidenceLevel * 0.5f)
        val waveAlpha = (alpha / i) * (1f + confidenceLevel * 0.5f)
        
        drawCircle(
            color = color.copy(alpha = waveAlpha),
            radius = waveRadius,
            center = center
        )
    }
    
    // Add pulsing effect for listening state
    if (authState == AuthState.LISTENING) {
        for (i in 1..2) {
            val pulseRadius = radius * (1.2f + sin(System.currentTimeMillis() * 0.01f * i) * 0.1f)
            drawCircle(
                color = color.copy(alpha = 0.2f),
                radius = pulseRadius,
                center = center
            )
        }
    }
}

@Composable
fun AuthStatusText(
    authState: AuthState,
    confidenceLevel: Float
) {
    val statusText = when (authState) {
        AuthState.WAITING -> "Say \"Hello Friday\" to authenticate"
        AuthState.LISTENING -> "Listening to your voice..."
        AuthState.PROCESSING -> "Analyzing voice pattern..."
        AuthState.SUCCESS -> "Voice recognized! Welcome back."
        AuthState.FAILED -> "Voice not recognized. Please try again."
    }
    
    val statusColor = when (authState) {
        AuthState.SUCCESS -> FridayGlow
        AuthState.FAILED -> Color.Red.copy(alpha = 0.8f)
        else -> FridayBlue
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = statusText,
            fontSize = 16.sp,
            color = statusColor,
            textAlign = TextAlign.Center,
            fontWeight = if (authState == AuthState.SUCCESS || authState == AuthState.FAILED) 
                FontWeight.SemiBold else FontWeight.Normal
        )
        
        if (authState == AuthState.SUCCESS && confidenceLevel > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = confidenceLevel,
                modifier = Modifier
                    .width(150.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = FridayGlow,
                trackColor = FridayGlow.copy(alpha = 0.3f)
            )
        }
        
        if (authState == AuthState.FAILED) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.1f)
                ),
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "Make sure you're in a quiet environment\nand speak clearly",
                    fontSize = 14.sp,
                    color = Color.Red.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}