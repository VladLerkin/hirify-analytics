---
description: Release a new version of the app (Bump version, Commit, Tag, Push)
---

1. Read `gradle/libs.versions.toml` to determine the current `app-version`. The new version will be a patch bump of this version (e.g., if current is `1.0.7`, the new version will be `1.0.8`).
2. Ask the user what changes should be included in the release notes, OR review the recent git commits to generate a summary of changes.
3. Update `RELEASE_NOTES.md` by prepending a new section for the new version (e.g., `## v1.0.8 (Month Year)`) and listing the changes.
4. Run the release script (which will automatically extract the notes from `RELEASE_NOTES.md` and include them in the Git tag):
// turbo
./release_app.sh
