import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.2.0"
  id("org.jetbrains.intellij.platform") version "2.10.5"
  id("de.undercouch.download") version "5.7.0"
}

group = "com.niikelion.ic10-plugin-jetbrains"
version = "1.5"

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
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }

  withType<KotlinCompile> {
    kotlin {
      compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
      }
    }
  }

  named<ProcessResources>("processResources") {
    dependsOn(downloadEnums, downloadStationpedia)
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }

  compileKotlin {
    kotlin {
      compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
      }
    }
  }

  compileTestKotlin {
    kotlin {
      compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
      }
    }
  }
}

sourceSets["main"].java.srcDirs("src/main/gen")