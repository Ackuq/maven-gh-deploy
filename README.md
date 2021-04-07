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
