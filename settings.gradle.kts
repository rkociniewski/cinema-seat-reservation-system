rootProject.name = "csrs"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
    }

    val flywayId: String by settings
    val flywayVersion: String by settings

    val micronautPluginId: String by settings
    val micronautPluginVersion: String by settings

    val shadowId: String by settings
    val shadowVersion: String by settings

    val testLoggerId: String by settings
    val testLoggerVersion: String by settings

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace?.startsWith(micronautPluginId) == true) {
                useVersion(micronautPluginVersion)
            }

            when (requested.id.id) {
                flywayId -> useVersion(flywayVersion)
                shadowId -> useVersion(shadowVersion)
                testLoggerId -> useVersion(testLoggerVersion)
            }
        }
    }
}