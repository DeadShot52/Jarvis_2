package io.livekit.android.example.voiceassistant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.livekit.android.example.voiceassistant.ui.theme.FridayAITheme
import io.livekit.android.example.voiceassistant.ui.theme.FridayBlue
import io.livekit.android.example.voiceassistant.ui.theme.FridayDark
import io.livekit.android.example.voiceassistant.ui.theme.FridayGlow

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is already setup
        val userManager = UserProfileManager(this)
        if (userManager.isUserSetup()) {
            // Go to voice authentication
            startActivity(Intent(this, VoiceAuthActivity::class.java))
            finish()
            return
        }
        
        setContent {
            FridayAITheme {
                WelcomeScreen(
                    onGetStarted = {
                        startActivity(Intent(this@WelcomeActivity, UserSetupActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
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
            // Friday AI Logo/Icon
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp)),
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
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = FridayBlue
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Welcome Title
            Text(
                text = "Welcome to Friday AI",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = FridayBlue,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle
            Text(
                text = "Your personal AI assistant,\njust like Tony Stark's Friday",
                fontSize = 18.sp,
                color = FridayBlue.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Features List
            FeatureItem("🎭", "Intelligent Personality", "Witty responses with humor and context")
            Spacer(modifier = Modifier.height(16.dp))
            FeatureItem("🔒", "Voice Biometrics", "Secure authentication using your voice")
            Spacer(modifier = Modifier.height(16.dp))
            FeatureItem("📱", "System Control", "Control apps, send messages, make calls")
            Spacer(modifier = Modifier.height(16.dp))
            FeatureItem("🤖", "Smart Automation", "Create custom routines and shortcuts")
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Get Started Button
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FridayBlue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = FridayDark
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Developer Credit
            Text(
                text = "Developed by Paras Gusain",
                fontSize = 14.sp,
                color = FridayBlue.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FeatureItem(icon: String, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 24.sp,
            modifier = Modifier.padding(end = 16.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = FridayBlue
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = FridayBlue.copy(alpha = 0.7f)
            )
        }
    }
}