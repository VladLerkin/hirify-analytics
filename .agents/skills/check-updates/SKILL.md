---
name: check-updates
description: Check for dependency updates and update stable versions across gradle files
---

1. Run `python3 scripts/check_updates.py`. This script will fetch the latest versions for all project dependencies and output a summary table.
2. Review the output from the script. Look for rows where the status is `Update Avail` and the `Latest Stable` version is newer than the `Current` version.
3. Determine which dependencies have new **stable** versions available. Do not update to pre-release, alpha, or beta versions (such as `alpha`, `beta`, `RC`, etc.) unless the user explicitly requests it.
4. For each stable update available, you must update the version string in the project.
5. First, update the corresponding version in `gradle/libs.versions.toml`.
6. **CRITICAL**: You must ALSO update the versions in `settings.gradle.kts` if the updated dependency is a plugin defined there. For example, if `kotlin`, `compose`, `agp`, `serialization`, or `foojay-resolver` is updated, you MUST replace their version strings in the `plugins { ... }` block of `settings.gradle.kts` so that they match the versions in the TOML file.
7. Build the project to verify that the dependency updates did not break the build.
8. Finally, update the `README.md` to document which dependency versions were updated (e.g. in the Tech Stack & Dependencies table).
