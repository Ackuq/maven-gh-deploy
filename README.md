# Maven GitHub action deploy

An example project that will be used to demonstrate how to deploy a Java project to Maven and use GitHub actions to update it.

## Steps taken

1. Create .gitignore
2. Run `mvn archetype:generate -DgroupId=io.github.ackuq -DartifactId=string-array-utils -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false`
3. Create basic utility function code
4. Get approved by OSSRH
5. Add distributionManagement, developers, description & scm to pom.xml
6. Install gpg for signing
7. Add maven-gpg-plugin, maven-javadoc-plugin, maven-source-plugin and nexus-staging-maven-plugin in pom.xml
8. Create a key with gpg, note down the passphrase
9. Upload gpg key with `gpg --keyserver hkp://pool.sks-keyservers.net --send-keys <KEY>`
10. Add login details in `~/.m2/`
11. Do a release with `mvn clean deploy -P release`
12. Create secret keys for `OSSRH_GPG_SECRET_KEY`, `OSSRH_GPG_SECRET_KEY_PASSWORD`, `OSSRH_PASSWORD` and `OSSRH_USERNAME`

## Scripts

### Bump version

The bump version script can be found in `string-array-utils/bump_version.sh` and has one input, the type of version bump it should do. The input can be one of `"MAJOR"`, `"MINOR"`, or `"PATCH"`, type indicate which semantic version number to change. The type can be generated from running the `determine_bump_type.sh` script.

### Determine bump type

This scripts looks through the commit messages between the most recent release and the current `HEAD` of the git history, if it detects at least one of `BREAKING CHANGE` or `major(...)` within one of these messages, if this is true, the function will return `"MAJOR"`. If does not detect a major version change, it can detect a minor version change, which requires at least one git message to have `feat(...)` or `minor(...)` within it, this will return `"MINOR"`. If it does not detect any of the mentioned commit message prefixes, it will return the value `"PATCH"`.

When running in the CI it sets the output parameter `bump_type` to the decided type.
