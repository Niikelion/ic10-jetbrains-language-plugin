import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.2.0"
  id("org.jetbrains.intellij.platform") version "2.10.5"
  id("de.undercouch.download") version "5.7.0"
}

group = "com.niikelion.ic10-plugin-jetbrains"
version = "2.0"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

kotlin {
  jvmToolchain(21)
}

repositories {
  mavenCentral()

  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  intellijPlatform {
    intellijIdeaUltimate("2025.3")
    testFramework(TestFrameworkType.Platform)
    bundledPlugin("com.intellij.modules.json")
    bundledPlugin("org.jetbrains.plugins.yaml")
  }

  testImplementation("junit:junit:4.13.2")
  testImplementation(kotlin("test"))
}

val dataDir = layout.projectDirectory.dir("src/main/resources/data")

val downloadEnums by tasks.registering(Download::class) {
  src("https://github.com/aproposmath/StationeersStationpediaExtractor/releases/download/stable/enums.json")
  dest(dataDir.file("enums.json"))
  onlyIfModified(true)
}

val downloadStationpedia by tasks.registering(Download::class) {
  src("https://github.com/aproposmath/StationeersStationpediaExtractor/releases/download/stable/stationpedia.json")
  dest(dataDir.file("stationpedia.json"))
  onlyIfModified(true)
}

tasks {
  named<ProcessResources>("processResources") {
    dependsOn(downloadEnums, downloadStationpedia)
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
    hidden.set(true)
  }
}

sourceSets["main"].java.srcDirs("src/main/gen")