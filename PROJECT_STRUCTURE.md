# 📁 Friday AI - Project Structure

## 🗂️ Main Files & Folders

```
Friday AI/
├── 📱 app/                          # Main Android app
│   ├── src/main/
│   │   ├── java/io/livekit/android/example/voiceassistant/
│   │   │   ├── 📄 MainActivity.kt           # Main UI and voice interaction
│   │   │   ├── 📄 TokenExt.kt              # LiveKit authentication
│   │   │   ├── 📄 PermissionsExt.kt        # Permission handling
│   │   │   ├── 📄 ErrorHandling.kt         # Error management system
│   │   │   ├── 📁 ui/
│   │   │   │   ├── 📄 UserTranscription.kt # Chat bubble UI
│   │   │   │   └── 📁 theme/
│   │   │   │       ├── 📄 Color.kt         # Friday AI colors
│   │   │   │       ├── 📄 Theme.kt         # App theme
│   │   │   │       └── 📄 Type.kt          # Typography
│   │   │   └── 📁 datastreams/
│   │   │       └── 📄 RememberTranscriptions.kt # Voice processing
│   │   ├── res/
│   │   │   ├── 📄 values/strings.xml       # App name and text
│   │   │   ├── 📄 values/themes.xml        # Theme configuration
│   │   │   └── 📄 AndroidManifest.xml      # Permissions and settings
│   │   └── 📄 build.gradle.kts             # App dependencies
├── 📄 build.gradle.kts                     # Project build file
├── 📄 settings.gradle.kts                  # Project settings
├── 📄 gradle.properties                    # Gradle configuration
├── 📄 gradlew & gradlew.bat                # Build scripts
└── 📁 gradle/                              # Gradle wrapper
```

## 🎯 Key Components

### **Frontend (Android App)**
- **MainActivity.kt**: Main UI with voice visualization and chat interface
- **Theme System**: Iron Man-inspired cyan/blue colors
- **Error Handling**: Comprehensive error management with user-friendly messages
- **Voice UI**: Real-time voice visualization and transcription display
- **Permission System**: Microphone and audio permissions handling

### **Configuration Files**
- **TokenExt.kt**: LiveKit authentication (⚠️ ADD YOUR SANDBOX ID HERE)
- **AndroidManifest.xml**: App permissions and settings
- **build.gradle.kts**: Dependencies and build configuration

### **UI Components**
- **UserTranscription.kt**: Chat bubble design for user messages
- **Color.kt**: Friday AI color palette (customizable)
- **Theme.kt**: Material 3 theme with Friday AI styling

## 🔧 How It Works

1. **Voice Input**: User speaks into microphone
2. **LiveKit Processing**: Audio sent to LiveKit cloud service
3. **Backend Agent**: Processes voice and generates response
4. **Voice Output**: Response played back through device speaker
5. **UI Updates**: Transcriptions shown in real-time chat interface

## 📝 Files You Can Customize

### **🎨 Visual Customization**
- `Color.kt` - Change Friday AI colors
- `Theme.kt` - Modify app theme
- `UserTranscription.kt` - Customize chat bubbles

### **🔧 Configuration**
- `TokenExt.kt` - Add your LiveKit credentials
- `strings.xml` - Change app name and text
- `AndroidManifest.xml` - Modify permissions

### **🧠 Functionality**
- `MainActivity.kt` - Main app logic
- `ErrorHandling.kt` - Error messages and handling
- `RememberTranscriptions.kt` - Voice processing logic

## 🚀 Deployment Files

- **📄 QUICK_START.md**: 5-minute setup guide
- **📄 DEPLOYMENT_GUIDE.md**: Detailed deployment instructions
- **📄 deploy.bat / deploy.sh**: One-click deployment scripts
- **📄 FRIDAY_AI_PERSONALITY_GUIDE.md**: Backend personality implementation

## 📋 What's Included

✅ **Complete Android app** with Iron Man theme
✅ **Error-free, production-ready** code
✅ **Comprehensive error handling** system
✅ **Voice visualization** and chat interface
✅ **Permission management** for microphone access
✅ **LiveKit integration** for voice processing
✅ **Deployment scripts** for easy installation
✅ **Detailed documentation** and guides

## 🎊 Ready to Deploy!

Your Friday AI project is complete and ready to deploy to your Android device. Follow the `QUICK_START.md` guide to get started!