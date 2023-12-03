plugins {
    kotlin("jvm")
    id("com.alecstrong.grammar.kit.composer")
    id("app.cash.licensee")
    id("publish")
    id("exclude")
    id("repos")
}

grammarKit {
    intellijRelease.set(libs.versions.idea)
}

dependencies {
    api(libs.sqldelight.dialect)
    compileOnly(libs.sqldelight.compilerEnv)

    testImplementation(kotlin("test"))
    testImplementation(libs.sqldelight.compilerEnv)
    testImplementation(testFixtures(libs.sql.psi))
}

kotlin {
    jvmToolchain(17)

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
