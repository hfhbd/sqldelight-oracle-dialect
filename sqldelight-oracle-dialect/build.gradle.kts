plugins {
    kotlin("jvm")
    id("com.alecstrong.grammar.kit.composer")
    id("app.cash.licensee")
    id("publish")
    id("exclude")
    id("repos")
    id("java-test-fixtures")
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
    testImplementation(libs.sql.psi.env)
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
