package io.livekit.android.example.voiceassistant

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.delay
import java.util.*

/**
 * Entertainment Hub Manager for Friday AI
 * Handles music, movies, games, and entertainment recommendations
 * Developed by Paras Gusain
 */
class EntertainmentHubManager(private val context: Context) {
    
    private val userManager = UserProfileManager(context)
    
    data class EntertainmentRecommendation(
        val type: EntertainmentType,
        val title: String,
        val description: String,
        val genre: String,
        val rating: Float,
        val mood: String,
        val url: String? = null
    )
    
    enum class EntertainmentType {
        MUSIC,
        MOVIE,
        TV_SHOW,
        PODCAST,
        GAME,
        BOOK,
        YOUTUBE_VIDEO
    }
    
    enum class Mood {
        HAPPY,
        SAD,
        ENERGETIC,
        RELAXED,
        FOCUSED,
        ROMANTIC,
        ADVENTUROUS,
        NOSTALGIC
    }
    
    private val musicRecommendations = mapOf(
        "happy" to listOf(
            EntertainmentRecommendation(
                EntertainmentType.MUSIC,
                "Uptown Funk",
                "High-energy funk that'll get you moving",
                "Funk/Pop",
                4.8f,
                "happy"
            ),
            EntertainmentRecommendation(
                EntertainmentType.MUSIC,
                "Can't Stop the Feeling",
                "Pure joy in musical form",
                "Pop",
                4.6f,
                "happy"
            )
        ),
        "focus" to listOf(
            EntertainmentRecommendation(
                EntertainmentType.MUSIC,
                "Deep Focus Playlist",
                "Ambient sounds for maximum concentration",
                "Ambient",
                4.7f,
                "focused"
            ),
            EntertainmentRecommendation(
                EntertainmentType.MUSIC,
                "Lo-Fi Hip Hop",
                "Chill beats for studying and working",
                "Lo-Fi",
                4.9f,
                "focused"
            )
        ),
        "workout" to listOf(
            EntertainmentRecommendation(
                EntertainmentType.MUSIC,
                "Pump It Up",
                "High-energy tracks for maximum motivation",
                "Electronic/Rock",
                4.8f,
                "energetic"
            )
        )
    )
    
    private val movieRecommendations = mapOf(
        "action" to listOf(
            EntertainmentRecommendation(
                EntertainmentType.MOVIE,
                "Iron Man",
                "Perfect choice! Tony Stark's origin story",
                "Action/Sci-Fi",
                4.9f,
                "adventurous"
            ),
            EntertainmentRecommendation(
                EntertainmentType.MOVIE,
                "The Avengers",
                "Friday's debut in the MCU!",
                "Action/Sci-Fi",
                4.8f,
                "adventurous"
            )
        ),
        "comedy" to listOf(
            EntertainmentRecommendation(
                EntertainmentType.MOVIE,
                "Guardians of the Galaxy",
                "Action-comedy with an amazing soundtrack",
                "Action/Comedy",
                4.7f,
                "happy"
            )
        ),
        "drama" to listOf(
            EntertainmentRecommendation(
                EntertainmentType.MOVIE,
                "The Pursuit of Happyness",
                "Inspiring story of perseverance",
                "Drama",
                4.6f,
                "nostalgic"
            )
        )
    )
    
    private val podcastRecommendations = listOf(
        EntertainmentRecommendation(
            EntertainmentType.PODCAST,
            "The Joe Rogan Experience",
            "Long-form conversations with interesting people",
            "Talk/Interview",
            4.5f,
            "focused"
        ),
        EntertainmentRecommendation(
            EntertainmentType.PODCAST,
            "TED Talks Daily",
            "Ideas worth spreading, daily",
            "Educational",
            4.7f,
            "focused"
        )
    )
    
    suspend fun startMonitoring() {
        // Monitor entertainment patterns and provide recommendations
        while (true) {
            try {
                checkMoodBasedRecommendations()
                analyzeListeningPatterns()
                provideTimeBasedSuggestions()
                delay(300000) // Check every 5 minutes
            } catch (e: Exception) {
                FridayErrorHandler.logEvent("Entertainment", "Monitoring error: ${e.message}")
                delay(600000) // Wait longer on error
            }
        }
    }
    
