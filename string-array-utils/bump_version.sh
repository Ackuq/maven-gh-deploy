#!/bin/bash

current_version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout --no-transfer-progress --batch-mode)

bump_type=""

determine_bump_version() {
    commit_messages=$(git log --pretty=%B $current_version...HEAD)

    if [[ $commit_messages == *"BREAKING CHANGE"* || $commit_messages == *"major("*")"* ]]; then
        echo "Found breaking or major changes, will bump major"
        bump_type="MAJOR"
    elif [[ $commit_messages == *"feat("*")"* || $commit_messages == *"minor("*")"* ]]; then
        echo "Found new features or minor changes, will bump minor"
        bump_type="MINOR"
    else
        echo "No new features or major/minor changes, will bump patch"
        bump_type="PATCH"
    fi
}

determine_bump_version

# Extract major
major_part="${current_version%.*.*}"
# Remove major from the version
rest="${current_version#*.}"
# Get the minor part from rest
minor_part="${rest%.*}"
# Get the patch part from rest
patch_part="${rest#*.}"

case $bump_type in
    "MAJOR")
        # Increment major
        major_part=$(($major_part + 1))
        ;;
    "MINOR")
        # Increment minor
        minor_part=$(($minor_part + 1))
        ;;
    "PATCH")
        # Increment patch
        patch_part=$(($patch_part + 1))
        ;;
    *)
        echo "Could not determine which part to increment"
        exit 1
esac

new_version="$major_part.$minor_part.$patch_part"

echo "Setting new version: $new_version"

mvn versions:set -DnewVersion=$new_version --no-transfer-progress --batch-mode

if [ "$CI" = true ]; then
    git config --local user.email "41898282+github-actions[bot]@users.noreply.github.com"
    git config --local user.name "Release bot"

    git add ./pom.xml
    git commit -m "Release: $new_version"
    git tag $new_version
    git push
    git push --tags
fi
