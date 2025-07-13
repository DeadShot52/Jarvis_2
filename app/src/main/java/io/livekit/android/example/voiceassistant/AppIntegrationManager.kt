package io.livekit.android.example.voiceassistant

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import kotlinx.coroutines.delay

/**
 * App Integration Manager for Friday AI
 * Handles integration with WhatsApp, Instagram, Gmail, and other apps
 * Developed by Paras Gusain
 */
class AppIntegrationManager(private val context: Context) {
    
    private val userManager = UserProfileManager(context)
    
    data class AppIntegration(
        val packageName: String,
        val displayName: String,
        val capabilities: List<AppCapability>
    )
    
    enum class AppCapability {
        SEND_MESSAGE,
        MAKE_CALL,
        OPEN_CHAT,
        COMPOSE_EMAIL,
        POST_CONTENT,
        SEARCH,
        PLAY_MEDIA,
        CREATE_EVENT
    }
    
    private val supportedApps = mapOf(
        "whatsapp" to AppIntegration(
            "com.whatsapp",
            "WhatsApp",
            listOf(AppCapability.SEND_MESSAGE, AppCapability.MAKE_CALL, AppCapability.OPEN_CHAT)
        ),
        "instagram" to AppIntegration(
            "com.instagram.android",
            "Instagram",
            listOf(AppCapability.POST_CONTENT, AppCapability.SEARCH)
        ),
        "gmail" to AppIntegration(
            "com.google.android.gm",
            "Gmail",
            listOf(AppCapability.COMPOSE_EMAIL, AppCapability.SEARCH)
        ),
        "spotify" to AppIntegration(
            "com.spotify.music",
            "Spotify",
            listOf(AppCapability.PLAY_MEDIA, AppCapability.SEARCH)
        ),
        "youtube" to AppIntegration(
            "com.google.android.youtube",
            "YouTube",
            listOf(AppCapability.PLAY_MEDIA, AppCapability.SEARCH)
        ),
        "facebook" to AppIntegration(
            "com.facebook.katana",
            "Facebook",
            listOf(AppCapability.POST_CONTENT, AppCapability.SEARCH)
        ),
        "twitter" to AppIntegration(
            "com.twitter.android",
            "Twitter",
            listOf(AppCapability.POST_CONTENT, AppCapability.SEARCH)
        ),
        "calendar" to AppIntegration(
            "com.google.android.calendar",
            "Google Calendar",
            listOf(AppCapability.CREATE_EVENT, AppCapability.SEARCH)
        ),
        "phone" to AppIntegration(
            "com.android.dialer",
            "Phone",
            listOf(AppCapability.MAKE_CALL)
        ),
        "messages" to AppIntegration(
            "com.google.android.apps.messaging",
            "Messages",
            listOf(AppCapability.SEND_MESSAGE)
        )
    )
    
    suspend fun startMonitoring() {
        // Monitor app usage and provide proactive suggestions
        while (true) {
            try {
                checkForAppSuggestions()
                monitorNotifications()
                delay(60000) // Check every minute
            } catch (e: Exception) {
                FridayErrorHandler.logEvent("AppIntegration", "Monitoring error: ${e.message}")
                delay(120000) // Wait longer on error
            }
        }
    }
    
    suspend fun executeAppCommand(command: ProcessedCommand) {
        try {
            val action = command.parameters["action"] ?: ""
            val appName = command.parameters["app"] ?: ""
            
            when (action) {
                "open" -> openApp(appName)
                "send_message" -> {
                    val contact = command.parameters["contact"] as? String
                    val message = command.parameters["text"] as? String
                    val app = command.parameters["app"] as? String ?: "whatsapp"
                    sendMessage(app, contact, message)
                }
                "make_call" -> {
                    val contact = command.parameters["contact"] as? String
                    makeCall(contact)
                }
                "compose_email" -> {
                    val recipient = command.parameters["recipient"] as? String
                    val subject = command.parameters["subject"] as? String
                    val body = command.parameters["body"] as? String
                    composeEmail(recipient, subject, body)
                }
                "search" -> {
                    val query = command.parameters["query"] as? String
                    val app = command.parameters["app"] as? String ?: "google"
                    searchInApp(app, query)
                }
                "post" -> {
                    val content = command.parameters["content"] as? String
                    val app = command.parameters["app"] as? String ?: "instagram"
                    createPost(app, content)
                }
                "play" -> {
                    val query = command.parameters["query"] as? String
                    val app = command.parameters["app"] as? String ?: "spotify"
                    playMedia(app, query)
                }
            }
            
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("AppIntegration", "Error executing app command: ${e.message}")
        }
    }
    
