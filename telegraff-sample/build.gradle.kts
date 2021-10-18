plugins {
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
}

group = "me.ruslanys"
version = "1.0.0"


kotlin{
    jvmToolchain{
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of("11"))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Spring
    implementation("com.github.xzima.telegraff:telegraff-starter:1.0.0")
    implementation("org.springframework.boot:spring-boot-starter")
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.vintage:junit-vintage-engine")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
