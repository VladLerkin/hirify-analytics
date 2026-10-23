# Hirify Analytics Release Notes

## v1.0.49 (October 2026)
* **OpenAI Model Migration:** Replaced the retiring `gpt-4o-mini` (API shutdown 2027-01-14) with `gpt-6-luna` in the preset, defaults and connection test. Settings saved with `gpt-4o-mini` are migrated automatically on load, API keys are kept. OpenAI requests now send `reasoning_effort=none` (GPT-6 only) so tools keep working, and `max_completion_tokens` instead of `max_tokens`.
* **Android Voice Fix:** Google Speech recording on Android now uses `AudioRecord` (16 kHz mono WAV) instead of `MediaRecorder` with a non-existent FLAC encoder, which failed immediately with "Invalid audio encoder".
* **iOS Crash Fix:** Dropdown menus in AI settings no longer crash in landscape (infinite height constraint); their height is now capped.
* **Kotlin Refactoring:** Shared `WavAudio` helper replaces ~200 lines of duplicated CAF-to-WAV code in the Whisper and Google Speech clients; `Pair`/`Triple` replaced by `AiPreset` and small data classes; removed `!!` in chart, label and Sherpa code; dropped an unneeded `@OptIn` for Base64.

## v1.0.48 (October 2026)
* **iOS Linker Warning Fix:** Raised iOS deployment target from `18.0` to `18.5` to match the Kotlin/Native `libicu` object and remove the "built for newer iOS-simulator version" linker warning.

## v1.0.47 (October 2026)
* **Dependency Updates:** Updated Compose Multiplatform to `1.12.2` and kotlinx-serialization to `1.12.0`.
* **Skill Discovery:** Added `.claude/skills` symlink to `.agents/skills` so Claude Code picks up project skills (`check-updates`, `release-app`, `android-debug-launch`).

## v1.0.46 (October 2026)
* **Skill & Automation Improvements:** Enhanced `check-updates` skill to inspect and report version status of GitHub Actions workflows in `.github/workflows/` alongside Gradle dependencies.
* **Workflow Modernization:** Updated `actions/setup-java` to the latest major version (`@v6`).

## v1.0.45 (October 2026)
* **GitHub Actions Modernization:** Updated CI and Release workflow actions to their latest major releases (`actions/checkout@v7`, `actions/setup-java@v5`, `gradle/actions/setup-gradle@v6`, `android-actions/setup-android@v4`, `actions/upload-artifact@v7`, `actions/download-artifact@v8`, and `softprops/action-gh-release@v3`).

## v1.0.44 (October 2026)
* **Desktop ProGuard & JDK Toolchain Fix:** Fixed ProGuard JDK library resolution in `app-desktop` to strictly match the target Java version (Java 25) via toolchain/environment, preventing CI runners from accidentally using legacy JDK 11 JMODs.
* **ProGuard Logging Clean-up:** Added `-dontnote **` in `proguard-rules.pro` to eliminate hundreds of verbose duplicate resource and descriptor `Note:` messages from build logs.

## v1.0.43 (October 2026)
* **Gradle 9.6+ DSL Modernization:** Upgraded build scripts across modules (`app-desktop`, `app-web`, `core`, `ui`) to modern Gradle 9.6+ collection APIs (`named()` instead of deprecated `by getting`).
* **KMP & Android DSL Cleanup:** Replaced deprecated `androidLibrary` block with `android` in `core`, updated build task registration to `tasks.register(...)`.
* **CI Build Stability:** Added `kotlin.native.ignoreDisabledTargets=true` to suppress non-macOS host warnings for iOS Simulator tests in Linux CI environments.
* **Code Modernization:** Replaced deprecated Java `URL` constructors with `URI.create().toURL()`, updated tar archive streaming API, and specified preferred tooltip anchor positioning.

## v1.0.42 (October 2026)
* **Build & Memory Fix:** Increased Gradle heap memory to 4GB (`-Xmx4096m`) to prevent JVM GC thrashing / OutOfMemory during Android Release builds on CI. Removed deprecated `kotlin.mpp.androidSourceSetLayoutVersion`.
* **Compose API Modernization:** Replaced deprecated Compose Material 3 APIs (`ScrollableTabRow` -> `PrimaryScrollableTabRow`, `MenuAnchorType` -> `ExposedDropdownMenuAnchorType`, `rememberPlainTooltipPositionProvider` -> `rememberTooltipPositionProvider`).
* **Icons & Lifecycle Fixes:** Migrated `Icons.Filled.ExitToApp` and `Icons.Outlined.HelpOutline` to `AutoMirrored` variants; replaced deprecated Voyager `LifecycleEffect` with standard `DisposableEffect`.

## v1.0.41 (October 2026)
* **Kotlin 2.4 Modernization & Refactoring:** Refactored AI clients and `MainViewModel` using modern Kotlin idioms (`sealed interface`, `entries` enum resolution, `require`/`error` contracts, streamlined coroutine flow & cancellation handling). Extracted shared `OpenAiCompatibleJson` mapper.
* **Cleanup:** Removed deprecated Yandex Cloud integration (YandexGPT and Yandex SpeechKit), unreferenced dialogs and dead legacy components (`ResourceLoader`, `UiAction`, unused config fields).
* **Cross-platform Fixes:** Added missing `actual` implementations and fixed Wasm target compilation issues (`DateUtils`, `SherpaRecognizerManager`).
* **Desktop Security:** Refactored `AiSettingsStorage` on desktop to use standard library `kotlin.io.encoding.Base64` and clean secret management.

