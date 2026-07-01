---
description: Release a new version of the app (Bump version, Commit, Tag, Push)
---

1. Read `gradle/libs.versions.toml` to determine the current `app-version` and `app-versionCode`. Calculate the new patch version (e.g., if current is `1.0.7`, the new version will be `1.0.8`) and increment the version code.
2. Update `gradle/libs.versions.toml` with the new `app-version` and `app-versionCode`.
3. Ask the user what changes should be included in the release notes, OR review the recent git commits to generate a summary of changes.
4. Update `RELEASE_NOTES.md` by prepending a new section for the new version (e.g., `## v1.0.8 (Month Year)`) and listing the changes.
5. Run the release script (which will read the new version, commit the changes, and tag the release):
// turbo
./release_app.sh
