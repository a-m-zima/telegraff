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

    // DevTools
    // TODO bug with kapt on windows
//    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // Telegraff
    implementation(project(":telegraff-core"))

    // Spring
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testImplementation(kotlin("test"))
}
