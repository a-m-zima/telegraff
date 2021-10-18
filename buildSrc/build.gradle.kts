plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    val kotlinVersion: String by project
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("reflect", kotlinVersion))
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation("org.jetbrains.dokka", "dokka-gradle-plugin", kotlinVersion)
    implementation(
        "org.jetbrains.kotlin.plugin.spring",
        "org.jetbrains.kotlin.plugin.spring.gradle.plugin",
        kotlinVersion
    )
}
