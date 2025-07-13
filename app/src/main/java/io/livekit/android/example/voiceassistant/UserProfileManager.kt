package io.livekit.android.example.voiceassistant

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.security.MessageDigest
import java.util.*

/**
 * Friday AI User Profile Manager
 * Handles user data, voice biometrics, preferences, and cloud sync
 * Developed by Paras Gusain
 */
class UserProfileManager(private val context: Context) {
    
    private val sharedPrefs: SharedPreferences = 
        context.getSharedPreferences("friday_user_profile", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    data class UserProfile(
        val userId: String = UUID.randomUUID().toString(),
        val name: String = "",
        val email: String = "",
        val setupComplete: Boolean = false,
        val voicePatternHash: String = "",
        val voiceConfidence: Float = 0.0f,
        val personalitySettings: PersonalitySettings = PersonalitySettings(),
        val preferences: UserPreferences = UserPreferences(),
        val createdAt: Long = System.currentTimeMillis(),
        val lastLogin: Long = System.currentTimeMillis()
    )
    
    data class PersonalitySettings(
        val humorLevel: Float = 0.7f,        // 0.0 = serious, 1.0 = very funny
        val formalityLevel: Float = 0.3f,     // 0.0 = casual, 1.0 = formal
        val proactiveness: Float = 0.6f,      // 0.0 = reactive, 1.0 = very proactive
        val responseSpeed: Float = 0.8f,      // 0.0 = slow, 1.0 = instant
        val memoryRetention: Boolean = true   // Remember conversations
    )
    
    data class UserPreferences(
        val preferredApps: List<String> = listOf(),
        val smartHomeDevices: List<String> = listOf(),
        val dailyRoutines: Map<String, List<String>> = mapOf(),
        val notifications: NotificationSettings = NotificationSettings(),
        val privacy: PrivacySettings = PrivacySettings(),
        val cloudSync: Boolean = true,
        val backgroundOperation: Boolean = true
    )
    
    data class NotificationSettings(
        val voiceAlerts: Boolean = true,
        val systemNotifications: Boolean = true,
        val quietHours: Pair<Int, Int> = Pair(22, 7), // 10 PM to 7 AM
        val emergencyOverride: Boolean = true
    )
    
    data class PrivacySettings(
        val voiceDataRetention: Int = 30, // days
        val conversationEncryption: Boolean = true,
        val anonymousUsage: Boolean = false,
        val dataSharingConsent: Boolean = false
    )
    
    data class VoiceBiometric(
        val voicePattern: String = "",
        val frequency: FloatArray = floatArrayOf(),
        val pitch: Float = 0.0f,
        val tone: Float = 0.0f,
        val confidence: Float = 0.0f,
        val samples: Int = 0
    )
    
    // Core Profile Management
    fun saveUserProfile(profile: UserProfile) {
        with(sharedPrefs.edit()) {
            putString("user_profile", gson.toJson(profile))
            putBoolean("setup_complete", profile.setupComplete)
            putString("user_name", profile.name)
            putLong("last_login", System.currentTimeMillis())
            apply()
        }
        FridayErrorHandler.logEvent("Profile", "User profile saved for ${profile.name}")
    }
    
    fun getUserProfile(): UserProfile? {
        val profileJson = sharedPrefs.getString("user_profile", null)
        return if (profileJson != null) {
            try {
                gson.fromJson(profileJson, UserProfile::class.java)
            } catch (e: Exception) {
                FridayErrorHandler.logEvent("Profile", "Error loading profile: ${e.message}")
                null
            }
        } else null
    }
    
    fun isUserSetup(): Boolean {
        return sharedPrefs.getBoolean("setup_complete", false)
    }
    
    fun getUserName(): String {
        return sharedPrefs.getString("user_name", "User") ?: "User"
    }
    
    // Voice Biometric Management
    fun saveVoiceBiometric(voiceData: ByteArray, confidence: Float): Boolean {
        try {
            val voiceHash = generateVoiceHash(voiceData)
            val profile = getUserProfile() ?: UserProfile()
            
            val updatedProfile = profile.copy(
                voicePatternHash = voiceHash,
                voiceConfidence = confidence
            )
            
            saveUserProfile(updatedProfile)
            
            // Save detailed voice biometric data separately
            val voiceBiometric = analyzeVoicePattern(voiceData)
            with(sharedPrefs.edit()) {
                putString("voice_biometric", gson.toJson(voiceBiometric))
                apply()
            }
            
            FridayErrorHandler.logEvent("Voice", "Voice biometric saved with confidence: $confidence")
            return true
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("Voice", "Error saving voice biometric: ${e.message}")
            return false
        }
    }
    
    fun verifyVoice(voiceData: ByteArray): Float {
        try {
            val currentHash = generateVoiceHash(voiceData)
            val profile = getUserProfile() ?: return 0.0f
            
            if (profile.voicePatternHash.isEmpty()) return 0.0f
            
            // Simple hash comparison (in production, use advanced voice recognition)
            val similarity = compareVoiceHashes(currentHash, profile.voicePatternHash)
            
            FridayErrorHandler.logEvent("Voice", "Voice verification result: $similarity")
            return similarity
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("Voice", "Error verifying voice: ${e.message}")
            return 0.0f
        }
    }
    
    private fun generateVoiceHash(voiceData: ByteArray): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(voiceData)
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }
    
