plugins {
    kotlin("jvm")
    id("app.cash.licensee")
    id("publish")
}

dependencies {
    implementation(libs.sqldelight.jdbcDriver)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(8)

    explicitApi()

    target.compilations.configureEach {
        kotlinOptions.allWarningsAsErrors = true
    }

    sourceSets.configureEach {
        languageSettings.progressiveMode = true
    }
}

licensee {
    allow("Apache-2.0")
}
