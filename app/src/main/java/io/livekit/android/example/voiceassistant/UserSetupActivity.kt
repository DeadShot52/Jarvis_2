package io.livekit.android.example.voiceassistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import io.livekit.android.example.voiceassistant.ui.theme.FridayAITheme
import io.livekit.android.example.voiceassistant.ui.theme.FridayBlue
import io.livekit.android.example.voiceassistant.ui.theme.FridayDark
import io.livekit.android.example.voiceassistant.ui.theme.FridayGlow
import kotlinx.coroutines.delay
import java.io.File
import java.io.IOException

class UserSetupActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission results
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FridayAITheme {
                UserSetupFlow { profile ->
                    // Save user profile and proceed to main app
                    val userManager = UserProfileManager(this@UserSetupActivity)
                    userManager.saveUserProfile(profile.copy(setupComplete = true))
                    
                    // Go to main activity
                    startActivity(Intent(this@UserSetupActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
    
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
        
        requestPermissionLauncher.launch(permissions)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSetupFlow(onSetupComplete: (UserProfileManager.UserProfile) -> Unit) {
    var currentStep by remember { mutableStateOf(0) }
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var voiceRecorded by remember { mutableStateOf(false) }
    var personalitySettings by remember { mutableStateOf(UserProfileManager.PersonalitySettings()) }
    var preferences by remember { mutableStateOf(UserProfileManager.UserPreferences()) }
    
    val steps = listOf("Personal Info", "Voice Setup", "Personality", "Preferences", "Complete")
    
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
                .padding(24.dp)
        ) {
            // Progress Indicator
            SetupProgressIndicator(
                currentStep = currentStep,
                totalSteps = steps.size,
                stepNames = steps
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Step Content
            when (currentStep) {
                0 -> PersonalInfoStep(
                    name = userName,
                    email = userEmail,
                    onNameChange = { userName = it },
                    onEmailChange = { userEmail = it },
                    onNext = { if (userName.isNotBlank()) currentStep++ }
                )
                1 -> VoiceSetupStep(
                    userName = userName,
                    onVoiceRecorded = { voiceRecorded = true },
                    onNext = { if (voiceRecorded) currentStep++ }
                )
                2 -> PersonalitySetupStep(
                    settings = personalitySettings,
                    onSettingsChange = { personalitySettings = it },
                    onNext = { currentStep++ }
                )
                3 -> PreferencesSetupStep(
                    preferences = preferences,
                    onPreferencesChange = { preferences = it },
                    onNext = { currentStep++ }
                )
                4 -> SetupCompleteStep(
                    userName = userName,
                    onComplete = {
                        val profile = UserProfileManager.UserProfile(
                            name = userName,
                            email = userEmail,
                            personalitySettings = personalitySettings,
                            preferences = preferences,
                            setupComplete = true
                        )
                        onSetupComplete(profile)
                    }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 0) {
                    TextButton(
                        onClick = { currentStep-- }
                    ) {
                        Text("Back", color = FridayBlue)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
            }
        }
    }
}

@Composable
fun SetupProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    stepNames: List<String>
) {
    Column {
        // Progress Bar
        LinearProgressIndicator(
            progress = (currentStep + 1).toFloat() / totalSteps,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = FridayBlue,
            trackColor = FridayBlue.copy(alpha = 0.3f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Step Name
        Text(
            text = "Step ${currentStep + 1} of $totalSteps: ${stepNames[currentStep]}",
            fontSize = 16.sp,
            color = FridayBlue,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoStep(
    name: String,
    email: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Personal Information",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = FridayBlue,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Let's get to know you better!",
            fontSize = 16.sp,
            color = FridayBlue.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Name Field
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FridayBlue,
                focusedLabelColor = FridayBlue,
                cursorColor = FridayBlue
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Email Field (Optional)
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FridayBlue,
                focusedLabelColor = FridayBlue,
                cursorColor = FridayBlue
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (name.isNotBlank()) onNext() }
            )
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Continue Button
        Button(
            onClick = onNext,
            enabled = name.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FridayBlue
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FridayDark
            )
        }
    }
}

@Composable
fun VoiceSetupStep(
    userName: String,
    onVoiceRecorded: () -> Unit,
    onNext: () -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    var recordingComplete by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Voice Recognition Setup",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = FridayBlue,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Let Friday learn your voice for secure authentication",
            fontSize = 16.sp,
            color = FridayBlue.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Voice Recording Visualizer
        VoiceRecordingVisualizer(isRecording = isRecording)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = FridayBlue.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Please say clearly:",
                    fontSize = 16.sp,
                    color = FridayBlue,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"Hello Friday, this is $userName\"",
                    fontSize = 18.sp,
                    color = FridayBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Record Button
        Button(
            onClick = {
                if (!isRecording && !recordingComplete) {
                    isRecording = true
                    // Simulate recording for 3 seconds
                    // In production, implement actual voice recording
                } else if (recordingComplete) {
                    onNext()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (recordingComplete) FridayGlow else FridayBlue
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = when {
                    isRecording -> "Recording..."
                    recordingComplete -> "Continue"
                    else -> "Start Recording"
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FridayDark
            )
        }
        
        // Simulate recording completion
        LaunchedEffect(isRecording) {
            if (isRecording) {
                delay(3000) // 3 second recording
                isRecording = false
                recordingComplete = true
                onVoiceRecorded()
            }
        }
    }
}

@Composable
fun VoiceRecordingVisualizer(isRecording: Boolean) {
    val animatedRadius by animateFloatAsState(
        targetValue = if (isRecording) 80f else 60f
    )
    
    Box(
        modifier = Modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawVoiceWave(isRecording, animatedRadius)
        }
        
        // Microphone Icon
        Text(
            text = "🎤",
            fontSize = 48.sp
        )
    }
}

fun DrawScope.drawVoiceWave(isRecording: Boolean, radius: Float) {
    if (isRecording) {
        for (i in 1..3) {
            drawCircle(
                color = Color(0xFF00E5FF).copy(alpha = 0.3f / i),
                radius = radius * i,
                center = center
            )
        }
    }
}

@Composable
fun PersonalitySetupStep(
    settings: UserProfileManager.PersonalitySettings,
    onSettingsChange: (UserProfileManager.PersonalitySettings) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Customize Friday's Personality",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = FridayBlue,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Make Friday uniquely yours!",
            fontSize = 16.sp,
            color = FridayBlue.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Humor Level
        PersonalitySlider(
            title = "Humor Level",
            description = "How funny should Friday be?",
            value = settings.humorLevel,
            onValueChange = { onSettingsChange(settings.copy(humorLevel = it)) },
            lowLabel = "Serious",
            highLabel = "Very Funny"
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Formality Level
        PersonalitySlider(
            title = "Formality Level",
            description = "How formal should Friday speak?",
            value = settings.formalityLevel,
            onValueChange = { onSettingsChange(settings.copy(formalityLevel = it)) },
            lowLabel = "Casual",
            highLabel = "Formal"
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Proactiveness
        PersonalitySlider(
            title = "Proactiveness",
            description = "How proactive should Friday be?",
            value = settings.proactiveness,
            onValueChange = { onSettingsChange(settings.copy(proactiveness = it)) },
            lowLabel = "Reactive",
            highLabel = "Very Proactive"
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Memory Setting
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Remember Conversations",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FridayBlue
                )
                Text(
                    text = "Let Friday remember your past conversations",
                    fontSize = 14.sp,
                    color = FridayBlue.copy(alpha = 0.7f)
                )
            }
            Switch(
                checked = settings.memoryRetention,
                onCheckedChange = { onSettingsChange(settings.copy(memoryRetention = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = FridayBlue,
                    checkedTrackColor = FridayBlue.copy(alpha = 0.5f)
                )
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Continue Button
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FridayBlue
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FridayDark
            )
        }
    }
}

@Composable
fun PersonalitySlider(
    title: String,
    description: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    lowLabel: String,
    highLabel: String
) {
    Column {
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = FridayBlue,
                activeTrackColor = FridayBlue,
                inactiveTrackColor = FridayBlue.copy(alpha = 0.3f)
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = lowLabel,
                fontSize = 12.sp,
                color = FridayBlue.copy(alpha = 0.6f)
            )
            Text(
                text = highLabel,
                fontSize = 12.sp,
                color = FridayBlue.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun PreferencesSetupStep(
    preferences: UserProfileManager.UserPreferences,
    onPreferencesChange: (UserProfileManager.UserPreferences) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "App Preferences",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = FridayBlue,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Configure Friday's capabilities",
            fontSize = 16.sp,
            color = FridayBlue.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Cloud Sync
        PreferenceToggle(
            title = "Cloud Sync",
            description = "Sync your data across devices",
            checked = preferences.cloudSync,
            onCheckedChange = { onPreferencesChange(preferences.copy(cloudSync = it)) }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Background Operation
        PreferenceToggle(
            title = "Background Operation",
            description = "Keep Friday running in background",
            checked = preferences.backgroundOperation,
            onCheckedChange = { onPreferencesChange(preferences.copy(backgroundOperation = it)) }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Notifications
        PreferenceToggle(
            title = "Voice Alerts",
            description = "Receive voice notifications",
            checked = preferences.notifications.voiceAlerts,
            onCheckedChange = { 
                val updatedNotifications = preferences.notifications.copy(voiceAlerts = it)
                onPreferencesChange(preferences.copy(notifications = updatedNotifications))
            }
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Continue Button
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FridayBlue
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FridayDark
            )
        }
    }
}

@Composable
fun PreferenceToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = FridayBlue,
                checkedTrackColor = FridayBlue.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun SetupCompleteStep(
    userName: String,
    onComplete: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Success Icon
        Text(
            text = "🎉",
            fontSize = 64.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Setup Complete!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = FridayBlue,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Welcome to Friday AI, $userName!",
            fontSize = 18.sp,
            color = FridayBlue.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = FridayBlue.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Your Friday AI is ready!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FridayBlue
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "• Voice authentication enabled\n• Personality customized\n• Smart features activated\n• Ready for system-wide assistance",
                    fontSize = 14.sp,
                    color = FridayBlue.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Complete Button
        Button(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FridayGlow
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Start Using Friday AI",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FridayDark
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Developed by Paras Gusain",
            fontSize = 14.sp,
            color = FridayBlue.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}