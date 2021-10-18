plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    // TODO try to replace with Dependabot
    id("name.remal.check-dependency-updates")
}

group = "com.github.xzima.telegraff"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(Jvm))
    }
    withSourcesJar()
}
kapt {
    includeCompileClasspath = false
}
tasks {
    test {
        useJUnitPlatform()
    }
}

val dokkaJavadocJar by tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(dokkaJavadocJar)
        }
    }
}
