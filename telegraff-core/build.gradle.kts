plugins {
    jacoco
    id("common-conventions")
    kotlin("plugin.spring")
    id("io.spring.dependency-management") version Plugins.springDepsMngVersion
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${Deps.springBootVersion}")
    }
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("script-util"))
    implementation(kotlin("compiler-embeddable"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testImplementation(kotlin("test"))
}
