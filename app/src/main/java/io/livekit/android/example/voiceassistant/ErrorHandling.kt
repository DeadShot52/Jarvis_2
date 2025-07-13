package io.livekit.android.example.voiceassistant

import android.content.Context
import android.widget.Toast
import com.github.ajalt.timberkt.Timber

/**
 * Friday AI Error Handling System
 * Provides centralized error handling with user-friendly messages
 */
object FridayErrorHandler {

    sealed class FridayError(val message: String, val userMessage: String) {
        object NetworkError : FridayError(
            "Network connection failed",
            "Friday AI is having trouble connecting. Please check your internet connection."
        )
        
        object AudioPermissionError : FridayError(
            "Audio permission denied",
            "Friday AI needs microphone access to hear you. Please grant permission in settings."
        )
        
        object ConfigurationError : FridayError(
            "Configuration missing",
            "Friday AI configuration is incomplete. Please check the setup guide."
        )
        
        object TranscriptionError : FridayError(
            "Transcription processing failed",
            "Friday AI couldn't process your voice. Please try speaking again."
        )
        
        object TokenError : FridayError(
            "Authentication token invalid",
            "Friday AI authentication failed. Please check your configuration."
        )
        
        class UnknownError(error: String) : FridayError(
            "Unknown error: $error",
            "Friday AI encountered an unexpected issue. Please try again."
        )
    }

    /**
     * Handle errors with appropriate logging and user feedback
     */
    fun handleError(context: Context, error: FridayError, showToast: Boolean = true) {
        // Log the technical error
        Timber.e { "Friday AI Error: ${error.message}" }
        
        // Show user-friendly message if requested
        if (showToast) {
            Toast.makeText(context, error.userMessage, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Handle exceptions with automatic error classification
     */
    fun handleException(context: Context, exception: Exception, showToast: Boolean = true) {
        val fridayError = when {
            exception.message?.contains("network", ignoreCase = true) == true -> FridayError.NetworkError
            exception.message?.contains("permission", ignoreCase = true) == true -> FridayError.AudioPermissionError
            exception.message?.contains("token", ignoreCase = true) == true -> FridayError.TokenError
            exception.message?.contains("transcription", ignoreCase = true) == true -> FridayError.TranscriptionError
            else -> FridayError.UnknownError(exception.message ?: "Unknown error")
        }
        
        handleError(context, fridayError, showToast)
    }

    /**
     * Log Friday AI events for debugging
     */
    fun logEvent(event: String, details: String? = null) {
        if (details != null) {
            Timber.d { "Friday AI Event: $event - $details" }
        } else {
            Timber.d { "Friday AI Event: $event" }
        }
    }
}