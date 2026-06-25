# ⚡️ Hirify Analytics

> A cross-platform HR analytics and job market analysis tool built with Kotlin Multiplatform and Compose Multiplatform.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.11.1-brightgreen)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Gradle](https://img.shields.io/badge/Gradle-9.5.1-02303A.svg?logo=gradle)](https://gradle.org)

## ✨ Features

- 📊 **Interactive Analytics Dashboards** - Visualize real-time job market data and HR metrics.
- 🔍 **Vacancy Filtering** - Deep dive into specializations, skills, formats, and regions.
- 🎤 **Voice Input (AI)** - Use offline (Vosk) or cloud (Whisper, Google, Yandex) speech recognition to enter filters by voice.
- 🎨 **Modern UI** - Built with Compose Multiplatform for a native look and feel on every platform.
- ⚡️ **High Performance** - Native compilation guarantees fast processing of massive analytics datasets.
- 🌐 **Cross-Platform** - Available on Desktop, Web, Android, and iOS.

## 📦 Download

**Ready-to-use distributions are available for free!**

Get the latest version for your platform:

<div align="center">

## [⬇️ Download Latest Release](https://github.com/VladLerkin/hirify-analytics/releases)

<br/>

*Looking for what's new?* <br/>
**[📄 View Release Notes (Changelog)](https://github.com/VladLerkin/hirify-analytics/blob/main/RELEASE_NOTES.md)**

</div>

**Available platforms:**
- 🖥️ **macOS** - Universal binary (Apple Silicon)
- 🪟 **Windows** - MSI installer and portable .exe
- 📱 **Android** - APK for Android 8.0+
- 🌐 **Web** - Browser distribution (Wasm)
- 🍎 **iOS** - Developer's build

> 💡 No compilation needed! Just download and run for free.

## 🚀 Quick Start

### Desktop
```bash
./gradlew :app-desktop:run
```

### Android
```bash
./gradlew :app-android:installDebug
```

### Web
```bash
./gradlew :app-web:wasmJsBrowserDevelopmentRun
```

### iOS
```bash
./gradlew :app-ios:linkDebugFrameworkIosSimulatorArm64
```

> 📖 **Detailed guides:** [Desktop](docs/BUILD_DESKTOP.md) • [Android](docs/BUILD_ANDROID.md) • [iOS](docs/BUILD_IOS.md)

## 🏗️ Architecture

```
├── core/                   # Shared domain models & Hirify API Client
├── ui/                     # Shared Compose UI components & Dashboards
├── app-desktop/            # Desktop application
├── app-android/            # Android application
├── app-ios/                # iOS application
├── app-web/                # Web application
└── gradle/
    └── libs.versions.toml  # Centralized Dependency Management (Version Catalog)
```

## 🛠️ Tech Stack & Dependencies

The project uses **Gradle Version Catalog** for centralized dependency management. All versions are defined in `gradle/libs.versions.toml`.

| Component | Version |
|-----------|---------|
| Kotlin | 2.4.0 |
| Compose Multiplatform | 1.11.1 |
| Android Compose BOM | 2026.05.01 |
| Gradle | 9.5.1 |
| Android Gradle Plugin | 9.2.1 |
| Ktor | 3.5.0 |
| JDK | 25 |

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

<p align="center">Made with ❤️ using Kotlin Multiplatform</p>
