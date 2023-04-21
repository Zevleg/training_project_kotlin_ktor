import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*plugins {
    //kotlin("jvm") version "1.7.21"
    //id("org.jetbrains.kotlin.jvm") version "1.7.21"
    //application
}*/

@Suppress("DSL_SCOPE_VIOLATION") plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    alias(libs.plugins.serialization.ktor)
    //kotlin("plugin.serialization") version "1.8.10"
    alias(libs.plugins.ktor.plugin)
    //alias(libs.plugins.detekt.jvm)
    //alias(libs.plugins.spotless.jvm)
}

/*spotless {
    kotlin {
        ktfmt()
        ktlint()
    }
    java {
        removeUnusedImports()
        googleJavaFormat()
    }
    scala {
        scalafmt()
    }
}*/

group = "org.xebia.functional"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven {
        url = uri("https://pkgs.dev.azure.com/47degrees/_packaging/47deg/maven/v1")
        credentials {
            username = "47degrees"
            password = System.getenv("MAVEN_ACCESS_TOKEN") ?: "${project.properties["MAVEN_ACCESS_TOKEN"]}"
        }
    }
}

dependencies {
    testImplementation(kotlin("test"))
    //implementation("io.arrow-kt:arrow-core:1.1.2")
    //implementation(libs.arrow.core)
    //implementation(libs.arrow.optics)
    //implementation(libs.arrow.fx.coroutines)
    implementation(libs.bundles.arrow)

    implementation("io.ktor:ktor-server-core:2.2.1")
    implementation("io.ktor:ktor-server-netty:2.2.1")
    implementation("io.ktor:ktor-server-content-negotiation:2.2.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("io.ktor:ktor-server-html-builder:2.2.1")
    implementation("io.ktor:ktor-server-resources:2.2.1")
    implementation("io.ktor:ktor-server-auto-head-response:2.2.1")
    implementation("io.ktor:ktor-server-default-headers:2.2.1")
    implementation("io.ktor:ktor-server-sessions:2.2.1")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("io.ktor:ktor-client-core:2.2.1")
    implementation("io.ktor:ktor-client-cio:2.2.1")
    implementation("io.ktor:ktor-client-resources:2.2.1")
    implementation("io.ktor:ktor-client-content-negotiation:2.2.1")
    implementation("io.ktor:ktor-server-metrics-micrometer:2.2.1")
    implementation("io.micrometer:micrometer-registry-jmx:1.10.2")
    implementation("io.ktor:ktor-server-auth:2.2.1")
    implementation("io.ktor:ktor-server-auth-jwt:2.2.1")
    implementation("io.ktor:ktor-client-auth:2.2.1")
    implementation("io.ktor:ktor-server-test-host:2.2.1")

    implementation("guru.zoroark.tegral:tegral-openapi-ktor:0.0.3")
    implementation("guru.zoroark.tegral:tegral-openapi-ktorui:0.0.3")

    implementation("com.sksamuel.hoplite:hoplite-core:2.7.3")
    //implementation("com.sksamuel.hoplite:hoplite-hocon:2.5.2")
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.7.4")
}

tasks.test {
    useJUnitPlatform()
}

/*tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}*/

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

val dockerBuild = tasks.register<Exec>("dockerBuild") {
    group = "containers"
    commandLine(
        "docker",
        "build",
        "-t",
        "training-project",
        "."
    )
}

val launchPostgres = tasks.register<Exec>("launchHealthCheck") {
    group = "containers"
//  workingDir = file("${project.buildDir}/docker")
    commandLine(
        "docker",
        "run",
        //"--rm",
        "--name",
        "example-health-check",
        "-p",
        "8080:8080",
        "-d",
        "training-project"
    )
}

application {
    mainClass.set("MainKt")
}