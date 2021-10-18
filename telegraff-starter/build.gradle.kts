plugins {
    id("common-conventions")
    id("io.spring.dependency-management") version Plugins.springDepsMngVersion
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${Deps.springBootVersion}")
    }
}

dependencies {
    api(project(":telegraff-core"))
    api(project(":telegraff-autoconfigure"))

    implementation("org.springframework.boot:spring-boot-starter")
}
