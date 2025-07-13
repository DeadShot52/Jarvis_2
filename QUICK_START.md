# 🚀 Friday AI - Quick Start for Android Device

## ⚡ Deploy in 5 Minutes!

Since you have your Android device ready, follow these steps to get Friday AI running:

### 1. **Download Required Software** (5 minutes)
- Download [Android Studio](https://developer.android.com/studio) 
- Install it with default settings (includes Android SDK)

### 2. **Prepare Your Android Device** (2 minutes)
- Go to **Settings → About Phone**
- Tap **Build Number** 7 times to enable Developer Options
- Go to **Settings → Developer Options**  
- Enable **USB Debugging**
- Connect your device to your computer via USB

### 3. **Download This Project** (1 minute)
- Download the Friday AI project folder to your computer
- Extract to a folder like `C:\FridayAI\` or `~/FridayAI/`

### 4. **One-Click Deploy** (2 minutes)
**Option A - Easy Method:**
1. Open command prompt/terminal in the Friday AI folder
2. Run: `deploy.bat` (Windows) or `./deploy.sh` (Mac/Linux)
3. Follow the prompts

**Option B - Android Studio Method:**
1. Open Android Studio
2. Click "Open an existing project"
3. Select the Friday AI folder
4. Click the green "Run" button
5. Select your device and click OK

### 5. **Configure LiveKit** (Required for voice)
1. Go to [LiveKit Cloud](https://cloud.livekit.io) and create free account
2. Create a new project
3. Copy your Sandbox ID
4. Edit `app/src/main/java/io/livekit/android/example/voiceassistant/TokenExt.kt`
5. Replace `const val sandboxID = ""` with your ID:
   ```kotlin
   const val sandboxID = "your_sandbox_id_here"
   ```

### 6. **Launch Friday AI** (30 seconds)
1. Find "Friday AI" on your Android device
2. Open the app
3. Grant microphone permission when prompted
4. Start talking to your Friday AI assistant!

---

## 🎉 That's It! You're Ready!

Your Friday AI assistant is now running on your Android device with:
- **Iron Man-inspired theme** (cyan/blue colors)
- **Smart error handling** 
- **Professional female voice** (when backend is configured)
- **Personality and humor** capabilities

## 🆘 Having Issues?

- **Build fails**: Check `DEPLOYMENT_GUIDE.md` for detailed troubleshooting
- **No device detected**: Ensure USB debugging is enabled
- **App crashes**: Verify LiveKit configuration
- **No response**: Check internet connection

## 🔗 Next Steps

- Read `FRIDAY_AI_PERSONALITY_GUIDE.md` to add humor and intelligence
- Configure backend agent for full Friday AI experience
- Customize the theme colors in `Color.kt`

**🎊 Enjoy your Friday AI assistant!**