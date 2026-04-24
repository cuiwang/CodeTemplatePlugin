plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "com.cuiwang"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("IC", "2025.1.4.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

      // Add necessary plugin dependencies for compilation here, example:
      // bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        version = project.version.toString()
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
            <h3>1.0.0</h3>
            <ul>
              <li>Initial release of Code Template plugin.</li>
              <li>Support creating template from editor selection.</li>
              <li>Support right-click insert menu and quick insert popup.</li>
              <li>Support template management and JSON import/export in Settings.</li>
            </ul>
        """.trimIndent()
    }

    signing {
        certificateChain = providers.gradleProperty("pluginCertificateChain")
            .orElse(providers.environmentVariable("PLUGIN_CERTIFICATE_CHAIN"))
        privateKey = providers.gradleProperty("pluginPrivateKey")
            .orElse(providers.environmentVariable("PLUGIN_PRIVATE_KEY"))
        password = providers.gradleProperty("pluginPrivateKeyPassword")
            .orElse(providers.environmentVariable("PLUGIN_PRIVATE_KEY_PASSWORD"))
    }

    publishing {
        token = providers.gradleProperty("jetbrainsMarketplaceToken")
            .orElse(providers.environmentVariable("JETBRAINS_MARKETPLACE_TOKEN"))
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
