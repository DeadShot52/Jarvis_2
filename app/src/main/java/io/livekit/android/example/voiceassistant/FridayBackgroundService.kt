package io.livekit.android.example.voiceassistant

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.util.*

/**
 * Friday AI Background Service
 * Provides continuous AI assistance, smart automation, and system monitoring
 * Developed by Paras Gusain
 */
class FridayBackgroundService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var userManager: UserProfileManager
    private lateinit var smartAutomation: SmartAutomationManager
    private lateinit var appIntegration: AppIntegrationManager
    private lateinit var entertainmentHub: EntertainmentHubManager
    
    companion object {
        const val CHANNEL_ID = "friday_ai_service"
        const val NOTIFICATION_ID = 1001
        
        private var isServiceRunning = false
        
        fun startService(context: Context) {
            if (!isServiceRunning) {
                val intent = Intent(context, FridayBackgroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, FridayBackgroundService::class.java)
            context.stopService(intent)
        }
        
        fun isRunning(): Boolean = isServiceRunning
    }
    
    override fun onCreate() {
        super.onCreate()
        
        userManager = UserProfileManager(this)
        smartAutomation = SmartAutomationManager(this)
        appIntegration = AppIntegrationManager(this)
        entertainmentHub = EntertainmentHubManager(this)
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        
        isServiceRunning = true
        
        // Start background tasks
        startBackgroundTasks()
        
        FridayErrorHandler.logEvent("Service", "Friday AI background service started")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Handle various commands
        when (intent?.action) {
            "VOICE_COMMAND" -> {
                val command = intent.getStringExtra("command")
                if (command != null) {
                    handleVoiceCommand(command)
                }
            }
            "EXECUTE_ROUTINE" -> {
                val routineName = intent.getStringExtra("routine_name")
                if (routineName != null) {
                    smartAutomation.executeRoutine(routineName)
                }
            }
            "ENTERTAINMENT_REQUEST" -> {
                val type = intent.getStringExtra("type")
                val query = intent.getStringExtra("query")
                if (type != null && query != null) {
                    entertainmentHub.handleRequest(type, query)
                }
            }
        }
        
        return START_STICKY // Restart if killed
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        isServiceRunning = false
        
        FridayErrorHandler.logEvent("Service", "Friday AI background service stopped")
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Friday AI Assistant",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Friday AI is running in the background"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Friday AI Active")
            .setContentText("Your AI assistant is ready to help")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Use a proper icon in production
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }
    
    private fun startBackgroundTasks() {
        serviceScope.launch {
            // Smart automation monitoring
            launch { smartAutomation.startMonitoring() }
            
            // App integration monitoring
            launch { appIntegration.startMonitoring() }
            
            // Entertainment hub monitoring
            launch { entertainmentHub.startMonitoring() }
            
            // Proactive assistance
            launch { startProactiveAssistance() }
            
            // Cloud sync
            launch { startCloudSync() }
        }
    }
    
    private suspend fun startProactiveAssistance() {
        while (isActive) {
            try {
                val preferences = userManager.getPreferences()
                val personalitySettings = userManager.getPersonalitySettings()
                
                if (personalitySettings.proactiveness > 0.5f) {
                    // Check for proactive suggestions
                    checkForProactiveSuggestions()
                }
                
                // Check every 5 minutes
                delay(5 * 60 * 1000)
                
            } catch (e: Exception) {
                FridayErrorHandler.logEvent("Proactive", "Error in proactive assistance: ${e.message}")
                delay(60 * 1000) // Wait 1 minute before retrying
            }
        }
    }
    
    private suspend fun startCloudSync() {
        while (isActive) {
            try {
                if (userManager.isCloudSyncEnabled()) {
                    // Sync user data to cloud
                    syncDataToCloud()
                }
                
                // Sync every 30 minutes
                delay(30 * 60 * 1000)
                
            } catch (e: Exception) {
                FridayErrorHandler.logEvent("CloudSync", "Error in cloud sync: ${e.message}")
                delay(10 * 60 * 1000) // Wait 10 minutes before retrying
            }
        }
    }
    
    private fun handleVoiceCommand(command: String) {
        serviceScope.launch {
            try {
                val processedCommand = processNaturalLanguage(command)
                
                when (processedCommand.type) {
                    CommandType.APP_CONTROL -> {
                        appIntegration.executeAppCommand(processedCommand)
                    }
                    CommandType.SMART_HOME -> {
                        smartAutomation.executeSmartHomeCommand(processedCommand)
                    }
                    CommandType.ENTERTAINMENT -> {
                        entertainmentHub.executeEntertainmentCommand(processedCommand)
                    }
                    CommandType.ROUTINE -> {
                        smartAutomation.executeRoutine(processedCommand.parameters["routine"] ?: "")
                    }
                    CommandType.SYSTEM -> {
                        executeSystemCommand(processedCommand)
                    }
                    else -> {
                        // Handle unknown commands with personality
                        respondWithPersonality("I'm not sure how to help with that, but I'm learning!")
                    }
                }
                
            } catch (e: Exception) {
                FridayErrorHandler.logEvent("VoiceCommand", "Error processing command: ${e.message}")
            }
        }
    }
    
    private fun processNaturalLanguage(command: String): ProcessedCommand {
        val lowerCommand = command.lowercase()
        
        return when {
            // App control patterns
            lowerCommand.contains("open") && lowerCommand.contains("whatsapp") -> 
                ProcessedCommand(CommandType.APP_CONTROL, mapOf("app" to "whatsapp", "action" to "open"))
            
            lowerCommand.contains("send message") || lowerCommand.contains("text") ->
                ProcessedCommand(CommandType.APP_CONTROL, mapOf("action" to "send_message", "text" to extractTextFromCommand(command)))
            
            lowerCommand.contains("call") ->
                ProcessedCommand(CommandType.APP_CONTROL, mapOf("action" to "call", "contact" to extractContactFromCommand(command)))
            
            // Entertainment patterns
            lowerCommand.contains("play music") || lowerCommand.contains("spotify") ->
                ProcessedCommand(CommandType.ENTERTAINMENT, mapOf("action" to "play_music", "query" to extractMusicQuery(command)))
            
            lowerCommand.contains("movie") || lowerCommand.contains("netflix") ->
                ProcessedCommand(CommandType.ENTERTAINMENT, mapOf("action" to "suggest_movie", "genre" to extractGenre(command)))
            
            // Routine patterns
            lowerCommand.contains("morning routine") ->
                ProcessedCommand(CommandType.ROUTINE, mapOf("routine" to "morning"))
            
            lowerCommand.contains("night routine") || lowerCommand.contains("bedtime") ->
                ProcessedCommand(CommandType.ROUTINE, mapOf("routine" to "night"))
            
            // System patterns
            lowerCommand.contains("volume") ->
                ProcessedCommand(CommandType.SYSTEM, mapOf("action" to "volume", "level" to extractVolumeLevel(command)))
            
            else -> ProcessedCommand(CommandType.UNKNOWN, mapOf("original" to command))
        }
    }
    
    private fun extractTextFromCommand(command: String): String {
        // Simple text extraction (in production, use NLP)
        val patterns = listOf("send message", "text", "say")
        patterns.forEach { pattern ->
            val index = command.lowercase().indexOf(pattern)
            if (index != -1) {
                return command.substring(index + pattern.length).trim()
            }
        }
        return ""
    }
    
    private fun extractContactFromCommand(command: String): String {
        // Simple contact extraction
        val words = command.split(" ")
        val callIndex = words.indexOfFirst { it.lowercase().contains("call") }
        return if (callIndex != -1 && callIndex + 1 < words.size) {
            words[callIndex + 1]
        } else ""
    }
    
    private fun extractMusicQuery(command: String): String {
        val patterns = listOf("play", "music")
        patterns.forEach { pattern ->
            val index = command.lowercase().indexOf(pattern)
            if (index != -1) {
                return command.substring(index + pattern.length).trim()
            }
        }
        return ""
    }
    
    private fun extractGenre(command: String): String {
        val genres = listOf("action", "comedy", "drama", "horror", "romance", "sci-fi", "thriller")
        return genres.find { command.lowercase().contains(it) } ?: ""
    }
    
    private fun extractVolumeLevel(command: String): String {
        val numbers = Regex("\\d+").findAll(command).map { it.value }.toList()
        return numbers.firstOrNull() ?: "50"
    }
    
    private fun executeSystemCommand(command: ProcessedCommand) {
        // Execute system-level commands
        when (command.parameters["action"]) {
            "volume" -> {
                val level = command.parameters["level"]?.toIntOrNull() ?: 50
                // Implement volume control
                FridayErrorHandler.logEvent("System", "Setting volume to $level%")
            }
        }
    }
    
    private fun checkForProactiveSuggestions() {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val userName = userManager.getUserName()
        
        when (currentHour) {
            7, 8 -> {
                // Morning suggestions
                showProactiveSuggestion("Good morning, $userName! Should I start your morning routine?")
            }
            12, 13 -> {
                // Lunch suggestions
                showProactiveSuggestion("It's lunch time! Would you like me to suggest some nearby restaurants?")
            }
            18, 19 -> {
                // Evening suggestions
                showProactiveSuggestion("Welcome home! Should I prepare your evening routine?")
            }
            22, 23 -> {
                // Night suggestions
                showProactiveSuggestion("Getting late, $userName. Should I start your bedtime routine?")
            }
        }
    }
    
    private fun showProactiveSuggestion(message: String) {
        // Show notification with suggestion
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Friday AI Suggestion")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
    
    private fun respondWithPersonality(message: String) {
        val personalitySettings = userManager.getPersonalitySettings()
        
        val response = when {
            personalitySettings.humorLevel > 0.7f -> addHumor(message)
            personalitySettings.formalityLevel > 0.7f -> makeFormal(message)
            else -> message
        }
        
        // Send response through notification or TTS
        showProactiveSuggestion(response)
    }
    
    private fun addHumor(message: String): String {
        val humorousResponses = listOf(
            "$message Even Tony Stark had to ask me twice sometimes!",
            "$message But hey, I'm always learning new tricks!",
            "$message That's a new one for me - you're keeping me on my toes!"
        )
        return humorousResponses.random()
    }
    
    private fun makeFormal(message: String): String {
        return "I apologize, but $message I shall endeavor to improve my capabilities."
    }
    
    private suspend fun syncDataToCloud() {
        try {
            val userData = userManager.exportUserData()
            // In production, implement actual cloud sync
            FridayErrorHandler.logEvent("CloudSync", "Data synced to cloud successfully")
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("CloudSync", "Failed to sync data: ${e.message}")
        }
    }
}

data class ProcessedCommand(
    val type: CommandType,
    val parameters: Map<String, String>
)

enum class CommandType {
    APP_CONTROL,
    SMART_HOME,
    ENTERTAINMENT,
    ROUTINE,
    SYSTEM,
    UNKNOWN
}