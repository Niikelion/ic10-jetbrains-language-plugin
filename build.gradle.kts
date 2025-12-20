import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.2.0"
  id("org.jetbrains.intellij.platform") version "2.10.5"
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
  }
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }

  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlin {
      compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
      }
    }
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