    private fun openApp(appName: String) {
        try {
            val app = supportedApps[appName.lowercase()]
            if (app != null) {
                val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    
                    val personalitySettings = userManager.getPersonalitySettings()
                    val message = when {
                        personalitySettings.humorLevel > 0.7f -> 
                            "Opening ${app.displayName}! Time to be social... or antisocial, your choice!"
                        personalitySettings.formalityLevel > 0.7f ->
                            "${app.displayName} has been launched successfully."
                        else ->
                            "Opening ${app.displayName} for you!"
                    }
                    
                    showAppNotification(message)
                } else {
                    showAppNotification("${app.displayName} is not installed on your device.")
                }
            } else {
                showAppNotification("I don't have integration for '$appName' yet, but I'm learning!")
            }
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("AppIntegration", "Error opening app '$appName': ${e.message}")
        }
    }
    
    private fun sendMessage(appName: String, contact: String?, message: String?) {
        try {
            when (appName.lowercase()) {
                "whatsapp" -> sendWhatsAppMessage(contact, message)
                "messages", "sms" -> sendSMSMessage(contact, message)
                else -> {
                    showAppNotification("I can't send messages through '$appName' yet, but I'm working on it!")
                }
            }
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("AppIntegration", "Error sending message: ${e.message}")
        }
    }
    
    private fun sendWhatsAppMessage(contact: String?, message: String?) {
        try {
            if (contact == null || message == null) {
                showAppNotification("I need both a contact and message to send via WhatsApp!")
                return
            }
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                `package` = "com.whatsapp"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            
            // Try to find contact and send directly
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            
            val personalitySettings = userManager.getPersonalitySettings()
            val confirmMessage = when {
                personalitySettings.humorLevel > 0.7f ->
                    "WhatsApp message ready! Just like Tony texting Happy, but probably more important."
                else ->
                    "WhatsApp message prepared for $contact!"
            }
            
            showAppNotification(confirmMessage)
            
        } catch (e: Exception) {
            showAppNotification("WhatsApp isn't available. Should I try regular SMS instead?")
        }
    }
    
    private fun sendSMSMessage(contact: String?, message: String?) {
        try {
            if (contact == null || message == null) {
                showAppNotification("I need both a contact and message to send SMS!")
                return
            }
            
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$contact")
                putExtra("sms_body", message)
            }
            
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            
            showAppNotification("SMS message prepared for $contact!")
            
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("AppIntegration", "Error sending SMS: ${e.message}")
        }
    }
    
    private fun makeCall(contact: String?) {
        try {
            if (contact == null) {
                showAppNotification("Who would you like me to call?")
                return
            }
            
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$contact")
            }
            
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            
            val personalitySettings = userManager.getPersonalitySettings()
            val message = when {
                personalitySettings.humorLevel > 0.7f ->
                    "Calling $contact! Hope they pick up faster than Tony answers his phone."
                else ->
                    "Calling $contact now!"
            }
            
            showAppNotification(message)
            
        } catch (e: Exception) {
            showAppNotification("I need phone permission to make calls. Check your settings!")
        }
    }
    
    private fun composeEmail(recipient: String?, subject: String?, body: String?) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                if (recipient != null) putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                if (subject != null) putExtra(Intent.EXTRA_SUBJECT, subject)
                if (body != null) putExtra(Intent.EXTRA_TEXT, body)
            }
            
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            
            val personalitySettings = userManager.getPersonalitySettings()
            val message = when {
                personalitySettings.humorLevel > 0.7f ->
                    "Email composition ready! Much more sophisticated than Tony's one-liner texts."
                personalitySettings.formalityLevel > 0.7f ->
                    "Email composition interface has been initialized."
                else ->
                    "Email ready to compose!"
            }
            
            showAppNotification(message)
            
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("AppIntegration", "Error composing email: ${e.message}")
        }
    }
    
    private fun searchInApp(appName: String, query: String?) {
        try {
            if (query == null) {
                showAppNotification("What would you like me to search for?")
                return
            }
            
            when (appName.lowercase()) {
                "google", "search" -> {
                    val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                        putExtra("query", query)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                "youtube" -> {
                    val intent = Intent(Intent.ACTION_SEARCH).apply {
                        `package` = "com.google.android.youtube"
                        putExtra("query", query)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                "spotify" -> {
                    val intent = Intent(Intent.ACTION_SEARCH).apply {
                        `package` = "com.spotify.music"
                        putExtra("query", query)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                else -> {
                    showAppNotification("I'll add search for '$appName' in my next update!")
                }
            }
            
            showAppNotification("Searching for '$query' in $appName!")
            
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("AppIntegration", "Error searching: ${e.message}")
        }
    }
    
    private fun createPost(appName: String, content: String?) {
        try {
            when (appName.lowercase()) {
                "instagram" -> {
                    val intent = context.packageManager.getLaunchIntentForPackage("com.instagram.android")
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        showAppNotification("Instagram opened! Time to share something amazing!")
                    } else {
                        showAppNotification("Instagram isn't installed on your device.")
                    }
                }
                "facebook" -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        `package` = "com.facebook.katana"
                        if (content != null) putExtra(Intent.EXTRA_TEXT, content)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                "twitter" -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        `package` = "com.twitter.android"
                        if (content != null) putExtra(Intent.EXTRA_TEXT, content)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                else -> {
                    showAppNotification("I'm still learning how to post on '$appName'!")
                }
            }
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("AppIntegration", "Error creating post: ${e.message}")
        }
    }
    
    private fun playMedia(appName: String, query: String?) {
        try {
            when (appName.lowercase()) {
                "spotify" -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("spotify:search:${query ?: "music"}")
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Fallback to opening Spotify app
                        openApp("spotify")
                    }
                }
                "youtube" -> {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://www.youtube.com/results?search_query=${query ?: "music"}")
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                else -> {
                    // Generic music intent
                    val intent = Intent("android.intent.action.MUSIC_PLAYER")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            }
            
            val personalitySettings = userManager.getPersonalitySettings()
            val message = when {
                personalitySettings.humorLevel > 0.7f ->
                    "Playing '$query' in $appName! Even Tony needs good music to work."
                else ->
                    "Playing '$query' in $appName!"
            }
            
            showAppNotification(message)
            
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("AppIntegration", "Error playing media: ${e.message}")
        }
    }
    
    private fun checkForAppSuggestions() {
        // Check usage patterns and suggest app actions
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val personalitySettings = userManager.getPersonalitySettings()
        
        if (personalitySettings.proactiveness > 0.6f) {
            when (currentHour) {
                9 -> suggestAppAction("Good morning! Should I check your emails in Gmail?")
                12 -> suggestAppAction("Lunch time! Want me to find nearby restaurants?")
                17 -> suggestAppAction("End of workday! Should I help you catch up with friends on WhatsApp?")
            }
        }
    }
    
    private fun monitorNotifications() {
        // Monitor for notification patterns to provide proactive suggestions
        // This would require notification access permission
    }
    
    private fun suggestAppAction(suggestion: String) {
        showAppNotification("💡 $suggestion")
    }
    
    private fun showAppNotification(message: String) {
        FridayErrorHandler.logEvent("AppIntegration", "App action: $message")
    }
    
    fun getInstalledSupportedApps(): List<AppIntegration> {
        return supportedApps.values.filter { app ->
            try {
                context.packageManager.getPackageInfo(app.packageName, 0)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    fun getAppCapabilities(appName: String): List<AppCapability> {
        return supportedApps[appName.lowercase()]?.capabilities ?: emptyList()
    }
}