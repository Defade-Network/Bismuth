plugins {
    id("java")
    id("maven-publish")
}

group = "net.defade.bismuth"
version = "1.0"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    group = rootProject.group
    version = rootProject.version

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        withSourcesJar()
        withJavadocJar()
    }

    configure<PublishingExtension> {
        publications.create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    publishing {
        repositories {
            maven("https://repo.defade.net/defade") {
                name = "defade"
                credentials(PasswordCredentials::class)
            }
        }
    }

    repositories {
        mavenCentral()
    }
}