    suspend fun handleRequest(type: String, query: String) {
        try {
            when (type.lowercase()) {
                "music" -> handleMusicRequest(query)
                "movie" -> handleMovieRequest(query)
                "podcast" -> handlePodcastRequest(query)
                "game" -> handleGameRequest(query)
                "youtube" -> handleYouTubeRequest(query)
                else -> handleGenericRequest(query)
            }
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("Entertainment", "Error handling request: ${e.message}")
        }
    }
    
    fun executeEntertainmentCommand(command: ProcessedCommand) {
        try {
            val action = command.parameters["action"] ?: ""
            val query = command.parameters["query"] ?: ""
            val genre = command.parameters["genre"] ?: ""
            
            when (action) {
                "play_music" -> playMusic(query, genre)
                "suggest_movie" -> suggestMovie(genre)
                "find_podcast" -> findPodcast(query)
                "recommend_entertainment" -> recommendBasedOnMood()
                "play_youtube" -> playYouTube(query)
                else -> {
                    showEntertainmentNotification("I'm not sure how to help with that entertainment request, but I'm learning!")
                }
            }
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("Entertainment", "Error executing command: ${e.message}")
        }
    }
    
    private fun handleMusicRequest(query: String) {
        val personalitySettings = userManager.getPersonalitySettings()
        val mood = detectMoodFromQuery(query)
        
        val recommendations = musicRecommendations[mood] ?: musicRecommendations["happy"]!!
        val selectedTrack = recommendations.random()
        
        val message = when {
            personalitySettings.humorLevel > 0.7f ->
                "Playing ${selectedTrack.title}! Even Tony needs good music to tinker with his suits."
            personalitySettings.formalityLevel > 0.7f ->
                "I have selected ${selectedTrack.title} based on your preferences."
            else ->
                "Playing ${selectedTrack.title} - ${selectedTrack.description}"
        }
        
        playMusic(selectedTrack.title, selectedTrack.genre)
        showEntertainmentNotification("🎵 $message")
    }
    
    private fun handleMovieRequest(query: String) {
        val genre = extractGenreFromQuery(query)
        val recommendations = movieRecommendations[genre] ?: movieRecommendations["action"]!!
        val selectedMovie = recommendations.random()
        
        val personalitySettings = userManager.getPersonalitySettings()
        val message = when {
            personalitySettings.humorLevel > 0.7f && selectedMovie.title.contains("Iron Man") ->
                "Excellent choice! ${selectedMovie.title} - where I learned everything I know about being awesome!"
            personalitySettings.humorLevel > 0.7f ->
                "${selectedMovie.title} is a great pick! ${selectedMovie.description}"
            else ->
                "I recommend ${selectedMovie.title} - ${selectedMovie.description}"
        }
        
        openMovieApp(selectedMovie.title)
        showEntertainmentNotification("🎬 $message")
    }
    
    private fun handlePodcastRequest(query: String) {
        val selectedPodcast = podcastRecommendations.random()
        
        val message = "I found ${selectedPodcast.title} - ${selectedPodcast.description}"
        openPodcastApp(selectedPodcast.title)
        showEntertainmentNotification("🎙️ $message")
    }
    
    private fun handleGameRequest(query: String) {
        val gameRecommendations = listOf(
            "Among Us - Great for playing with friends!",
            "Candy Crush - Perfect for quick fun breaks",
            "Clash of Clans - Strategy and building",
            "PUBG Mobile - Battle royale excitement"
        )
        
        val recommendation = gameRecommendations.random()
        showEntertainmentNotification("🎮 How about $recommendation")
    }
    
    private fun handleYouTubeRequest(query: String) {
        playYouTube(query)
        
        val personalitySettings = userManager.getPersonalitySettings()
        val message = when {
            personalitySettings.humorLevel > 0.7f ->
                "Searching YouTube for '$query'! Let's see what rabbit hole we fall into this time."
            else ->
                "Playing '$query' on YouTube!"
        }
        
        showEntertainmentNotification("📺 $message")
    }
    
    private fun handleGenericRequest(query: String) {
        val entertainment = recommendBasedOnTimeAndMood()
        val message = "Based on your preferences, I suggest: ${entertainment.title} - ${entertainment.description}"
        showEntertainmentNotification("✨ $message")
    }
    
