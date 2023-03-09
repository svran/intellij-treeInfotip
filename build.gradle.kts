plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.1"
//    kotlin("jvm") version "1.2.30"
}

group = "com.svran.idea.plugin"
version = "1.1.0-SNAPSHOT"
//
//buildscript {
//    repositories { mavenCentral() }
//    dependencies { classpath(kotlin("gradle-plugin", "1.2.30")) }
//}

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform
    sandboxDir.set("D:/Android/idea-sandbox")
    downloadSources.set(false)
    plugins.set(listOf("org.jetbrains.kotlin"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("231.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
