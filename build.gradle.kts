import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.InventoryMarkdownReportRenderer
import com.github.jk1.license.render.ReportRenderer
import com.github.jk1.license.render.TextReportRenderer

plugins {
    jacoco
    id("com.github.jk1.dependency-license-report") version "2.0"
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

licenseReport {
    excludeOwnGroup = true
    excludeBoms = true
    renderers = arrayOf<ReportRenderer>(
        InventoryMarkdownReportRenderer(),
        TextReportRenderer(),
    )
    filters = arrayOf<DependencyFilter>(
        LicenseBundleNormalizer("$projectDir/license-normalizer-bundle.json", true)
    )
}
