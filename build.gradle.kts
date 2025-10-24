import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.micronaut.gradle.docker.NativeImageDockerfile
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/**
 * artifact group
 */
group = "rk.powermilk"

/**
 * project version
 */
version = "1.3.7"

val javaVersion: JavaVersion = JavaVersion.VERSION_21
val jvmTargetVersion = JvmTarget.JVM_21.target

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.flyway)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.open)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.micronaut.aot)
    alias(libs.plugins.micronaut.application)
    alias(libs.plugins.micronaut.library)
    alias(libs.plugins.shadow)
    alias(libs.plugins.test.logger)
    application
    jacoco
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

// dependencies
dependencies {
    kapt("io.micronaut:micronaut-inject-kotlin")
    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.micrometer:micronaut-micrometer-annotation")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("io.micronaut.serde:micronaut-serde-processor")

    detektPlugins(libs.detekt)

    implementation("ch.qos.logback:logback-classic")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.data:micronaut-data-tx")
    implementation("io.micronaut.data:micronaut-data-tx-hibernate")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.micrometer:micronaut-micrometer-core")
    implementation("io.micronaut.micrometer:micronaut-micrometer-registry-prometheus")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("io.micronaut:micronaut-http-server")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation(libs.flyway)
    implementation(libs.kotlinx)

    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")

    runtimeOnly(libs.flyway.postgres)
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.yaml:snakeyaml")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation(libs.junit.params)
    testImplementation(libs.test.junit5)
    testImplementation(platform(libs.junit.jupiter))
    testImplementation("org.apache.commons:commons-compress:1.27.1")
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:testcontainers")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly(libs.postgres)
}

application {
    mainClass = "rk.powermilk.cinema.Application"
}

allOpen {
    annotations(
        "jakarta.inject.Singleton",
        "jakarta.transaction.Transactional",
        "io.micronaut.context.annotation.Factory",
        "io.micronaut.http.annotation.Controller",
        "io.micronaut.http.annotation.Filter",
        "io.micronaut.aop.Around",
        "io.micronaut.validation.Validated"
    )
    preset("micronaut")
}


testlogger {
    showStackTraces = false
    showFullStackTraces = false
    showCauses = false
    slowThreshold = 10000
    showSimpleNames = true
}

kotlin {
    compilerOptions {
        verbose = true // enable verbose logging output
        jvmTarget.set(JvmTarget.fromTarget(jvmTargetVersion)) // target version of the generated JVM bytecode
    }
}

detekt {
    source.setFrom("src/main/kotlin")
    config.setFrom("$projectDir/detekt.yml")
    autoCorrect = true
}

dokka {
    dokkaSourceSets.main {
        jdkVersion.set(java.targetCompatibility.toString().toInt()) // Used for linking to JDK documentation
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

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("rk.powermilk.*")
    }
    aot {
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = false
    }
}

tasks.test {
    jvmArgs("-XX:+EnableDynamicAgentLoading")
    useJUnitPlatform()
    failOnNoDiscoveredTests = false
    finalizedBy(tasks.jacocoTestReport)
}


tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.75".toBigDecimal()
            }
        }

        rule {
            enabled = true
            element = "CLASS"
            includes = listOf("rk.*")

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.75".toBigDecimal()
            }
        }
    }
}

tasks.register("cleanReports") {
    doLast {
        delete("${layout.buildDirectory}/reports")
    }
}

tasks.register("coverage") {
    dependsOn(tasks.test, tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = jvmTargetVersion
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = jvmTargetVersion
}

tasks.named<NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
}
