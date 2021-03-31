# Maven GitHub action deploy

An example project that will be used to demonstrate how to deploy a Java project to Maven and use GitHub actions to update it.

## Steps taken

1. Create .gitignore
2. Run `mvn archetype:generate -DgroupId=io.github.ackuq -DartifactId=string-array-utils -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false`
3. Create basic utility function code
4. Get approved by OSSRH
5. Add distributionManagement to pom.xml
