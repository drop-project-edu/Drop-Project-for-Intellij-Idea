plugins {
    id("java") // Java support
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.gradleIntelliJPlugin) // Gradle IntelliJ Plugin
    id("org.jetbrains.kotlinx.kover") version "0.7.3"
    id("com.google.devtools.ksp").version("1.9.10-1.0.13")

}

group = "org.dropProject"
version = "0.9.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.moshi:moshi:1.14.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
    implementation(group = "net.lingala.zip4j", name = "zip4j", version = "2.10.0")
    implementation("org.jetbrains:marketplace-zip-signer:0.1.8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.1")
    implementation("io.github.resilience4j:resilience4j-ratelimiter:2.1.0")

}
kotlin {
    jvmToolchain(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

intellij {
    pluginName = "Drop Project for IntelliJ Idea"
    version = "2022.3.3"
    type = "IC"
}

koverReport {
    defaults {
        xml {
            onCheck = true
        }
    }
}

tasks {

    wrapper {
        gradleVersion = "8.3"
    }

    patchPluginXml {
        version = "0.9.3"
        sinceBuild.set("223")
        untilBuild.set("232.*")

        /*pluginDescription = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with (it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHtml)
            }*/
    }

    /*compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }*/

    runIdeForUiTests {
        systemProperty("robot-server.port", "8082")
        systemProperty("ide.mac.message.dialogs.as.sheets", "false")
        systemProperty("jb.privacy.policy.text", "<!--999.999-->")
        systemProperty("jb.consents.confirmation.enabled", "false")
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