#!/bin/bash

# Friday AI Deployment Script
# This script helps deploy Friday AI to your Android device

echo "🚀 Friday AI - Android Deployment Script"
echo "=========================================="
echo ""

# Check if Android Studio is installed
if ! command -v adb &> /dev/null; then
    echo "❌ Android Debug Bridge (adb) not found."
    echo "Please install Android Studio and add adb to your PATH."
    echo "Download from: https://developer.android.com/studio"
    exit 1
fi

# Check if device is connected
device_count=$(adb devices | grep -c device)
if [ "$device_count" -le 1 ]; then
    echo "❌ No Android device detected."
    echo "Please connect your device and enable USB debugging."
    echo "Then run this script again."
    exit 1
fi

echo "✅ Android device detected!"
echo ""

# Check if Gradle wrapper exists
if [ ! -f "./gradlew" ]; then
    echo "❌ Gradle wrapper not found."
    echo "Please run this script from the Friday AI project root directory."
    exit 1
fi

echo "🔧 Building Friday AI APK..."
echo ""

# Build the APK
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ APK built successfully!"
    echo ""
    
    # Install the APK
    echo "📱 Installing Friday AI on your device..."
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "🎉 Friday AI installed successfully!"
        echo ""
        echo "📋 Next steps:"
        echo "1. Open Friday AI on your device"
        echo "2. Grant microphone permission when prompted"
        echo "3. Configure your LiveKit Sandbox ID (see DEPLOYMENT_GUIDE.md)"
        echo "4. Start talking to your Friday AI assistant!"
        echo ""
        echo "🎊 Enjoy your Iron Man-inspired AI assistant!"
    else
        echo ""
        echo "❌ Installation failed."
        echo "Please check your device connection and USB debugging settings."
    fi
else
    echo ""
    echo "❌ Build failed."
    echo "Please check the error messages above and refer to DEPLOYMENT_GUIDE.md"
fi

echo ""
echo "📖 For detailed instructions, see DEPLOYMENT_GUIDE.md"
echo "🆘 For support, check the troubleshooting section in the guide"