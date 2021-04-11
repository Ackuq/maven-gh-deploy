# Maven Central Repository GitHub action deploy

An example project that will be used to demonstrate how to deploy a Java project to Maven and use GitHub actions to update it.

## Table of contents

1. [Prerequisites](#prerequisites)
2. [Maven build plugins](#maven-build-plugins)
    - [Maven source plugin](#maven-source-plugin)
    - [Maven Javadoc plugin](#maven-javadoc-plugin)
    - [Maven GPG plugin](#maven-gpg-plugin)
        - [GPG Configurations](#gpg-Configuration)
    - [Maven Staging Nexus Plugin](#maven-staging-nexus-plugin)
3. [Bash Scripts](#bash-scripts)
    - [Bump version](#bump-version)
    - [Determine bump type](#determine-bump-type)
4. [GitHub Secrets](#github-secrets)
    - [GPG credentials](#gpg-credentials)
    - [Maven Central credentials](#maven-central-credentials)
5. [GitHub Actions](#github-actions)
    - [GPG import test](#gpg-import-test)
    - [Maven deploy](#maven-deploy)

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

## Prerequisites

This demonstration is aimed towards people that already has a package up on Maven Central, information about how to do this can be found in the official documentation [here](https://central.sonatype.org/publish/publish-guide/).

## Maven build plugins

Within the pom.xml file, these build plugins will need to be present to automate the build process for our package.

### Maven source plugin

This plugin is used to archive source files of the package together with the binaries when building, which is required by Maven Central. More information about this plugin can be found [here](https://maven.apache.org/plugins/maven-source-plugin/).

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-source-plugin</artifactId>
  <version>3.2.1</version>
  <executions>
    <execution>
      <id>attach-sources</id>
      <goals>
        <goal>jar</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

### Maven Javadoc plugin

This plugin archives Javadoc files together with the binaries fo the build, which is also required by Maven Central. More information about this plugin can be found [here](https://maven.apache.org/plugins/maven-javadoc-plugin/),

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-javadoc-plugin</artifactId>
  <version>3.2.0</version>
  <executions>
    <execution>
      <id>attach-javadocs</id>
      <goals>
        <goal>jar</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

### Maven GPG plugin

Since Maven Central requires you to have your packages signed with a GPG key, we add this plugin to automate the signing process. If you are unsure about how to get set up with GPG signing, check out [this blog post](https://blog.sonatype.com/2010/01/how-to-generate-pgp-signatures-with-maven/).

#### GPG Configuration

To be able to pass command line options for the GPG passphrase we need to add options for `--pinentry-mode` and `loopback`.

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-gpg-plugin</artifactId>
  <version>1.6</version>
  <executions>
    <execution>
      <id>sign-artifacts</id>
      <phase>verify</phase>
      <goals>
        <goal>sign</goal>
      </goals>
      <configuration>
        <gpgArguments>
          <arg>--pinentry-mode</arg>
          <arg>loopback</arg>
        </gpgArguments>
      </configuration>
    </execution>
  </executions>
</plugin>
```

### Maven Staging Nexus Plugin

This plugin helps us control the Nexus Staging workflow, where our artifacts gets published. Since in this demo we want to release directly to production, we set the `autoReleaseAfterClose` option to `true`. This will result in our artifact be released directly to Maven Central when deployed.

```xml
<plugin>
  <groupId>org.sonatype.plugins</groupId>
  <artifactId>nexus-staging-maven-plugin</artifactId>
  <version>1.6.8</version>
  <extensions>true</extensions>
  <configuration>
    <serverId>ossrh</serverId>
    <nexusUrl>https://s01.oss.sonatype.org/</nexusUrl>
    <autoReleaseAfterClose>true</autoReleaseAfterClose>
  </configuration>
</plugin>
```

## Bash Scripts

These bash scripts contains logic that helps us with semantic versioning when deploying new artifacts.

### Bump version

The bump version script can be found in `string-array-utils/bump_version.sh` and has one input, the type of version bump it should do. The input can be one of `"MAJOR"`, `"MINOR"`, or `"PATCH"`, type indicate which semantic version number to change. The type can be generated from running the `determine_bump_type.sh` script.

### Determine bump type

This scripts looks through the commit messages between the most recent release and the current `HEAD` of the git history, if it detects at least one of `BREAKING CHANGE` or `major(...)` within one of these messages, if this is true, the function will return `"MAJOR"`. If does not detect a major version change, it can detect a minor version change, which requires at least one git message to have `feat(...)` or `minor(...)` within it, this will return `"MINOR"`. If it does not detect any of the mentioned commit message prefixes, it will return the value `"PATCH"`.

When running in the CI it sets the output parameter `bump_type` to the decided type.

## GitHub Secrets

Before starting with out with our GitHub Actions, we set up the secrets that our actions can use.

### GPG credentials

For GitHub Actions to be able use our GPG key for signing the artifact when deploying, it must know both the secret key and the passphrase of the key.

The passphrase should be saved under a secret called `OSSRH_GPG_SECRET_KEY_PASSWORD`, this passphrase is decided by you when you created the GPG key.

To extract the secret key and add it to a secret, do the following:

1. Export your secret key with `gpg --export-secret-keys -a <KEY_ID> > secret-key.txt`
2. Copy the contents of `secret-key.txt`
3. Add the copied contents to a new secret on GitHub called `OSSRH_GPG_SECRET_KEY`.

Your action can now import the secret key with the command:

```bash
echo "${{ secrets.OSSRH_GPG_SECRET_KEY }}" | gpg --batch --import
```

### Maven Central credentials

Your action will need to access your Maven Central credentials to be able to authenticate against the service when deploying. Simply create a secret called `OSSRH_USERNAME`, which includes your username, and one called `OSSRH_PASSWORD`, which includes your password.

## GitHub Actions

Here we will go through the created GitHub actions.

### GPG import test

This workflow can be found in `.github/workflows/test.yml`. The purpose of this workflow is to verify that GitHub actions can import your GPG secret key correctly. It is manually dispatch which means you can test this when desired through the GitHub UI, simply navigate to it by going to: "Actions" > "Test import of GPG key" and press the "run workflow" button.

```yml
name: Test import of GPG key

on: workflow_dispatch

jobs:
    import-gpg-key:
        runs-on: ubuntu-20.04
        steps:
            - name: Install GPG secret key
              run: |
                  echo "${{ secrets.OSSRH_GPG_SECRET_KEY }}" | gpg --batch --import
                  gpg --list-secret-keys --keyid-format LONG
```

### Maven deploy

<!--
#TODO: implement and add documentation
-->