    private fun playMusic(title: String, genre: String) {
        try {
            // Try Spotify first
            val spotifyIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("spotify:search:$title")
            }
            
            try {
                spotifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(spotifyIntent)
            } catch (e: Exception) {
                // Fallback to YouTube Music or generic music player
                val musicIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://music.youtube.com/search?q=${title.replace(" ", "+")}")
                }
                musicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(musicIntent)
            }
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("Entertainment", "Error playing music: ${e.message}")
        }
    }
    
    private fun suggestMovie(genre: String) {
        val recommendations = movieRecommendations[genre.lowercase()] ?: movieRecommendations["action"]!!
        val movie = recommendations.random()
        
        showEntertainmentNotification("🎬 I recommend ${movie.title} - ${movie.description}")
        openMovieApp(movie.title)
    }
    
    private fun openMovieApp(movieTitle: String) {
        try {
            // Try Netflix first
            val netflixIntent = context.packageManager.getLaunchIntentForPackage("com.netflix.mediaclient")
            if (netflixIntent != null) {
                netflixIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(netflixIntent)
                return
            }
            
            // Try Amazon Prime Video
            val primeIntent = context.packageManager.getLaunchIntentForPackage("com.amazon.avod.thirdpartyclient")
            if (primeIntent != null) {
                primeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(primeIntent)
                return
            }
            
            // Fallback to Google Play Movies
            val playMoviesIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.videos")
            if (playMoviesIntent != null) {
                playMoviesIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(playMoviesIntent)
            }
            
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("Entertainment", "Error opening movie app: ${e.message}")
        }
    }
    
    private fun openPodcastApp(podcastTitle: String) {
        try {
            // Try Spotify for podcasts
            val spotifyIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("spotify:search:$podcastTitle")
            }
            
            try {
                spotifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(spotifyIntent)
            } catch (e: Exception) {
                // Fallback to Google Podcasts
                val podcastIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.apps.podcasts")
                if (podcastIntent != null) {
                    podcastIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(podcastIntent)
                }
            }
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("Entertainment", "Error opening podcast app: ${e.message}")
        }
    }
    
    private fun playYouTube(query: String) {
        try {
            val youtubeIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.youtube.com/results?search_query=${query.replace(" ", "+")}")
            }
            youtubeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(youtubeIntent)
        } catch (e: Exception) {
            FridayErrorHandler.logEvent("Entertainment", "Error opening YouTube: ${e.message}")
        }
    }
    
    private fun findPodcast(query: String) {
        val matchingPodcasts = podcastRecommendations.filter { 
            it.title.contains(query, ignoreCase = true) || 
            it.description.contains(query, ignoreCase = true) ||
            it.genre.contains(query, ignoreCase = true)
        }
        
        val podcast = if (matchingPodcasts.isNotEmpty()) {
            matchingPodcasts.random()
        } else {
            podcastRecommendations.random()
        }
        
        openPodcastApp(podcast.title)
        showEntertainmentNotification("🎙️ Found ${podcast.title} - ${podcast.description}")
    }
    
    private fun recommendBasedOnMood(): EntertainmentRecommendation {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val personalitySettings = userManager.getPersonalitySettings()
        
        return when {
            currentHour in 6..9 -> {
                // Morning - energetic content
                EntertainmentRecommendation(
                    EntertainmentType.MUSIC,
                    "Morning Energy Playlist",
                    "Upbeat tracks to start your day right!",
                    "Pop/Electronic",
                    4.8f,
                    "energetic"
                )
            }
            currentHour in 12..14 -> {
                // Lunch - light entertainment
                EntertainmentRecommendation(
                    EntertainmentType.YOUTUBE_VIDEO,
                    "Quick Comedy Skits",
                    "Short, funny videos for your lunch break",
                    "Comedy",
                    4.6f,
                    "happy"
                )
            }
            currentHour in 18..22 -> {
                // Evening - relaxing content
                if (personalitySettings.humorLevel > 0.6f) {
                    movieRecommendations["comedy"]?.random() ?: EntertainmentRecommendation(
                        EntertainmentType.MOVIE,
                        "Comedy Night",
                        "Time for some laughs!",
                        "Comedy",
                        4.7f,
                        "happy"
                    )
                } else {
                    EntertainmentRecommendation(
                        EntertainmentType.MUSIC,
                        "Evening Chill",
                        "Relaxing sounds for winding down",
                        "Ambient",
                        4.5f,
                        "relaxed"
                    )
                }
            }
            else -> {
                // Default recommendation
                EntertainmentRecommendation(
                    EntertainmentType.MUSIC,
                    "Friday's Choice",
                    "A personally curated selection",
                    "Mixed",
                    4.8f,
                    "happy"
                )
            }
        }
    }
    
    private fun checkMoodBasedRecommendations() {
        val personalitySettings = userManager.getPersonalitySettings()
        
        if (personalitySettings.proactiveness > 0.7f) {
            val recommendation = recommendBasedOnTimeAndMood()
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            
            when (currentHour) {
                8 -> suggestEntertainment("Good morning! How about some energizing music to start your day?", recommendation)
                12 -> suggestEntertainment("Lunch break entertainment?", recommendation)
                19 -> suggestEntertainment("Evening wind-down time?", recommendation)
            }
        }
    }
    
    private fun analyzeListeningPatterns() {
        // Analyze user's entertainment preferences and update recommendations
        val preferences = userManager.getPreferences()
        // This would track and learn from user's choices
    }
    
    private fun provideTimeBasedSuggestions() {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        when {
            dayOfWeek == Calendar.FRIDAY && hour >= 17 -> {
                suggestEntertainment("It's Friday evening! Time to celebrate the weekend!", 
                    EntertainmentRecommendation(
                        EntertainmentType.MUSIC,
                        "Weekend Vibes",
                        "Upbeat tracks for Friday night!",
                        "Party",
                        4.9f,
                        "happy"
                    )
                )
            }
            dayOfWeek == Calendar.SUNDAY && hour >= 18 -> {
                suggestEntertainment("Sunday evening chill time?",
                    EntertainmentRecommendation(
                        EntertainmentType.MOVIE,
                        "Sunday Night Movie",
                        "Something relaxing before the week starts",
                        "Drama",
                        4.6f,
                        "relaxed"
                    )
                )
            }
        }
    }
    
    private fun recommendBasedOnTimeAndMood(): EntertainmentRecommendation {
        return recommendBasedOnMood()
    }
    
    private fun detectMoodFromQuery(query: String): String {
        return when {
            query.contains("happy", ignoreCase = true) || 
            query.contains("upbeat", ignoreCase = true) ||
            query.contains("energetic", ignoreCase = true) -> "happy"
            
            query.contains("chill", ignoreCase = true) ||
            query.contains("relax", ignoreCase = true) ||
            query.contains("calm", ignoreCase = true) -> "focus"
            
            query.contains("workout", ignoreCase = true) ||
            query.contains("exercise", ignoreCase = true) ||
            query.contains("gym", ignoreCase = true) -> "workout"
            
            else -> "happy"
        }
    }
    
    private fun extractGenreFromQuery(query: String): String {
        val genres = listOf("action", "comedy", "drama", "horror", "romance", "sci-fi", "thriller", "documentary")
        return genres.find { query.contains(it, ignoreCase = true) } ?: "action"
    }
    
    private fun suggestEntertainment(message: String, recommendation: EntertainmentRecommendation) {
        val personalitySettings = userManager.getPersonalitySettings()
        val enhancedMessage = when {
            personalitySettings.humorLevel > 0.7f ->
                "$message I suggest ${recommendation.title} - because even Tony Stark needs good entertainment!"
            personalitySettings.formalityLevel > 0.7f ->
                "$message I recommend ${recommendation.title} for your consideration."
            else ->
                "$message Try ${recommendation.title} - ${recommendation.description}"
        }
        
        showEntertainmentNotification("🎭 $enhancedMessage")
    }
    
    private fun showEntertainmentNotification(message: String) {
        FridayErrorHandler.logEvent("Entertainment", "Entertainment suggestion: $message")
    }
    
    fun getRecommendationsForMood(mood: String): List<EntertainmentRecommendation> {
        val musicRecs = musicRecommendations[mood.lowercase()] ?: emptyList()
        val movieRecs = movieRecommendations.values.flatten().filter { it.mood == mood.lowercase() }
        return musicRecs + movieRecs
    }
    
    fun getUserEntertainmentStats(): Map<String, Any> {
        return mapOf(
            "preferred_genres" to userManager.getPreferredMovieGenres(),
            "music_preferences" to userManager.getPreferredMusicGenres(),
            "listening_time" to "2.5 hours/day", // This would be tracked
            "favorite_apps" to listOf("Spotify", "Netflix", "YouTube")
        )
    }
}