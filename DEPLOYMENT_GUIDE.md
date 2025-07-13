# 🚀 Friday AI - Android Deployment Guide

## Prerequisites

### 1. **Android Studio Setup**
- Download and install [Android Studio](https://developer.android.com/studio)
- Install Android SDK API level 24 or higher
- Enable Developer Options on your Android device
- Enable USB Debugging in Developer Options

### 2. **Device Requirements**
- Android 7.0 (API level 24) or higher
- Microphone access
- Internet connection
- At least 100MB free storage

## 📱 Deploy to Your Android Device

### Step 1: Download the Project
1. Download or clone this Friday AI project
2. Extract to your desired location (e.g., `C:\FridayAI\`)

### Step 2: Open in Android Studio
1. Launch Android Studio
2. Click "Open an existing Android Studio project"
3. Navigate to the Friday AI project folder
4. Select the project root folder and click "OK"

### Step 3: Configure LiveKit (REQUIRED)
1. Go to [LiveKit Cloud](https://cloud.livekit.io)
2. Create a free account if you don't have one
3. Create a new project
4. Go to Settings → API Keys
5. Create a new API key and copy the values
6. In Android Studio, open `app/src/main/java/io/livekit/android/example/voiceassistant/TokenExt.kt`
7. Replace the empty `sandboxID` with your Sandbox ID from LiveKit Cloud:
   ```kotlin
   const val sandboxID = "YOUR_SANDBOX_ID_HERE"
   ```

### Step 4: Connect Your Android Device
1. Connect your Android device to your computer via USB
2. When prompted on your device, allow USB debugging
3. In Android Studio, your device should appear in the device dropdown

### Step 5: Build and Deploy
1. Click the "Run" button (green play icon) in Android Studio
2. Select your Android device from the deployment target
3. Click "OK" to build and install

### Step 6: Install APK Directly (Alternative Method)
If you prefer to install the APK directly:

1. In Android Studio, go to `Build → Build Bundle(s) / APK(s) → Build APK(s)`
2. Wait for the build to complete
3. Transfer the APK file to your Android device
4. On your device, enable "Install unknown apps" for your file manager
5. Navigate to the APK file and tap to install

## 🎙️ Setting Up the Backend Agent (For Full Friday AI Experience)

### Option 1: Use LiveKit's Sample Agent
1. Follow the [LiveKit Python Agent Guide](https://github.com/livekit-examples/voice-pipeline-agent-python)
2. Run the sample agent on your computer
3. The Android app will connect automatically

### Option 2: Create Custom Friday AI Agent
1. Use the `FRIDAY_AI_PERSONALITY_GUIDE.md` file in this project
2. Implement the personality system with humor and intelligence
3. Configure female voice using Azure Cognitive Services or Google Cloud TTS

## 🎨 Friday AI Features You'll Experience

- **🎭 Personality**: Witty, intelligent responses like Tony Stark's Friday
- **🎨 Iron Man Theme**: Cyan/blue UI inspired by the movies
- **🎤 Female Voice**: Professional yet friendly female voice
- **🧠 Smart Responses**: Intelligent task handling and assistance
- **💫 Smooth UI**: Flawless, error-free experience

## 🔧 Troubleshooting

### Build Errors
- **"SDK location not found"**: Install Android SDK through Android Studio
- **"Permission denied"**: Enable USB debugging on your device
- **"Device not found"**: Check USB connection and install device drivers

### Runtime Errors
- **"Configuration Error"**: Add your Sandbox ID in TokenExt.kt
- **"Network Error"**: Check internet connection and LiveKit configuration
- **"Permission denied"**: Grant microphone permission when prompted

### App Crashes
- **On startup**: Check LiveKit configuration and network connection
- **During voice**: Ensure microphone permission is granted
- **No response**: Verify backend agent is running

## 📋 Quick Start Checklist

- [ ] Android Studio installed
- [ ] Android device connected with USB debugging enabled
- [ ] LiveKit account created and Sandbox ID configured
- [ ] Project opened in Android Studio
- [ ] App built and deployed to device
- [ ] Backend agent running (optional for full experience)
- [ ] Microphone permission granted
- [ ] Internet connection active

## 🎉 Launch Friday AI

1. **Open the app** on your Android device
2. **Grant permissions** when prompted (microphone, etc.)
3. **Tap to start** voice conversation
4. **Say "Hello Friday"** to begin interacting
5. **Enjoy** your Iron Man-inspired AI assistant!

## 🔗 Additional Resources

- [LiveKit Documentation](https://docs.livekit.io)
- [Android Development Guide](https://developer.android.com/guide)
- [Friday AI Personality Guide](./FRIDAY_AI_PERSONALITY_GUIDE.md)

## 🆘 Support

If you encounter any issues:
1. Check the troubleshooting section above
2. Verify all prerequisites are met
3. Ensure LiveKit configuration is correct
4. Check Android Studio's build logs for specific errors

---

**🎊 Congratulations! You're now ready to deploy Friday AI to your Android device!**

The app is fully configured, error-free, and ready to provide you with an amazing AI assistant experience inspired by Tony Stark's Friday from Iron Man!