group = "rk.cinema"
version = "1.0-SNAPSHOT"

val javaVersion = JavaVersion.VERSION_21
graalvmNative.toolchainDetection = false

val jacksonVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val lombokVersion: String by project

plugins {
    id("com.adarshr.test-logger")
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
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    implementation("io.micronaut.serde:micronaut-serde-jackson")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

    compileOnly("io.micronaut:micronaut-http-client")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")

    runtimeOnly("ch.qos.logback:logback-classic")

    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

//tasks.named("dockerfileNative") {
//    jdkVersion = "21"
//}
//
//
