#!/bin/bash

# Get the current version
current_version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

commit_messages=$(git log --pretty=%B 1.0.3...HEAD)

if [[ $commit_messages == *"BREAKING CHANGE"* || $commit_messages == *"major("*")"* ]]; then
    echo "Found breaking or major changes, will bump major"
    type="MAJOR"
elif [[ $commit_messages == *"feat("*")"* || $commit_messages == *"minor("*")"* ]]; then
    echo "Found new features or minor changes, will bump minor"
    type="MINOR"
else
    echo "No new features or major/minor changes, will bump patch"
    type="PATCH"
fi

# On CI, set the output
if [ $CI = true ]; then
    echo "::set-output name=bump_type::$type"
fi
