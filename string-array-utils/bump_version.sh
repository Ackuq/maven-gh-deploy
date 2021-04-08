#!/bin/bash

new_version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

case $1 in
    "MAJOR")
        # Get and increment major
        new_major_value=$((${new_version:0:1} + 1))
        # Replace the 1st value with our new value
        new_version=$(echo $new_version | sed s/./$new_major_value/1)
        ;;
    "MINOR")
        # Get and increment minor
        new_minor_value=$((${new_version:2:1} + 1))
        # Replace the 3rd value with our new value
        new_version=$(echo $new_version | sed s/./$new_minor_value/3)
        ;;
    "PATCH")
        # Get and increment patch
        new_patch_value=$((${new_version:4:1} + 1))
        # Replace the 5th value with our new value
        new_version=$(echo $new_version | sed s/./$new_patch_value/5)
        ;;
    *)
        echo "First argument should be one of 'MAJOR', 'MINOR', or 'PATCH'"
        exit 1
esac

echo "Setting new version: $new_version"

mvn versions:set -DnewVersion=$new_version

if [[ $PRODUCTION == 'true' ]]; then
    git add ./pom.xml
    git commit -m "Release: $new_version"
    git tag $new_version
    git push
    git push --tags
fi
