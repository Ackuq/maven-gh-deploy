#!/bin/bash

current_version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

# Extract major
major_part="${current_version%.*.*}"
# Remove major from the version
rest="${current_version#*.}"
# Get the minor part from rest
minor_part="${rest%.*}"
# Get the patch part from rest
patch_part="${rest#*.}"

case $1 in
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
        echo "First argument should be one of 'MAJOR', 'MINOR', or 'PATCH'"
        exit 1
esac

new_version="$major_part.$minor_part.$patch_part"

echo "Setting new version: $new_version"

mvn versions:set -DnewVersion=$new_version

if [ $CI = true ]; then
    git add ./pom.xml
    git commit -m "Release: $new_version"
    git tag $new_version
    git push
    git push --tags
fi
