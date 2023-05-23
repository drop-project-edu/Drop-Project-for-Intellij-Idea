plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.13.3"
    kotlin("kapt") version "1.7.21"
}

group = "com.tfc.ulht"
version = "v0.9"

repositories {
    mavenCentral()
}



dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.moshi:moshi:1.14.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
    implementation(group = "net.lingala.zip4j", name = "zip4j", version = "2.10.0")
    implementation("org.jetbrains:marketplace-zip-signer:0.1.8")

}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
java {
    sourceCompatibility = JavaVersion.VERSION_17
}
intellij {
    version.set("2022.2.5")
}
tasks {

    patchPluginXml {
        version.set("${project.version}")
        sinceBuild.set("222")
        untilBuild.set("231.*")
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
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