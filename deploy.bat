@echo off
title Friday AI - Android Deployment Script

echo 🚀 Friday AI - Android Deployment Script
echo ==========================================
echo.

REM Check if ADB is available
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Android Debug Bridge (adb) not found.
    echo Please install Android Studio and add adb to your PATH.
    echo Download from: https://developer.android.com/studio
    pause
    exit /b 1
)

REM Check if device is connected
for /f %%i in ('adb devices ^| find /c "device"') do set device_count=%%i
if %device_count% leq 1 (
    echo ❌ No Android device detected.
    echo Please connect your device and enable USB debugging.
    echo Then run this script again.
    pause
    exit /b 1
)

echo ✅ Android device detected!
echo.

REM Check if Gradle wrapper exists
if not exist "gradlew.bat" (
    echo ❌ Gradle wrapper not found.
    echo Please run this script from the Friday AI project root directory.
    pause
    exit /b 1
)

echo 🔧 Building Friday AI APK...
echo.

REM Build the APK
gradlew.bat assembleDebug

if %errorlevel% equ 0 (
    echo.
    echo ✅ APK built successfully!
    echo.
    
    REM Install the APK
    echo 📱 Installing Friday AI on your device...
    adb install -r app\build\outputs\apk\debug\app-debug.apk
    
    if %errorlevel% equ 0 (
        echo.
        echo 🎉 Friday AI installed successfully!
        echo.
        echo 📋 Next steps:
        echo 1. Open Friday AI on your device
        echo 2. Grant microphone permission when prompted
        echo 3. Configure your LiveKit Sandbox ID (see DEPLOYMENT_GUIDE.md)
        echo 4. Start talking to your Friday AI assistant!
        echo.
        echo 🎊 Enjoy your Iron Man-inspired AI assistant!
    ) else (
        echo.
        echo ❌ Installation failed.
        echo Please check your device connection and USB debugging settings.
    )
) else (
    echo.
    echo ❌ Build failed.
    echo Please check the error messages above and refer to DEPLOYMENT_GUIDE.md
)

echo.
echo 📖 For detailed instructions, see DEPLOYMENT_GUIDE.md
echo 🆘 For support, check the troubleshooting section in the guide
echo.
pause