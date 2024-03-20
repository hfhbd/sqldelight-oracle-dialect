plugins {
    kotlin("jvm")
    id("app.cash.sqldelight")
}

kotlin.jvmToolchain(8)

dependencies {
    implementation(projects.sqldelightOracleJdbc)

    testImplementation(kotlin("test"))
    testImplementation(libs.testcontainers)
    testImplementation(libs.oracle.driver)
    testRuntimeOnly(libs.logback)
}

sqldelight {
    databases.register("TestingDB") {
        dialect(projects.sqldelightOracleDialect)
        packageName.set("app.softwork.sqldelight.oracledialect")
    }
}
