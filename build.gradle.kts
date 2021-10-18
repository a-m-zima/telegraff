plugins {
    jacoco
}

repositories {
    mavenCentral()
}

tasks.register<JacocoReport>("jacocoRootReport") {
    subprojects {
        this@subprojects.plugins.withType<JacocoPlugin>().configureEach {
            this@subprojects.tasks.matching {
                val extension = it.extensions.findByType<JacocoTaskExtension>()
                extension?.isEnabled ?: false
            }.configureEach {
                sourceSets(this@subprojects.the<SourceSetContainer>().named("main").get())
                executionData(this)
            }
        }
    }

    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}