## v1.0.40 (October 2026)
* **Build Improvements:** Updated GitHub Actions to use `gradle/actions/setup-gradle@v4` instead of the deprecated `setup-java` caching.

## v1.0.39 (October 2026)
* **Dependency Updates:** Updated Kotlin to 2.4.21, Compose Multiplatform to 1.12.1, AGP to 9.4.1, Ktor to 3.6.0, and Apache Commons Compress to 1.28.0.
* **Build Improvements:** Upgraded Gradle wrapper to the latest stable version (9.8.1).

## v1.0.38 (September 2026)
* **Bug Fix:** Fixed iOS compilation issue with ExperimentalForeignApi in ModelFileWriter and SherpaRecognizerManager.

## v1.0.37 (September 2026)
* **Bug Fix:** Fixed an issue where the `AgentService` would fall back to Ollama instead of using the local LLM client for offline voice processing.
* **Bug Fix:** Fixed missing `LOCAL_LLAMATIK` logic in `AiSettingsScreen`, which prevented the Local AI model download button from appearing in the UI.
* **AI Models:** Renamed "Local Gemma 4 E4B QAT" preset to clearly indicate "Offline & Free".

## v1.0.36 (September 2026)
* **Bug Fix:** Fixed a UI freeze issue on older machines when pressing "Stop Recording". The heavy initialization of the Sherpa-ONNX model has been moved to a background thread to prevent blocking the UI.

## v1.0.35 (September 2026)
* **Bug Fix:** Fixed an issue where the desktop application's UI would freeze (deadlock) when stopping or cancelling voice recording.

## v1.0.34 (September 2026)
* **Performance:** Sped up Sherpa-ONNX model extraction on older Windows systems by adding stream buffering to the Java fallback extractor.

## v1.0.33 (September 2026)
* **Feature:** Added an explicit "Exit" button in the application UI for easier quitting on Android and Desktop.
* **Feature:** Added a "Delete Model" button to the AI Settings screen for managing downloaded Sherpa-ONNX models.
* **Bug Fix:** Enhanced STT connection testing to correctly evaluate the Sherpa-ONNX offline model state alongside cloud STT providers.
* **Agent Skills:** Added the `android-debug-launch` skill to ensure correct ADB launching of debug builds.
## v1.0.32 (September 2026)
* **Bug Fix:** Fixed an issue where the STT recording format was hardcoded to `.m4a`, causing Sherpa-ONNX offline speech recognition to fail because it expects `.wav` format.


## v1.0.31 (September 2026)
* **Bug Fix:** Fixed an issue where Sherpa-ONNX offline recognition threw a ClassNotFoundException on Android by ensuring the dependency is properly packaged in the APK.
* **Bug Fix:** Fixed the voice model testing in the AI Config Dialog by supplying proper default models for STT connection testing.

## v1.0.30 (September 2026)
* **Bug Fix:** Fixed an Android release build crash by adding Proguard keep rules for Sherpa-ONNX JNI reflection.

## v1.0.29 (September 2026)
* **Bug Fix:** Fixed an issue where Sherpa-ONNX model download interruptions would leave a corrupted model and silently fail.
* **Performance:** Replaced slow Java-based bzip2 compression with native `tar` extraction, significantly reducing model extraction time.

## v1.0.28 (September 2026)
* **Speech Recognition:** Migrated offline voice recognition engine from Vosk to Sherpa-ONNX for improved accuracy and stability.
* **Dependency Updates:** Updated Android Compose BOM to `2026.09.00` and Android Gradle Plugin to `9.4.0`.
* **Agent Skills:** Updated the `check-updates` skill to automatically maintain dependency versions in the README.

## v1.0.27 (September 2026)
* **iOS Build Fix:** Fixed an iOS native compilation issue in `DateUtils` related to C-Interop memory access by correctly using `useContents` for C-structs.

## v1.0.26 (September 2026)
* **Chart Data Extrapolation:** Implemented extrapolation for the current month's incomplete data based on the number of days passed, preventing the chart from falsely dipping at the end.
* **UI Improvements:** The extrapolated segment of the chart is now visually distinguished with a dashed line to indicate forecasted data.
* **Stability:** Migrated date processing from third-party libraries to native multiplatform `expect`/`actual` implementations, eliminating JVM classloading issues on desktop.

## v1.0.25 (September 2026)
* **Bug Fixes:** Fixed chart x-axis rendering to show consistent periods and correctly handle missing data. Also resolved an issue where tab state (like uncommitted search queries) was incorrectly retained across newly created tabs.
* **Agent Skills:** Refactored the release workflow to comply with the Agent Skills specification and added a new `check-updates` skill for automated dependency tracking.

