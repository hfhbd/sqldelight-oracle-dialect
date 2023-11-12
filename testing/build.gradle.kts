plugins {
    kotlin("jvm")
    id("app.cash.sqldelight")
}

kotlin.jvmToolchain(8)

dependencies {
    implementation(libs.sqldelight.jdbcDriver)
    implementation(libs.oracle.driver)

    testImplementation(kotlin("test"))
    testImplementation(libs.testcontainers)
    testRuntimeOnly(libs.logback)
}

sqldelight {
    databases.register("TestingDB") {
        dialect(projects.sqldelightOracleDialect)
        packageName.set("app.softwork.sqldelight.oracledialect")
    }
}
