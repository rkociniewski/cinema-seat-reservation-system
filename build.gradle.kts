group = "rk.cinema"
version = "1.0-SNAPSHOT"

val javaVersion = JavaVersion.VERSION_21
graalvmNative.toolchainDetection = false

val flywayVersion: String by project
val testContainerVersion: String by project
val junitVersion: String by project
val lombokVersion: String by project

plugins {
    id("com.adarshr.test-logger")
    id("org.flywaydb.flyway")
    id("java")
    id("io.micronaut.application")
    id("com.gradleup.shadow")
    id("io.micronaut.aot")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.sql:micronaut-hibernate-jpa")
    implementation("io.micronaut.data:micronaut-data-tx-hibernate")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("org.flywaydb:flyway-core:$flywayVersion")

    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")
    compileOnly("io.micronaut:micronaut-http-client")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("io.micronaut.sql:micronaut-jdbc-hikari")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:$flywayVersion")
    runtimeOnly("org.postgresql:postgresql")

    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.testcontainers:postgresql:$testContainerVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.postgresql:postgresql")
}


application {
    mainClass = "rk.cinema.Application"
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.test {
    useJUnitPlatform()
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