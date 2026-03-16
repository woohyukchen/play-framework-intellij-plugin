import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.12.0"
}

group = "io.github.woohyukchen"
version = "2025.3.3"

providers.gradleProperty("playBuildDir").orNull?.let {
    layout.buildDirectory.set(file(it))
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaUltimate("2025.1.4.1")
        bundledPlugin("org.intellij.groovy")
        bundledPlugin("com.intellij.persistence")
        testFramework(TestFrameworkType.Platform)
        pluginVerifier()
    }
    testImplementation("junit:junit:4.13.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

sourceSets["main"].java {
    srcDir("src/main/gen")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }
        changeNotes =
            """
            <html>
              <body>
                <ul>
                  <li>Lowered the minimum supported IntelliJ Platform line to 251 after validation on IntelliJ IDEA Ultimate 2025.1.4.1.</li>
                  <li>Validated compatibility across IntelliJ IDEA Ultimate 2025.1.4.1 through 2025.3.3.</li>
                  <li>Replaced the removed PrattTokenType usage for newer IntelliJ Platform builds.</li>
                  <li>Fixed a 2025.3 runtime ClassCastException in persistence integration.</li>
                  <li>Added regression coverage for routes navigation and non-Play file safety boundaries.</li>
                  <li>Published as an unofficial community-maintained fork, independent from JetBrains releases.</li>
                </ul>
              </body>
            </html>
            """.trimIndent()
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.release.set(21)
    }
}
