plugins {
    id("java")
    id("java-library")
}

group = "net.defade"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("org.slf4j:slf4j-api:2.0.9")
}