#!/bin/bash

# release_app.sh
# Automates the release process for Version Catalog:
# 1. Bumps app-version (PATCH) and app-versionCode in gradle/libs.versions.toml
# 2. Commits the change
# 3. Tags the release
# 4. Pushes to origin (main + tags)

set -e

TOML_FILE="gradle/libs.versions.toml"

if [ ! -f "$TOML_FILE" ]; then
    echo "Error: $TOML_FILE not found!"
    exit 1
fi

# Function to get property value from TOML (stripping quotes)
get_toml_version() {
    grep "^$1 =" "$TOML_FILE" | cut -d'=' -f2 | tr -d ' "'
}

# 1. Read current versions
CURRENT_VERSION=$(get_toml_version "app-version")
CURRENT_CODE=$(get_toml_version "app-versionCode")

if [ -z "$CURRENT_VERSION" ] || [ -z "$CURRENT_CODE" ]; then
    echo "Error: Could not read app-version or app-versionCode from $TOML_FILE"
    exit 1
fi

echo "Current Version: $CURRENT_VERSION"
echo "Current Version Code: $CURRENT_CODE"

# 2. Calculate new versions
# Split version into components (assuming MAJOR.MINOR.PATCH)
IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_VERSION"
NEW_PATCH=$((PATCH + 1))
NEW_VERSION="$MAJOR.$MINOR.$NEW_PATCH"
NEW_CODE=$((CURRENT_CODE + 1))

echo "New Version: $NEW_VERSION"
echo "New Version Code: $NEW_CODE"

# 3. Update TOML (MacOS compatible sed)
# For version string (with quotes)
sed -i '' "s/^app-version = .*/app-version = \"$NEW_VERSION\"/" "$TOML_FILE"
# For version code (without quotes)
sed -i '' "s/^app-versionCode = .*/app-versionCode = \"$NEW_CODE\"/" "$TOML_FILE"

echo "Updated $TOML_FILE"

# 4. Git operations
git add .
COMMIT_MSG="Release v$NEW_VERSION"
git commit -m "$COMMIT_MSG"

TAG_NAME="v$NEW_VERSION"

# Extract release notes for the new version from RELEASE_NOTES.md
# It looks for the section starting with "## vNEW_VERSION" and stops at the next "## v"
RELEASE_NOTES=$(awk "/^## v$NEW_VERSION/ {flag=1; next} /^## v/ {flag=0} flag" RELEASE_NOTES.md | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')

if [ -n "$RELEASE_NOTES" ]; then
    git tag -a "$TAG_NAME" -m "Release $TAG_NAME" -m "$RELEASE_NOTES"
    echo "Commited and tagged: $TAG_NAME with release notes."
else
    git tag "$TAG_NAME"
    echo "Commited and tagged: $TAG_NAME (no release notes found in RELEASE_NOTES.md)."
fi

# 5. Push
echo "Pushing to origin..."
git push origin main
git push origin "$TAG_NAME"

echo "Release $NEW_VERSION completed successfully!"