    private fun analyzeVoicePattern(voiceData: ByteArray): VoiceBiometric {
        // Simplified voice analysis (in production, use advanced ML models)
        val frequency = FloatArray(10) { kotlin.random.Random.nextFloat() }
        val pitch = kotlin.random.Random.nextFloat() * 300 + 100 // 100-400 Hz
        val tone = kotlin.random.Random.nextFloat()
        
        return VoiceBiometric(
            voicePattern = generateVoiceHash(voiceData),
            frequency = frequency,
            pitch = pitch,
            tone = tone,
            confidence = 0.85f + kotlin.random.Random.nextFloat() * 0.15f,
            samples = 1
        )
    }
    
    private fun compareVoiceHashes(hash1: String, hash2: String): Float {
        if (hash1.isEmpty() || hash2.isEmpty()) return 0.0f
        
        // Simple string similarity (in production, use advanced voice comparison)
        val commonChars = hash1.zip(hash2).count { it.first == it.second }
        val similarity = commonChars.toFloat() / maxOf(hash1.length, hash2.length)
        
        return if (similarity > 0.7f) similarity else 0.0f
    }
    
    // Personality Management
    fun updatePersonalitySettings(settings: PersonalitySettings) {
        val profile = getUserProfile() ?: UserProfile()
        val updatedProfile = profile.copy(personalitySettings = settings)
        saveUserProfile(updatedProfile)
        FridayErrorHandler.logEvent("Personality", "Settings updated")
    }
    
    fun getPersonalitySettings(): PersonalitySettings {
        return getUserProfile()?.personalitySettings ?: PersonalitySettings()
    }
    
    // Preferences Management
    fun updatePreferences(preferences: UserPreferences) {
        val profile = getUserProfile() ?: UserProfile()
        val updatedProfile = profile.copy(preferences = preferences)
        saveUserProfile(updatedProfile)
        FridayErrorHandler.logEvent("Preferences", "User preferences updated")
    }
    
    fun getPreferences(): UserPreferences {
        return getUserProfile()?.preferences ?: UserPreferences()
    }
    
    // Routine Management
    fun addDailyRoutine(routineName: String, actions: List<String>) {
        val preferences = getPreferences()
        val updatedRoutines = preferences.dailyRoutines.toMutableMap()
        updatedRoutines[routineName] = actions
        
        val updatedPreferences = preferences.copy(dailyRoutines = updatedRoutines)
        updatePreferences(updatedPreferences)
        
        FridayErrorHandler.logEvent("Routine", "Added routine: $routineName")
    }
    
    fun getDailyRoutines(): Map<String, List<String>> {
        return getPreferences().dailyRoutines
    }
    
    // App Integration Management
    fun addPreferredApp(packageName: String, appName: String) {
        val preferences = getPreferences()
        val updatedApps = preferences.preferredApps.toMutableList()
        if (!updatedApps.contains(packageName)) {
            updatedApps.add(packageName)
            val updatedPreferences = preferences.copy(preferredApps = updatedApps)
            updatePreferences(updatedPreferences)
            FridayErrorHandler.logEvent("Apps", "Added preferred app: $appName")
        }
    }
    
    fun getPreferredApps(): List<String> {
        return getPreferences().preferredApps
    }
    
    // Cloud Sync Management
    fun isCloudSyncEnabled(): Boolean {
        return getPreferences().cloudSync
    }
    
    fun enableCloudSync(enable: Boolean) {
        val preferences = getPreferences()
        val updatedPreferences = preferences.copy(cloudSync = enable)
        updatePreferences(updatedPreferences)
        FridayErrorHandler.logEvent("Cloud", "Cloud sync ${if (enable) "enabled" else "disabled"}")
    }
    
    // Privacy Management
    fun updatePrivacySettings(privacy: PrivacySettings) {
        val preferences = getPreferences()
        val updatedPreferences = preferences.copy(privacy = privacy)
        updatePreferences(updatedPreferences)
        FridayErrorHandler.logEvent("Privacy", "Privacy settings updated")
    }
    
    fun getPrivacySettings(): PrivacySettings {
        return getPreferences().privacy
    }
    
    // Session Management
    fun updateLastLogin() {
        val profile = getUserProfile() ?: return
        val updatedProfile = profile.copy(lastLogin = System.currentTimeMillis())
        saveUserProfile(updatedProfile)
    }
    
    fun getLastLoginTime(): Long {
        return getUserProfile()?.lastLogin ?: 0L
    }
    
    // Data Management
    fun exportUserData(): String {
        val profile = getUserProfile() ?: return ""
        return gson.toJson(profile)
    }
    
    fun clearUserData() {
        with(sharedPrefs.edit()) {
            clear()
            apply()
        }
        FridayErrorHandler.logEvent("Data", "User data cleared")
    }
    
    // Entertainment Preferences
    fun updateEntertainmentPreferences(movieGenres: List<String>, musicGenres: List<String>) {
        with(sharedPrefs.edit()) {
            putString("preferred_movie_genres", gson.toJson(movieGenres))
            putString("preferred_music_genres", gson.toJson(musicGenres))
            apply()
        }
        FridayErrorHandler.logEvent("Entertainment", "Entertainment preferences updated")
    }
    
    fun getPreferredMovieGenres(): List<String> {
        val json = sharedPrefs.getString("preferred_movie_genres", "[]")
        return try {
            gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
        } catch (e: Exception) {
            listOf()
        }
    }
    
    fun getPreferredMusicGenres(): List<String> {
        val json = sharedPrefs.getString("preferred_music_genres", "[]")
        return try {
            gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
        } catch (e: Exception) {
            listOf()
        }
    }
}