## v1.0.24 (September 2026)
* **Code Maintenance:** Reorganized utility scripts into a dedicated `scripts/` directory for better project structure.
* **Documentation:** Updated AI workflows and application documentation to reflect the new script locations.

## v1.0.23 (August 2026)
* **Build Fixes:** Downgraded Gradle wrapper to version `9.6.1` and fixed a version conflict with the Compose Multiplatform plugin (`1.11.1`).

## v1.0.21 (August 2026)
* **Dependency Updates:** Updated Gradle wrapper to version `9.7.0` and synchronized corresponding versions in project documentation.

## v1.0.20 (August 2026)
* **Dependency Updates:** Updated stable dependencies to their latest versions: Android Gradle Plugin (`9.3.1`), Ktor (`3.5.2`), and llamatik (`1.10.0`).

## v1.0.19 (July 2026)
* **Code Simplification:** Migrated state properties in `MainViewModel` and `VoiceInputProcessor` to use Kotlin's new explicit backing fields (`field`) syntax for cleaner code.

## v1.0.18 (July 2026)
* **Dependency Updates:** Updated Gradle wrapper to version `9.6.1` and Kotlin to `2.4.10`, along with synchronizing corresponding versions in project documentation.

## v1.0.17 (July 2026)
* **UI Bug Fix:** Fixed a logic bug in the Status filter (All, Active, Archived) where multiple mutually exclusive options could be selected simultaneously. The filter now correctly acts as a single-select choice.

## v1.0.16 (July 2026)
* **Web Integration Fixes:** Fixed the URL mapping in `toHirifyWebUrl()` to correctly pass missing filters (`title`, `country`, `macroregion`, `source`) using the exact keys (`search`, `countries`, `regions`, `sources_filter`) expected by the frontend on `hirify.me`.

## v1.0.15 (July 2026)
* **Expanded Analytics Filters:** Exposed all analytical API filter parameters in the UI, including Work Format, Remote Type, Specializations, Skills, Grade, Company Type, Employee Type, English Level, Vacancy Language, Status, Primary Source, and Include Scam.
* **Improved UX & Dictionary Autocomplete:** Added autocomplete suggestion dropdowns for Specializations, Skills, Countries (Страны), and Geo-regions (Гео-регионы).
* **UI Order Alignment:** Aligned the sidebar filter layout, labels, and placeholders to match the official hirify.me website.

## v1.0.14 (July 2026)
* **Release Process Improvements:** Extracted version bumping logic into a dedicated script (`up_version.sh`) for a more deterministic release process. Updated the AI workflow to execute this step explicitly.

## v1.0.13 (July 2026)
* **Dependency Updates:** Updated Voyager to version `2.2.21-1.10.3`.
* **Tooling Fixes:** Removed filter in `check_updates.py` to allow correctly checking Voyager dependencies. Fixed release script to prevent version skipping.

## v1.0.12 (June 2026)
* **Dependency Updates:** Safely bumped multiple libraries (Ktor, llamatik, Koin, JNA, Compose BOM) to their latest stable versions.
* **CI Improvements:** Added concurrency blocks to GitHub Actions releases to prevent duplicate overlapping runs.
* **Tooling:** Introduced a python script (`check_updates.py`) to easily check and monitor dependency updates.

## v1.0.10 (June 2026)
* **Bug Fixes:** Resolved an issue where pasting an API key with whitespace or a trailing newline caused an HTTP connection error. Keys are now automatically trimmed.

## v1.0.8 (June 2026)
* **Multi-Language Support:** Introduced a new robust i18n system for multi-language translations across the UI.
* **Smart AI Agents:** Enhanced AI functionality by validating skills against an API dictionary for greater accuracy.
* **Automated Releases:** Upgraded the release workflow to automatically sync and include release notes in Git tags.

## v1.0.7 (June 2026)
* **Multiple Chart Series (Google Trends Style):** Compare up to 5 different job market queries on the same chart simultaneously.
* **Dynamic Tab Labels:** Added auto-generated semantic names for each query tab (e.g., "Java, Сеньор") based on active filters.
* **Enhanced Mobile UX:** In portrait mode, tapping an active tab now quickly toggles the sidebar for filter adjustments.

## v1.0.6 (June 2026)
* **Voice Dictation Integration:** Support for offline (Vosk) and cloud (Whisper, Yandex, Google) voice inputs for filtering.
* **Cleanups:** Removed unused app icon assets and optimized build config.
* **Fixes:** Various bug fixes and UI stability improvements under the hood.

## v1.0.1 - v1.0.5 (June 2026)
* **API Stabilization:** Improved the `HirifyApiClient` error handling.
* **UX Improvements:** Fine-tuned spacing in the left sidebar and enhanced tooltip discoverability.
* **Performance:** Minor native compilation optimizations.

## v1.0.0 (June 2026)
* **Initial Release:** Launched Hirify Analytics — a cross-platform tool for HR and job market analytics.
* **Core Features:** 
  * Interactive job market dashboards.
  * Real-time vacancy filtering by skills, specializations, regions, and more.
  * Unified UI across Desktop, Web, Android, and iOS using Compose Multiplatform.
