import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "rk.cinema"
version = "1.0-SNAPSHOT"

val mockitoAgent = configurations.create("mockitoAgent")

val javaVersion = JavaVersion.VERSION_21
graalvmNative.toolchainDetection = false

plugins {
    alias(libs.plugins.test.logger)
    alias(libs.plugins.flyway)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.shadow)
    alias(libs.plugins.micronaut.application)
    alias(libs.plugins.micronaut.aot)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.detekt)
    application
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    ksp("io.micronaut.openapi:micronaut-openapi")

    detektPlugins(libs.detekt)

    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.sql:micronaut-hibernate-jpa")
    implementation("io.micronaut.data:micronaut-data-tx-hibernate")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation(libs.flyway)

    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")
    compileOnly("io.micronaut:micronaut-http-client")

    mockitoAgent(libs.mockito) { isTransitive = false }

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("io.micronaut.sql:micronaut-jdbc-hikari")
    runtimeOnly(libs.flyway.postgres)
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.yaml:snakeyaml")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation(libs.junit.params)
    testImplementation(kotlin("test"))
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation(platform(libs.junit.jupiter))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.mockito) { isTransitive = false }
    testImplementation(libs.testcontainers)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly(libs.postgres)
}

application {
    mainClass = "rk.cinema.Application"
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

detekt {
    config.setFrom("$projectDir/detekt.yml")
    autoCorrect = true
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = JvmTarget.JVM_21.target
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = JvmTarget.JVM_21.target
}

dokka {
    dokkaSourceSets.main {
        jdkVersion.set(
            java.targetCompatibility.toString().toInt()
        ) // Used for linking to JDK documentation
        skipDeprecated.set(false)
    }

    pluginsConfiguration.html {
        dokkaSourceSets {
            configureEach {
                documentedVisibilities.set(
                    setOf(
                        VisibilityModifier.Public,
                        VisibilityModifier.Private,
                        VisibilityModifier.Protected,
                        VisibilityModifier.Internal,
                        VisibilityModifier.Package,
                    )
                )
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

tasks.javadoc {
    isFailOnError = false
    options.encoding = "UTF-8"
    source = sourceSets["main"].allJava
    (options as StandardJavadocDocletOptions).apply {
        addBooleanOption("Xdoclint:none", true)
        addStringOption("charset", "UTF-8")
        addStringOption("docencoding", "UTF-8")
    }
}

testlogger {
    showStackTraces = false
    slowThreshold = 10000
    showSimpleNames = true
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("rk.cinema.*")
    }
    aot {
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}