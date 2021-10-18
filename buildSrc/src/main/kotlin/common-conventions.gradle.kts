plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.dokka")
    `java-library`
    `maven-publish`
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
