# GitHub Action to Deploy to Maven Central Repository

This repository contains an example on how to deploy a Java project to the [Maven Central repository](https://search.maven.org/) 
with [GitHub actions](https://docs.github.com/en/actions). Furthermore, the workflow is described and shown in a video 
on YouTube (ADD LINK).

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Architecture](#architecture)
3. [Maven Project Set-up](#maven-project-set-up)
    - [Maven Source Plugin](#maven-source-plugin)
    - [Maven Javadoc Plugin](#maven-javadoc-plugin)
    - [Maven GPG Plugin](#maven-gpg-plugin)
    - [Maven Staging Nexus Plugin](#maven-staging-nexus-plugin)
    - [Release Plugins in Separate Maven Profile](#release-plugins-in-separate-maven-profile)
4. [Bash Script for Versioning](#bash-script-for-versioning)
5. [GitHub Secrets](#github-secrets)
    - [GPG Key Pair](#gpg-key-pair)
    - [Maven Central Credentials](#maven-central-credentials)
6. [GitHub Actions](#github-actions)
    - [Test](#test)
    - [Publish](#publish)

## Prerequisites

This demonstration assumes that you are already registered at Maven Central (for example when you already have a 
package on it). Information about how to register at Maven Central can be found in the [official documentation](https://central.sonatype.org/publish/publish-guide/).

## Architecture

As an example, we created a simple [Java library for handling strings](string-array-utils/src/main/java/io/github/ackuq/StringArrayUtils.java) 
which we want to offer for other projects. To test changes in the source code automatically, we use JUnit 5 to perform 
[automated tests](string-array-utils/src/test/java/StringArrayUtilsTest.java) which are integrated into the Maven 
processes with the Maven Surefire plugin.

## Maven Project Set-up

Within the [`pom.xml`](string-array-utils/pom.xml) file, you can find the configuration for our project. Besides the 
[distribution management and other project details](https://help.sonatype.com/repomanager2/staging-releases/configuring-your-project-for-deployment), 
the Maven Source plugin, the Maven Javadoc plugin, the Maven GPG plugin and the Maven Staging Nexus plugin have to be 
present to automate the build process for our package.

### Maven Source Plugin

The Maven Source plugin is used to archive source files of the package together with the binaries when building, which 
is required by Maven Central. More information about this plugin can be found on [Apache's Maven website](https://maven.apache.org/plugins/maven-source-plugin/).

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

### Maven Javadoc Plugin

The Maven Javadoc plugin archives Javadoc files together with the binaries of the build, which is also required by 
Maven Central. More information about this plugin can be found on the [Apache Maven website](https://maven.apache.org/plugins/maven-javadoc-plugin/),

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

### Maven GPG Plugin

Since Maven Central requires you to have your packages signed with a [GPG key](https://en.wikipedia.org/wiki/GNU_Privacy_Guard), 
we add the Maven GPG plugin to automate the signing process. If you are unsure about how to get started with GPG 
signing, check out [this blog post](https://blog.sonatype.com/2010/01/how-to-generate-pgp-signatures-with-maven/).

To be able to pass command line options for the GPG passphrase, we need to add options for `--pinentry-mode` and 
`loopback`.

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

The Maven Staging Nexus plugin helps us to control the Nexus Staging workflow in which our artifacts get published. 
Since in this demo we want to release directly to production, we set the `autoReleaseAfterClose` option to `true`. This 
will result in our artifact being released directly to Maven Central when deployed.

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

### Release Plugins in Separate Maven Profile

Especially bigger projects might require more plugins and dependencies and developers should not be forced to always 
load the release plugins when they just perform a local build. We therefore created the Maven profile `release` which 
includes the release plugins only if this profile is activated in the Maven deploy command: `mvn -Prelease deploy`. A 
profile creation can look as follows.

```xml
<profiles>
    <!-- Profile activated for release to provide needed release plugins -->
    <profile>
      <id>release</id>
      <activation>
        <property>
          <name>release</name>
        </property>
      </activation>
      <build>
        <plugins>
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-source-plugin</artifactId>
            ...
          </plugin>
           ...
        </plugins>
      </build>
    </profile>
  </profiles>
```

## Bash Script for Versioning

To automate the semantic versioning and the creation of [GitHub releases](https://docs.github.com/en/github/administering-a-repository/about-releases),
we created a [bash script](string-array-utils/bump_version.sh). 

The script scrapes the last commit messages back to the one from the last release for keywords. In doing so, it 
determines the part of the semantic version which has to be increased in the [`pom.xml`](string-array-utils/pom.xml): 
`major(...)` results in increasing the major version , `minor(...)` or `feat(...)` results in increasing the minor 
version and the patch version gets increased if no keyword is detected. If the script is executed within a CI/CD system 
(e.g. GitHub Actions), the script will commit and push these version changes.

## GitHub Secrets

For performing the intended workflow of publishing a packaged version of our source code to Maven Central, we require 
different credentials. As the file of our GitHub Action will be and should be visible publicly, we store the required 
credentials as [GitHub secrets](https://docs.github.com/en/actions/reference/encrypted-secrets).

For deploying our packaged source code on Maven Central, we require two sets of credentials:
* GPG key pair with password
* Maven Central credentials

### GPG Key Pair

Maven Central requires us to sign the deployed package with a GPG key pair. For signing the package, we require the 
secret key and the passphrase to access the secret key. We then save these details as GitHub secrets. In our case, we 
used `OSSRH_GPG_SECRET_KEY` and `OSSRH_GPG_SECRET_KEY_PASSWORD` respectively. [This GitHub gist](https://gist.github.com/sualeh/ae78dc16123899d7942bc38baba5203c?fbclid=IwAR2bTMmf1Qs1UqiwOxQS9vSwIh-pyzPtQTtZJKlNwim6EmCPTRmTzD6kVEw) 
might give you an idea on how to retrieve these details.

### Maven Central Credentials

To authenticate against Maven Central, you will need the according credentials. Simply create a secret called 
`OSSRH_USERNAME`, which includes your username, and a secret called `OSSRH_PASSWORD`, which includes your password.

## GitHub Actions

With all the previously described steps in place, we can finally implement a workflow with GitHub Actions which 
automatically deploys a release of our packaged code to Maven Central.

At the beginning of every workflow, we have to mention what triggers its execution. For our use case, we want our 
workflow to be executed for every push to the branch `main`.

```yml
on:
  push:
    branches:
      - main
```

### Test

Before we want to publish a new release, we want to test our code with the mentioned JUnit tests. 

As the package should work for others with different Java versions, we define different version in a `matrix` within 
the `strategy` block. We then checkout the code and set up the different Java versions of the `adopt` distribution with 
`actions/setup-java`. For test efficiency when executing the workflow multiple times, we cache the dependency of the 
project before we finally run the tests with `mvn test`.

```yml
  test:
    name: Test Java code with different Java versions
    runs-on: ubuntu-20.04
    # Release only if last commit message contains keyword 'PUBLISH' (not case-sensitive)
    if: "contains(github.event.head_commit.message, 'PUBLISH')"
    strategy:
      matrix:
        java-version: [8, 11, 16]
    steps:
      - name: Checkout code
        uses: actions/checkout@v2

      - name: Set up Java with Maven
        uses: actions/setup-java@v2
        with:
          java-version: ${{ matrix.java-version }}
          distribution: 'adopt'

      - name: Cache Maven dependencies
        uses: actions/cache@v2
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-m2

      - name: Run tests
        working-directory: 'string-array-utils'
        run: |
          mvn \
            --no-transfer-progress \
            --batch-mode \
            clean test
```

The tests are then performed in a separate job for each Java version. However, the if statement ensures that the job 
starts only when the relevant commit message contains the keyword `PROCEDURE` (not case-sensitive).

### Publish

The eventual deployment to Maven Central happens within the job `publish`. This job only starts when the previous `test` 
job successfully finishes. 

Our bash script for bumping the version of the release automatically requires several commit messages which we specify 
when checking out the code. We then set up Java with the needed credentials to login at Maven Central (the used 
credential variables are populated later on with values from GitHub secrets). After again caching the dependencies, we 
install the secret GPG key with `echo "${{ secrets.OSSRH_GPG_SECRET_KEY }}" | gpg --batch --import`; the passphrase for 
this key is used later on when the key should be decrypted.

```yml
  publish:
    needs: test
    name: Bump Maven version, release on GitHub and Maven Central
    runs-on: ubuntu-20.04
    steps:
      - name: Checkout code
        uses: actions/checkout@v2
        with:
          # We need all tags and commits to be able to determine new bump type
          fetch-depth: 0

      - name: Set up Java with Maven
        uses: actions/setup-java@v2
        with:
          java-version: '11'
          distribution: 'adopt'
          server-id: ossrh
          server-username: MAVEN_USERNAME
          server-password: MAVEN_PASSWORD

      - name: Cache Maven dependencies
        uses: actions/cache@v2
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-m2

      - name: Install GPG secret key
        run: echo "${{ secrets.OSSRH_GPG_SECRET_KEY }}" | gpg --batch --import
```

As a second last step and last step, we run our script for bumping the correct semantic version and deploy our source 
code to Maven central. In this last step, we populate the variables of the Maven credentials and provide the passphrase 
required to decrypt our GPG secret key. We skip the execution of the JUnit tests because we already run the tests in the 
previous job `test`. In the [section about the required plugins](#maven-project-set-up), we described the `release` 
profile which we activate here.

```yml
      - name: Get latest version and update based on previous commits
        working-directory: 'string-array-utils'
        run: ./bump_version.sh

      - name: Publish to Maven Central
        working-directory: 'string-array-utils'
        run: |
          mvn \
            --no-transfer-progress \
            --batch-mode \
            -Dgpg.passphrase=${{ secrets.OSSRH_GPG_SECRET_KEY_PASSWORD }} \
            -DskipTests \
            -Prelease \
            deploy
        env:
          MAVEN_USERNAME: ${{ secrets.OSSRH_USERNAME }}
          MAVEN_PASSWORD: ${{ secrets.OSSRH_PASSWORD }}
```

Consult the file [`publish_to_maven_central.yml`](.github/workflows/publish_to_maven_central.yml) to see the whole 
workflow.

## Solution

We created a workflow with GitHub Actions which gets triggered on every push to the branch `main`. The first job tests 
ours source code with different Java versions. This job gets executed only on the keyword `PUBLISH` within the last 
commit message. 

The second job of the workflow gets only executed if the first job succeeded. It bumps a new version, creates a new 
release on GitHub and publishes the new version to Maven Central. The published package of our project can be found on 
Maven Central under the following link: [https://search.maven.org/artifact/io.github.ackuq/string-array-utils](https://search.maven.org/artifact/io.github.ackuq/string-array-utils).
When trying to publish your own package to Maven Central, you should not expect for it to appear to fast. Usually, it 
lasts a bit longer until packages appear on Maven Central.

## Copyright and License
Copyright © 2021, [Axel Pettersson](https://github.com/ackuq) and [Felix Seifert](https://www.felix-seifert.com/)

This demo is published under the short and permissive [MIT license](LICENSE). Basically, you can do whatever you want 
as long as you include the original copyright and license notice in any copy of the software/source.