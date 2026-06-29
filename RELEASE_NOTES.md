# Hirify Analytics Release Notes

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
